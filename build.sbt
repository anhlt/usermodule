import scala.tools.nsc.doc.html.Doclet
import Deps._
lazy val root = (project in file("."))
  .enablePlugins(PlayScala, JavaAppPackaging)
  .settings(
    ThisBuild / organization := "Helloe",
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
          ficus
        )
      )
    ),
    Compile / run / fork := true,
    Compile / run / javaOptions += "-Dhttp.address=0.0.0.0"
  )

lazy val localPackage = project
  .in(file("build/local"))
  .enablePlugins(PlayScala, JavaAppPackaging, SwaggerPlugin)
  .settings(
    swaggerDomainNameSpaces := Seq(
      "models",
      "forms",
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
    Docker / packageName := "user_module",
    ThisBuild / dockerRepository := sys.env.get("DOCKER_IMAGE_HOST"),
    ThisBuild / dockerUsername := sys.env.get("DOCKER_USERNAME"),
    Compile / resourceDirectory := (resourceDirectory in (root, Compile)).value,
    Universal / mappings += {
      ((Compile / resourceDirectory).value / "application.stg.conf") -> "conf/application.conf"
    }
  )
  .dependsOn(root)

lazy val prodPackage = project
  .in(file("build/prod"))
  .enablePlugins(PlayScala, JavaAppPackaging)
  .settings(
    Compile / resourceDirectory := (resourceDirectory in (root, Compile)).value,
    Universal / mappings += {
      ((Compile / resourceDirectory).value / "application.prod.conf") -> "conf/application.conf"
    }
  )
  .dependsOn(root)
