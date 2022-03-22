name := "blending_engine"

version := "0.1"

scalaVersion := "2.13.8"

val AkkaVersion = "2.6.18"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "ch.qos.logback" % "logback-classic" % "1.2.11"
)
