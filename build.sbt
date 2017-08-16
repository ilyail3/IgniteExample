name := "IgniteExample"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.apache.ignite" % "ignite-core" % "2.1.0" % "provided",
  "org.apache.ignite" % "ignite-spring" % "2.1.0" % "provided"
)