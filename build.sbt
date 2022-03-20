name := "blending_engine"

version := "0.1"

scalaVersion := "2.13.8"

val AkkaVersion = "2.6.18"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson"  % AkkaVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all"       % "1.8",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test
)
