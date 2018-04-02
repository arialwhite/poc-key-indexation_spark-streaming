import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.TaskContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import com.datastax.spark.connector.{SomeColumns, _}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object StreamApp {
  def main(args: Array[String]) {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "kafka:29092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "index_to_cassandra",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val conf = new SparkConf().setAppName("Streaming Application")
               .setIfMissing("spark.cassandra.connection.host", "cassandra")
    val ssc = new StreamingContext(conf, Seconds(2))

    val topics = Array("small_topic")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      rdd.mapPartitions { it =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        val fromOffset = o.fromOffset
        it.zipWithIndex.map { case (record: ConsumerRecord[String, String], i)  =>
          val partition = o.partition
          val key = record.key
          val value = record.value
          (key, fromOffset + i, partition, value)
        }
      }.saveToCassandra("kafka_topics", "small_topic", SomeColumns("key", "offset", "partition", "value"))

      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
 
    ssc.start()
    ssc.awaitTermination()
  }
}