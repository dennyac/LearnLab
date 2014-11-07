import sbt.Keys._

name := """LearnLab"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  javaEbean,
  cache,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
  "com.typesafe" %% "play-plugins-redis" % "2.1.1",
  "mysql" % "mysql-connector-java" % "5.5.40",
  filters
)

resolvers ++= Seq(
    "Apache" at "http://repo1.maven.org/maven2/",
    "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
    "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "org.sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"
)


lazy val root = (project in file(".")).enablePlugins(play.PlayJava)
