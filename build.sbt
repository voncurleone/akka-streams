ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.8.0"

lazy val root = (project in file("."))
  .settings(
    name := "akka streams",
    resolvers += "Maven Central" at "https://repo1.maven.org/maven2/",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.danielasfregola" %% "twitter4s" % "8.0"
    )
  )
