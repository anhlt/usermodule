import Deps._

lazy val common = Project("common", file("libs/common")).settings()

lazy val deviceManager = Project("deviceManager", file("libs/device"))
  .dependsOn(common)
  .enablePlugins(PlayScala)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, JavaAppPackaging)
  .settings(
    ThisBuild / organization := "Helloe",
    inThisBuild(
      List(
        scalaVersion := "2.13.7",
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
          ficus
        ),
        scalacOptions ++= Seq(
          "-language:postfixOps"
        )
      )
    ),
    Compile / run / fork := true,
    Compile / run / javaOptions += "-Dhttp.address=0.0.0.0"
  )
  .dependsOn(common, deviceManager)
  .aggregate(common, deviceManager)

lazy val localPackage = project
  .in(file("build/local"))
  .enablePlugins(PlayScala, JavaAppPackaging, SwaggerPlugin)
  .settings(
    swaggerDomainNameSpaces := Seq(
      "models",
      "forms",
      "device.forms",
      "device.models.entities",
      "com.mohiva.play.silhouette.api"
    ),
    swaggerRoutesFile := "routes",
    Assets / WebKeys.exportedMappings := Nil,
    libraryDependencies ++= Seq(swaggerUI),
    routesGenerator := InjectedRoutesGenerator,
    Compile / resourceDirectory := (resourceDirectory in (root, Compile)).value,
    Universal / mappings += {
      ((Compile / resourceDirectory).value / "application.local.conf") -> "conf/application.conf"
    }
  )
  .dependsOn(root)

lazy val stagePackage = project
  .in(file("build/stage"))
  .enablePlugins(PlayScala, JavaAppPackaging, DockerPlugin)
  .settings(
    Docker / packageName := sys.env
      .get("DOCKER_REPOSITORY")
      .getOrElse("usermodule"),
    Docker / version := sys.env.get("DOCKER_VERSION").getOrElse("staging"),
    ThisBuild / dockerRepository := sys.env.get("DOCKER_IMAGE_HOST"),
    ThisBuild / dockerUsername := sys.env.get("DOCKER_USERNAME"),
    Compile / resourceDirectory := (resourceDirectory in (root, Compile)).value,
    Universal / mappings += {
      ((Compile / resourceDirectory).value / "application.stg.conf") -> "conf/application.conf"
    },
    Universal / javaOptions ++= Seq(
      "-Dpidfile.path=/dev/null"
    ),
    dockerBaseImage := "adoptopenjdk/openjdk11:debian-slim"
  )
  .dependsOn(root)
