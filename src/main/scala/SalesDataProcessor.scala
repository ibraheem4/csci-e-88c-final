import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.log4j.{Level, Logger}

object SalesDataProcessor extends App {
  Logger.getLogger("org").setLevel(Level.WARN)

  val spark = SparkSession.builder.appName("SalesDataAnalysis").master("local[*]").getOrCreate()
  import spark.implicits._

  val bootstrapServers = "localhost:9092"
  val topic = "sales_data_topic"

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", bootstrapServers)
    .option("subscribe", topic)
    .option("startingOffsets", "earliest")
    .load()

  val salesData = df.selectExpr("CAST(value AS STRING)").as[String]
    .flatMap { line =>
      try {
        val fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)
        Some((fields(19), fields(14).toDouble))
      } catch {
        case _: ArrayIndexOutOfBoundsException => None
      }
    }
    .toDF("Tender", "OrderTotal")

  val statsByTender = salesData.groupBy("Tender")
    .agg(
      count("OrderTotal").alias("TotalOrders"),
      avg("OrderTotal").alias("AverageOrderTotal")
    )

  def logOutput(batchDF: org.apache.spark.sql.DataFrame, batchId: Long): Unit = {
    println(s"Batch ID: $batchId")
    if (batchDF.count() > 0) {
      println("Order Type Statistics:")
      batchDF.show(false)
    } else {
      println("No new data in this batch.")
    }
  }

  val query = statsByTender.writeStream
    .outputMode("complete")
    .trigger(Trigger.ProcessingTime("5 seconds"))
    .foreachBatch(logOutput _)
    .start()

  query.awaitTermination()
}
