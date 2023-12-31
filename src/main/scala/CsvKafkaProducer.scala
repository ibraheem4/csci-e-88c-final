import java.util.Properties
import org.apache.kafka.clients.producer._
import scala.io.Source
import org.apache.log4j.{Level, Logger}

object CsvKafkaProducer extends App {
  Logger.getLogger("org").setLevel(Level.WARN)

  val bootstrapServers = "localhost:9092"
  val topic = "clover_sales_data"

  val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val bufferedSource = Source.fromFile("data/clover_sales_data_orders.csv")

  for (line <- bufferedSource.getLines.drop(1)) {
    val record = new ProducerRecord[String, String](topic, line)
    producer.send(record)
  }

  bufferedSource.close()
  producer.close()
}
