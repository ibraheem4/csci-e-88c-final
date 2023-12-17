import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object SalesDataProcessor extends App {
  val spark = SparkSession.builder.appName("SalesDataAnalysis").master("local[*]").getOrCreate()
  import spark.implicits._

  val bootstrapServers = "localhost:9092"
  val subscribeType = "subscribe"
  val topic = "sales_data_topic"

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", bootstrapServers)
    .option(subscribeType, topic)
    .load()

  val salesData = df.selectExpr("CAST(value AS STRING)").as[String]
    .map(_.split(","))
    .map(array => (array(0), array(1), array(2).toInt, array(3).toDouble))
    .toDF("Date", "Product", "Quantity", "Price")

  // Example processing: total sales per product
  val productSales = salesData.groupBy("Product").agg(sum("Price").alias("TotalSales"))

  val query = productSales.writeStream
    .outputMode("complete")
    .format("console")
    .start()

  query.awaitTermination()
}
