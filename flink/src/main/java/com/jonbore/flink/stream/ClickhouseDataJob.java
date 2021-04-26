package com.jonbore.flink.stream;

import com.alibaba.fastjson.JSON;
import com.jonbore.clickhouse.ClickHouseClient;
import com.jonbore.clickhouse.config.ClickHouseConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.types.Row;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ClickhouseDataJob {
    public static void main(String[] args) throws Exception {
        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("119.3.251.41", 8081, "D:\\code\\Local\\Spring-Parent\\flink\\target\\flink-1.0.0.jar");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);
        env.setParallelism(1);
        DataStreamSource<Row> input = env.createInput(
                JdbcInputFormat.buildJdbcInputFormat()
                        .setDrivername("ru.yandex.clickhouse.ClickHouseDriver")
                        .setDBUrl("jdbc:clickhouse://219.142.87.90:9003/default")
                        .setQuery("SELECT mcmrc.S_ID,mcmrc.S_MESSAGE_NAME,mcmrc.S_MAIN_TYPE,mcmrc.S_SUB_TYPE ,mcmrc.S_CREATE_TIME from default.MAN_COMPONENT_MESSAGE_RABBIT_cluster mcmrc ")
                        .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.DATE_TYPE_INFO))
                        .finish()
        );
        input.addSink(new ClickHouseSink());
        env.execute();


    }

    public static class ClickHouseSink extends RichSinkFunction {
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
        public void invoke(Object value, Context context) throws Exception {
            System.out.println(JSON.toJSONString(value));
            Object[] data = new Object[6];
            data[0]= UUID.randomUUID().toString().replace("-", "").toUpperCase();
            data[1]=((Row) value).getField(1);
            data[2]=((Row) value).getField(2);
            data[3]=((Row) value).getField(3);
            data[4]=sdf.format(new Date());
            data[5]=sdf.format(((Row) value).getField(4));
            instance.executeUpdate("INSERT INTO `default`.`flink_result_002` (\n" +
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
