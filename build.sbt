name := "Streaming Project"

version := "1.0"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.first
  case PathList(pl @ _*) if pl.contains("log4j.properties") => MergeStrategy.concat
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.1.1" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.1.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.1.1" % "provided",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.1.1",
  "org.apache.kafka" % "kafka-clients" % "0.10.2.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.2"
)
