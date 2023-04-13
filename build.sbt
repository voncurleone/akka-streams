ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.8.0"

lazy val root = (project in file("."))
  .settings(
    name := "akka streams",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
  )
