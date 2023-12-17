import sbt.*

object Dependencies {
  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.2.15" % Test
  )


  lazy val core = Seq(
    // logging
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    "ch.qos.logback" % "logback-classic" % "1.4.7",
    // Apache Spark dependencies
    "org.apache.spark" %% "spark-core" % "3.3.2" % "provided",
    "org.apache.spark" %% "spark-sql" % "3.3.2" % "provided",
    "org.apache.spark" %% "spark-streaming" % "3.3.2" % "provided",
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.3.2" % "provided",
    // Apache Kafka dependency
    "org.apache.kafka" % "kafka-clients" % "3.4.0"
  )
}
