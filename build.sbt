name := """flume-client"""

organization := "com.eyeem"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.10.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.play" %% "play-json" % "2.6.5",
  "com.github.pureconfig" %% "pureconfig" % "0.7.2",

  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test"
)

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-language:implicitConversions" //allow implicit conversions defined by implicit def convertAtoB(a:A):B type functions
)

lazy val root = (project in file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .configs(CustomIntegrationTest)
  .settings(inConfig(CustomIntegrationTest)(Defaults.testTasks): _*)

//*********************
//-- test settings --
//*********************

def integrationFilter(name: String): Boolean = name endsWith "ITSpec"

def unitFilter(name: String): Boolean = {
  (name endsWith "Spec") && !integrationFilter(name)
}

lazy val CustomIntegrationTest = config("it") extend Test

testOptions in Test := Seq(Tests.Filter(unitFilter))
testOptions in CustomIntegrationTest := Seq(Tests.Filter(integrationFilter))

//********************************************************
// publishing settings http://www.scala-sbt.org/0.13.5/docs/Detailed-Topics/Publishing.html
//********************************************************

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "EyeEm"),
  "conf" -> Apache2_0("2016", "EyeEm", "#")
)

licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

bintrayOrganization := Some("eyeem")

bintrayRepository := "maven"

crossScalaVersions := Seq("2.10.6", "2.11.8")
