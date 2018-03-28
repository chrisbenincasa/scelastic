import sbt._
import BuildConfig.Dependencies

lazy val commonSettings = BuildConfig.commonSettings(currentVersion = "1.0")

commonSettings

name := "scelastic"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "io.getquill" %% "quill-core" % "2.3.3" % "test",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.patch),
  "io.paradoxical" %% "paradox-scala-jackson" % "1.1"
) ++ Dependencies.testDeps

scalacOptions in (Compile, console) += "-Yrepl-sync"

initialCommands :=
  """
    |import com.chrisbenincasa.scelastic._
    |import com.chrisbenincasa.scelastic.queries._
    |import com.chrisbenincasa.scelastic.params._
    |import io.paradoxical.jackson._
    |val serializer = new JacksonSerializer()
  """.stripMargin

lazy val showVersion = taskKey[Unit]("Show version")

showVersion := {
  println(version.value)
}

// custom alias to hook in any other custom commands
addCommandAlias("build", "; compile")
