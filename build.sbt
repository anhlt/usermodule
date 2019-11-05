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
          guice,
          slick,
          slickHikaricp,
          jdbc,
          h2,
          mysql,
          scalaTestPlay,
          flyway,
          slickMigrationFlyway
        )
      )
    )
  )
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "h3ck3rn3w.io.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "h3ck3rn3w.io.binders._"
