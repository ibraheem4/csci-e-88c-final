import java.util.Properties
import org.apache.kafka.clients.producer._
import scala.io.Source
import org.apache.log4j.{Level, Logger}

object CsvKafkaProducer extends App {
  Logger.getLogger("org").setLevel(Level.INFO)

  val bootstrapServers = "localhost:9092"
  val topic = "sales_data_topic"

  val props = new Properties()
  props.put("bootstrap.servers", bootstrapServers)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val bufferedSource = Source.fromFile("data/clover_sales_data.csv")

  for (line <- bufferedSource.getLines.drop(1)) { // Skipping header
    val record = new ProducerRecord[String, String](topic, line)
    producer.send(record)
    println(s"Sent data to Kafka: $line")
  }

  println("All data sent to Kafka")
  bufferedSource.close()
  producer.close()
}
