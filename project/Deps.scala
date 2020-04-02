import sbt._

object Deps {
  val avroVersion = "1.8.2"
  val kafkaVersion = "2.3.1"
  val kafkaStreamsScalaVersion = "0.1.0"
  val scalaLoggingVersion = "3.9.2"
  val scalaTestVersion = "3.0.4"
  val avroSerializerVersion = "4.0.0"
  val slickVersion = "3.3.2"
  val slickJodaMapperVersion = "2.4.0"
  val scalaForkLiftVersion = "0.3.1"
  val flywayVersion = "5.4.0"
  val slickMigrationFlywayVersion = "0.6.0"
  val mqttVersion = "1.2.2"
  val playVersion = "2.7.3"
  val silhouetteVersion = "6.1.0"
  val playMailerVersion = "8.0.0"
  val oauthVersion = "1.5.0"

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
  lazy val jodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperVersion
  lazy val jodaTime = "joda-time" % "joda-time" % "2.7"
  lazy val jodaConvert = "org.joda" % "joda-convert" % "1.7"
  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.4"
  lazy val h2 = "com.h2database" % "h2" % "1.4.191"
  lazy val mysql = "mysql" % "mysql-connector-java" % "5.1.47"
  lazy val avro4s = "com.sksamuel.avro4s" %% "avro4s-core" % "2.0.4"
  lazy val mqtt = "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % mqttVersion
  lazy val play = "com.typesafe.play" %% "play" % playVersion
  lazy val silhouette = "com.mohiva" %% "play-silhouette" % "6.1.0"
  lazy val silhouetteBcrypt = "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion
  lazy val silhouettePersistence = "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion
  lazy val silhouetteCrypto = "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion
  lazy val silhouetteTotp = "com.mohiva" %% "play-silhouette-totp" % silhouetteVersion
  lazy val scalaTestPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
  lazy val scalaForkLift = "com.liyaos" %% "scala-forklift-slick" % scalaForkLiftVersion
  lazy val flyway = "org.flywaydb" %% "flyway-play" % flywayVersion
  lazy val slickMigrationFlyway = "io.github.nafg" %% "slick-migration-api-flyway" % slickMigrationFlywayVersion
  lazy val ficus = "com.iheart" %% "ficus" % "1.4.7"
  lazy val bs3 = "com.adrianhurt" %% "play-bootstrap3" % "1.5.1-P27-B3"
  lazy val playMailer = "com.typesafe.play" %% "play-mailer" % playMailerVersion
  lazy val playMailerGuice = "com.typesafe.play" %% "play-mailer-guice" % playMailerVersion
  lazy val scalaOauthCore = "com.nulab-inc" %% "scala-oauth2-core" % oauthVersion
  lazy val playOauth2Provider = "com.nulab-inc" %% "play2-oauth2-provider" % oauthVersion
}
