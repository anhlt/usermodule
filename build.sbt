import Deps._
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.12.15",
        name := """h3ck3rn3w""",
        organization := "h3ck3rn3w.io",
        version := "1.0",
        resolvers ++=
          Seq(
            "Atlassian's Maven Public Repository" at "https://packages.atlassian.com/maven-public/"
          ),
        libraryDependencies ++= Seq(
          play,
          guice,
          slick,
          slickHikaricp,
          jodaMapper,
          jodaConvert,
          jodaTime,
          jdbc,
          mysql,
          scalaTestPlay,
          flyway,
          slickMigrationFlyway,
          silhouette,
          silhouetteBcrypt,
          silhouetteCrypto,
          silhouettePersistence,
          silhouetteTotp,
          playMailer,
          playMailerGuice,
          scalaOauthCore,
          playOauth2Provider,
          ficus
        )
      )
    ),
    Compile / run / fork := true,
    Compile/ run / javaOptions += "-Dhttp.address=0.0.0.0"
  )

routesGenerator := InjectedRoutesGenerator
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "h3ck3rn3w.io.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "h3ck3rn3w.io.binders._"
