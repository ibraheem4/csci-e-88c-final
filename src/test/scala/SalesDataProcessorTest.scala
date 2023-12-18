import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.functions.{count, sum, avg, lit}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SalesDataProcessorTest extends AnyFunSuite with Matchers {
  val spark: SparkSession = SparkSession.builder()
    .appName("SalesDataProcessorTest")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  test("SalesDataProcessor should correctly aggregate order totals by tender") {
    // Sample data
    val sampleData = Seq(
      "16-Dec-2023,123,,Dine In,1YPCYR0YP57K4,Alex,,,,USD,3.56,6.04,,0.00,33.56,33.56,,0.00,0.00,Credit Card,,,,Paid",
      "16-Dec-2023,124,,Dine In,1YPCYR0YP57K4,Alex,,,,USD,1.43,0.00,,0.00,13.43,13.43,,0.00,0.00,Debit Card,,,,Paid"
    ).toDF("value")

    // Applying the same transformations as in SalesDataProcessor
    val salesData = sampleData
      .flatMap { row =>
        val line = row.getString(0)
        val fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)
        try {
          Some((fields(19), fields(14).toDouble))
        } catch {
          case _: ArrayIndexOutOfBoundsException => None
        }
      }
      .toDF("Tender", "OrderTotal")

    val statsByTender = salesData.groupBy("Tender")
      .agg(
        count(lit(1)).alias("TotalOrders"),  // Count the number of orders
        sum("OrderTotal").alias("TotalOrderTotal"),
        avg("OrderTotal").alias("AverageOrderTotal")
      )

    // Collect results and assert
    val results = statsByTender.collect()

    results should contain theSameElementsAs Seq(
      Row("Credit Card", 1L, 33.56, 33.56),
      Row("Debit Card", 1L, 13.43, 13.43)
    )
  }
}
