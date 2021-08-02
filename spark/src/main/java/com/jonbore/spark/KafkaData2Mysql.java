package com.jonbore.spark;

import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.DataStreamReader;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/7/29
 */
public class KafkaData2Mysql {
    public static void main(String[] args) {
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("id", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("home_page_url", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("is_publish", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("item_order", DataTypes.IntegerType, true));
        fields.add(DataTypes.createStructField("create_date", DataTypes.DateType, true));
        SparkSession kafkaConsumer = SparkSession.builder().appName("kafkaConsumer").getOrCreate();
        DataStreamReader kafka = kafkaConsumer.readStream().format("kafka");
        Properties properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "123456");
        properties.put("driver", "com.mysql.cj.jdbc.Driver");
        DataStreamWriter<Row> streamWriter = kafka
                .option("kafka.bootstrap.servers", "192.168.81.43:9092")
                .option("startingOffsets", "latest")
                .option("subscribe", "spark_stream_kafka")
                .load()
                .select("value").writeStream()
                .outputMode(OutputMode.Append())
                .foreachBatch((VoidFunction2<Dataset<Row>, Long>)
                        (batchDS, batchId) -> batchDS.write()
                                .mode(SaveMode.Append)
                                .jdbc("jdbc:mysql://192.168.10.100:3306/debezium?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8", "spark_output_001", properties)
                );
        try {
            streamWriter.start().awaitTermination();
        } catch (StreamingQueryException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
