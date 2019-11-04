import sbt._

object Deps {
  val avroVersion = "1.8.2"
  val kafkaVersion = "2.3.1"
  val kafkaStreamsScalaVersion = "0.1.0"
  val scalaLoggingVersion = "3.9.2"
  val scalaTestVersion = "3.0.4"
  val avroSerializerVersion = "4.0.0"
  val slickVersion = "3.3.2"
  val scalaForkLiftVersion= "0.3.1"
  lazy val avro = "org.apache.avro" % "avro" % avroVersion
  lazy val avroSerializer = "io.confluent" % "kafka-avro-serializer" % avroSerializerVersion
  lazy val avroStreamsSerializer = "io.confluent" % "kafka-streams-avro-serde" % avroSerializerVersion
  lazy val kafkaClient = "org.apache.kafka" % "kafka-clients" % kafkaVersion
  lazy val kafkaStreams = "org.apache.kafka" % "kafka-streams" % kafkaVersion
  lazy val kafkaStreamsScala = "com.lightbend" %% "kafka-streams-scala" % kafkaStreamsScalaVersion
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
  lazy val scalariform = "org.scalariform" %% "scalariform" % "0.2.10"
  lazy val slick = "com.typesafe.slick" %% "slick" % slickVersion
  lazy val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.4"
  lazy val h2 = "com.h2database" % "h2" % "1.4.191"
  lazy val mysql = "mysql" % "mysql-connector-java" % "5.1.47"
  lazy val avro4s = "com.sksamuel.avro4s" %% "avro4s-core" % "2.0.4"

  lazy val scalaTestPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
  lazy val scalaForkLift = "com.liyaos" %% "scala-forklift-slick" % scalaForkLiftVersion
}
