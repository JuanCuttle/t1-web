name := """t1"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(ws, guice)
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.6.5"
libraryDependencies ++= Seq(guice, "org.mongodb.scala" %% "mongo-scala-driver" % "2.1.0")

routesGenerator := InjectedRoutesGenerator
