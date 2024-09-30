import sys.process._

lazy val runOnLoad = taskKey[Unit]("Prepares git hooks using node")

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

val scala3Version = "3.5.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Template-for-Scala3-Projects",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.1" % Test,
    semanticdbEnabled := true,
    scalacOptions += {
      if (scalaVersion.value.startsWith("2.12"))
        "-Ywarn-unused-import"
      else
        "-Wunused:imports"
    }
  )

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
