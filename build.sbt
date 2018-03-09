import sbt._
import BuildConfig.Dependencies

lazy val commonSettings = BuildConfig.commonSettings(currentVersion = "1.0")

commonSettings

name := "scelastic"

libraryDependencies ++= Seq(
  "org.typelevel" %% "macro-compat" % "1.1.1",
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.patch),
  "com.curalate" %% "json-jackson" % "1.3-29-6380452"
) ++ Dependencies.testDeps

scalacOptions in (Compile, console) += "-Yrepl-sync"

initialCommands :=
  """
    |import com.chrisbenincasa.scelastic._
    |import com.chrisbenincasa.scelastic.queries._
    |import com.chrisbenincasa.scelastic.params._
    |import com.chrisbenincasa.json.jackson._
    |val serializer = new JacksonSerializer()
  """.stripMargin

lazy val showVersion = taskKey[Unit]("Show version")

showVersion := {
  println(version.value)
}

// custom alias to hook in any other custom commands
addCommandAlias("build", "; compile")