import Deps._
lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SwaggerPlugin)
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.12.15",
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
          postgresql,
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
          ficus,
          swaggerUI
        )
      )
    ),
    Compile / run / fork := true,
    Compile / run / javaOptions += "-Dhttp.address=0.0.0.0"
  )

swaggerDomainNameSpaces := Seq("models", "forms", "com.mohiva.play.silhouette.api")
swaggerRoutesFile := "routes"
Assets / WebKeys.exportedMappings := Nil
routesGenerator := InjectedRoutesGenerator
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "h3ck3rn3w.io.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "h3ck3rn3w.io.binders._"
