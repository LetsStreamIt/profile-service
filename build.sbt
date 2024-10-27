import scala.sys.process.*

fork := true

enablePlugins(JavaAppPackaging) 
dockerBaseImage := "openjdk:17"
dockerExposedPorts ++= Seq(sys.env.getOrElse("PORT", "8080").toInt)
dockerRepository := Some("ghcr.io/letsstreamit")

lazy val runOnLoad = taskKey[Unit]("Prepares git hooks using node")
lazy val akkaHttpVersion = "10.6.3"
lazy val akkaVersion = "2.9.6"
lazy val root = project
  .in(file("."))
  .settings(
    inThisBuild(
      List(
        scalaVersion := scala3Version
      )
    ),
    name := "profile-service",
    version := "latest",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-pki" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.12",
      "org.mongodb.scala" % "mongo-scala-driver_2.13" % "5.2.0",
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalamock" % "scalamock_3" % "6.0.0"
    ),
    semanticdbEnabled := true,
    scalacOptions += {
      if (scalaVersion.value.startsWith("2.12"))
        "-Ywarn-unused-import"
      else
        "-Wunused:imports"
    }
  )

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

Test / javaOptions += "-Dconfig.file=src/test/resources/application-test.conf"

runOnLoad := {
  // Run npm install silently
  val npmInstallResult = Process("npm install").run(ProcessLogger(_ => ()))

  // Wait for npm install to complete
  if (npmInstallResult.exitValue() != 0) {
    sys.error("npm install failed")
  }

  // Run npm run prepare silently
  val npmPrepareResult = Process("npm run prepare").run(ProcessLogger(_ => ()))

  // Wait for npm run prepare to complete
  if (npmPrepareResult.exitValue() != 0) {
    sys.error("npm run prepare failed")
  }
}

val scala3Version = "3.5.2"

onLoad in Global := {
  val previous = (onLoad in Global).value
  previous andThen { state =>
    val isCI = sys.env.get("CI").contains("true")
    if (!isCI) {
      Project.extract(state).runTask(runOnLoad, state)._1
    } else {
      state // Just return the state without running the task
    }
  }
}
