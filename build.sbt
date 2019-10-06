name := "duelLinks"

version := "1.0.0"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.projectlombok" % "lombok" % "1.18.8",
  "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.2.1",
)

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
