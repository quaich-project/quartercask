
name := "sbt-quartercask-awslambda"

version := projectVersion

organization := "codes.bytes"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")


val projectVersion = "0.1-SNAPSHOT"
val awsSdkVersion = "1.10.77"


logLevel := Level.Debug

sbtPlugin := true


retrieveManaged := true

scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.7",
  "-deprecation",
  "-language:_"
)

libraryDependencies ++= Seq(
  "com.amazonaws"  % "aws-java-sdk-iam"    % awsSdkVersion,
  "com.amazonaws"  % "aws-java-sdk-lambda" % awsSdkVersion,
  "com.amazonaws"  % "aws-java-sdk-s3"     % awsSdkVersion
)

