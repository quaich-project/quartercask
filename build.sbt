val projectVersion          = "0.1-SNAPSHOT"
val projectOrg              = "codes.bytes"
val awsSdkVersion           = "1.10.77"
val json4sVersion           = "3.4.0"


lazy val commonSettings = Seq(
  organization := projectOrg,
  version := projectVersion,
  retrieveManaged := true,
  libraryDependencies ++= Seq(
    "org.json4s"    %% "json4s-jackson"      % json4sVersion,
    "com.amazonaws"  % "aws-java-sdk-iam"    % awsSdkVersion,
    "com.amazonaws"  % "aws-java-sdk-lambda" % awsSdkVersion,
    "com.amazonaws"  % "aws-java-sdk-s3"     % awsSdkVersion
  ),
  retrieveManaged := true,
  scalacOptions := Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.7",
    "-deprecation",
    "-language:_"
  ),
  fork in (Test, run) := true,
  addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")
)


lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "sbt-quartercask"
  ).
  aggregate(
    lambda, util, apiGateway, iot, alexaSkills
  )

lazy val lambda = (project in file("lambda")).
  settings(commonSettings: _*).
  settings(
    name := "sbt-quartercask-lambda",
    sbtPlugin := true
  ).
  dependsOn(util)

lazy val util = (project in file("util")).
  settings(commonSettings: _*).
  settings(
    name := "sbt-quartercask-util",
    sbtPlugin := true
  )

lazy val apiGateway = (project in file("api-gateway")).
  settings(commonSettings: _*).
  settings(
    name := "sbt-quartercask-api-gateway",
    sbtPlugin := true
  ).
  dependsOn(util)

lazy val iot = (project in file("iot")).
  settings(commonSettings: _*).
  settings(
    name := "sbt-quartercask-iot",
    sbtPlugin := true
  ).
  dependsOn(util)

lazy val alexaSkills = (project in file("alexa-skills")).
  settings(commonSettings: _*).
  settings(
    name := "sbt-quartercask-alexa-skills",
    sbtPlugin := true
  ).
  dependsOn(util)
