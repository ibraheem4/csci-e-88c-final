import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.log4j.{Level, Logger}

object SalesDataProcessor extends App {
  Logger.getLogger("org").setLevel(Level.INFO)

  val spark = SparkSession.builder.appName("SalesDataAnalysis").master("local[*]").getOrCreate()
  import spark.implicits._

  val bootstrapServers = "localhost:9092"
  val topic = "sales_data_topic"

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", bootstrapServers)
    .option("subscribe", topic)
    .option("startingOffsets", "earliest") // Read from the earliest offset
    .load()

  val salesData = df.selectExpr("CAST(value AS STRING)").as[String]
    .map(_.split(","))
    .map(array => (array(0), array(1), array(2).toInt, array(3).toDouble))
    .toDF("Date", "Product", "Quantity", "Price")

  // Aggregate total quantity and average price per product
  val statsByProduct = salesData.groupBy("Product")
    .agg(
      sum("Quantity").alias("TotalQuantity"),
      avg("Price").alias("AveragePrice")
    )

  // Define a custom foreachBatch function to log the output of each micro-batch
  def logOutput(batchDF: org.apache.spark.sql.DataFrame, batchId: Long): Unit = {
    println(s"Batch ID: $batchId")
    if (batchDF.count() > 0) {
      println("Statistics per Product:")
      batchDF.show(false)
    } else {
      println("No new data in this batch.")
    }
  }

  // Output the results using foreachBatch for more control
  val query = statsByProduct.writeStream
    .outputMode("complete")
    .trigger(Trigger.ProcessingTime("5 seconds")) // Adjust the trigger interval as needed
    .foreachBatch(logOutput _)
    .start()

  query.awaitTermination()
}
