import sbt._

object Deps {

  val scalaLoggingVersion = "3.9.2"
  val scalaTestVersion = "3.0.4"
  val slickVersion = "3.3.3"
  val slickJodaMapperVersion = "2.5.0"
  val scalaForkLiftVersion = "0.3.1"
  val flywayVersion = "7.14.0"
  val slickMigrationFlywayVersion = "0.8.1"
  val playVersion = "2.8.1"
  val silhouetteVersion = "7.0.0"
  val playMailerVersion = "8.0.0"
  val oauthVersion = "1.5.0"
  val ficusVersion = "1.4.7"
  val mysqlVersion = "8.0.17"
  val postgresqlVersion = "42.1.4"

  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion
  lazy val scalariform = "org.scalariform" %% "scalariform" % "0.2.10"
  lazy val slick = "com.typesafe.slick" %% "slick" % slickVersion
  lazy val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
  lazy val jodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % slickJodaMapperVersion
  lazy val jodaTime = "joda-time" % "joda-time" % "2.7"
  lazy val jodaConvert = "org.joda" % "joda-convert" % "1.7"
  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.4"
  lazy val mysql = "mysql" % "mysql-connector-java" % mysqlVersion
  lazy val mysql = "mysql" % "mysql-connector-java" % mysqlVersion
  lazy val postgresql =  "org.postgresql" % "postgresql" % postgresqlVersion
  lazy val play = "com.typesafe.play" %% "play" % playVersion
  lazy val silhouette = "com.mohiva" %% "play-silhouette" % "6.1.0"
  lazy val silhouetteBcrypt = "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion
  lazy val silhouettePersistence = "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion
  lazy val silhouetteCrypto = "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion
  lazy val silhouetteTotp = "com.mohiva" %% "play-silhouette-totp" % silhouetteVersion
  lazy val scalaTestPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test
  lazy val scalaForkLift = "com.liyaos" %% "scala-forklift-slick" % scalaForkLiftVersion
  lazy val flyway = "org.flywaydb" %% "flyway-play" % flywayVersion
  lazy val slickMigrationFlyway = "io.github.nafg.slick-migration-api" %% "slick-migration-api-flyway" % slickMigrationFlywayVersion
  lazy val playMailer = "com.typesafe.play" %% "play-mailer" % playMailerVersion
  lazy val playMailerGuice = "com.typesafe.play" %% "play-mailer-guice" % playMailerVersion
  lazy val scalaOauthCore = "com.nulab-inc" %% "scala-oauth2-core" % oauthVersion
  lazy val playOauth2Provider = "com.nulab-inc" %% "play2-oauth2-provider" % oauthVersion
  lazy val ficus = "com.iheart" %% "ficus" % ficusVersion
}
