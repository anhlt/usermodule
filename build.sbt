import Deps._
resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("naftoligug", "maven")
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.12.10",
        name := """h3ck3rn3w""",
        organization := "h3ck3rn3w.io",
        version := "1.0",
        libraryDependencies ++= Seq(
          play,
          ficus,
          guice,
          slick,
          slickHikaricp,
          jodaMapper,
          jodaConvert,
          jodaTime,
          jdbc,
          h2,
          mysql,
          scalaTestPlay,
          flyway,
          mqtt,
          slickMigrationFlyway,
          silhouette,
          silhouetteBcrypt,
          silhouetteCrypto,
          silhouettePersistence,
          silhouetteTotp,
          playMailer,
          playMailerGuice,
          scalaOauthCore,
          playOauth2Provider
        )
      )
    ),
    fork in (Compile, run) := true,
    javaOptions in (Compile, run) += "-Dhttp.address=0.0.0.0"
  )

routesGenerator := InjectedRoutesGenerator
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "h3ck3rn3w.io.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "h3ck3rn3w.io.binders._"
