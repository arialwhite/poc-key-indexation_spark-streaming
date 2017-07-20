# Spark streaming - Indexation des clefs des messages Kafka

Sous module git du projet https://github.com/arialwhite/poc-key-indexation

**Note: Projet actuellement en cours de réalisation**

## Installation
- Installation de sbt
- Construction d'un fat jar
```
sbt assembly
```
## Usage
**Exemple :**
```
/opt/spark/bin/spark-submit --class "StreamApp" --master local[4] 'target/scala-2.11/Streaming Project-assembly-1.0.jar'
```
## Inspiration
- https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html
- https://github.com/koeninger/kafka-exactly-once/blob/master/src/main/scala/example/BasicStream.scala
