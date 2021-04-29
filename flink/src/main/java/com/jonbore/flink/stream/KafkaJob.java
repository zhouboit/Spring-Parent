package com.jonbore.flink.stream;

import com.alibaba.fastjson.JSON;
import com.jonbore.clickhouse.ClickHouseClient;
import com.jonbore.clickhouse.config.ClickHouseConfig;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class KafkaJob {

    public static void main(String[] args) throws Exception {
        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        params.get("address");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);
        env.setParallelism(1);
        DataStreamSource<String> input = env.addSource(new RMQSource<String>(
                new RMQConnectionConfig.Builder()
                        .setHost("119.3.251.41")
                        .setPort(5672)
                        .setUserName("admin")
                        .setPassword("admin")
                        .setVirtualHost("/")
                        .build(),
                "flink_source",
                true,
                new SimpleStringSchema()
        ));
        input
                .filter(value -> value != null && !value.isEmpty()).name("myFilter")
                .flatMap(new RabbitMQJob.SplitMessage()).name("myFlatMap")
                .addSink(new SinkClickhouse()).name("clickhouse");

        env.execute();
    }

    public static class SplitMessage implements FlatMapFunction<String, Tuple4<String, String, String, Date>> {

        @Override
        public void flatMap(String value, Collector<Tuple4<String, String, String, Date>> out) throws Exception {
            out.collect(new Tuple4<String, String, String, Date>(
                    value.split("_")[0],
                    value.split("_")[1],
                    value.split("_")[2],
                    new Date()));
        }
    }


    public static class SinkClickhouse extends RichSinkFunction {
        public static ClickHouseClient instance;

        static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ClickHouseConfig clickHouseConfig = new ClickHouseConfig();
            clickHouseConfig.setAddress("jdbc:clickhouse://219.142.87.90:9003");
            clickHouseConfig.setDatabase("default");
            instance = ClickHouseClient.instance(clickHouseConfig);
        }

        @Override
        public void invoke(Object value, SinkFunction.Context context) throws Exception {
            System.out.println(JSON.toJSONString(value));
            Object[] data = new Object[6];
            data[0] = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            data[1] = ((Tuple4) value).f0;
            data[2] = ((Tuple4) value).f1;
            data[3] = ((Tuple4) value).f2;
            data[4] = sdf.format(((Tuple4) value).f3);
            data[5] = sdf.format(new Date());
            instance.executeUpdate("INSERT INTO `default`.`flink_result_004` (\n" +
                    "\t`S_ID`,\n" +
                    "\t`S_MESSAGE_NAME`,\n" +
                    "\t`S_MAIN_TYPE`,\n" +
                    "\t`S_SUB_TYPE`,\n" +
                    "\t`S_SEND_TIME`,\n" +
                    "\t`S_CREATE_TIME`\n" +
                    ")\n" +
                    "VALUES\n" +
                    "\t(?,?,?,?,?,?);", data);
        }

        @Override
        public void close() throws Exception {
            super.close();
        }
    }
}
