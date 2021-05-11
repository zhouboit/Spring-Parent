package com.jonbore.flink.stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jonbore.clickhouse.ClickHouseClient;
import com.jonbore.clickhouse.config.ClickHouseConfig;
import com.jonbore.flink.config.Page;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class MySQL2ClickhouseJob {
    public static void main(String[] args) throws Exception {
        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        String sourceDatabase = "mes_sys_app_copy";
        if (params.has("source")) {
            sourceDatabase = params.get("source");
        } else {
            System.out.println("未指定源表名称，将使用默认表[mes_sys_app_copy],如果需要自定义 -source XXX");
        }
        int packageSize = 5000;
        if (params.has("pkgNum")) {
            packageSize = params.getInt("pkgNum");
        } else {
            System.out.println("未指定分包数量,使用默认分包数[5000],如果需要自定义 -pkgNum XXX");
        }
        if (!params.has("target")) {
            System.out.println("必须指定clickhouse目标数据库表名，-target XXX");
            return;
        }
        String targetDatabase = params.get("target");
        String format = String.format("-source %s -target %s -pkgNum %s", sourceDatabase, targetDatabase, packageSize);
        ParameterTool defaultParams = ParameterTool.fromArgs(format.split(" "));
        HashMap<String, String> data = Maps.newHashMap();
        params.toMap().forEach(data::put);
        defaultParams.toMap().forEach(data::put);
        System.out.printf("将源表 %s 的数据拉取到目标表 %s 中,数据包大小为 %s\n", sourceDatabase, targetDatabase, packageSize);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(ParameterTool.fromMap(data));
        env
                .addSource(new SourceMySQL()).name("MySQL").setParallelism(1)
                .flatMap(new UnicodeFlatMap()).name("Unicode").setParallelism(8)
                .addSink(new SinkClickhouse()).name("Clickhouse").setParallelism(8);
        env.execute();
    }

    public static class SourceMySQL extends RichSourceFunction<JSONObject> {
        private static String sourceDatabase = "mes_sys_app_copy";
        private static int packageSize = 5000;
        private Connection connection = null;
        private PreparedStatement preparedStatement = null;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ParameterTool parameterTool = (ParameterTool)
                    getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            sourceDatabase = parameterTool.get("source");
            packageSize = parameterTool.getInt("pkgNum");
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://192.168.80.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8";
            String username = "root";
            String password = "123456";
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            preparedStatement = connection.prepareStatement("SELECT " +
                    "id," +
                    "`code`," +
                    "`name`," +
                    "`level`," +
                    "is_publish," +
                    "home_page_url," +
                    "logout_url," +
                    "description," +
                    "create_date," +
                    "update_date," +
                    "img_name," +
                    "item_order," +
                    "is_manage," +
                    "sys_type_id," +
                    "data_hub_id," +
                    "data_hub_oper_time" +
                    " FROM " +
                    sourceDatabase + " LIMIT ?, ?");

        }

        @Override
        public void run(SourceContext ctx) throws Exception {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(id) as totalNum from " + sourceDatabase);
            int total = 0;
            while (resultSet.next()) {
                total = resultSet.getInt(1);
            }
            Page page = new Page(packageSize, total);
            System.out.printf("共有 %s 条数据，分成 %s 个批次，每批最大数据包数量为 %s", total, page.getTotalPageNum(), page.getPageSize());
            for (int i = 1; i <= page.getTotalPageNum(); i++) {
                page.setPageNumber(i);
                preparedStatement.setInt(1, page.getStartRowNum());
                preparedStatement.setInt(2, page.getPageSize());
                ResultSet query = preparedStatement.executeQuery();
                ResultSetMetaData metaData = query.getMetaData();
                int index = 0;
                while (query.next()) {
                    JSONObject jsonObject = new JSONObject();
                    for (int i1 = 1; i1 <= metaData.getColumnCount(); i1++) {
                        jsonObject.fluentPut(metaData.getColumnName(i1), query.getObject(i1));
                    }
                    ctx.collect(jsonObject);
                    index++;
                }
                System.out.printf("第 %s 个数据包读取成功，共有 %s 条数据\n", i, index);
            }

        }

        @Override
        public void cancel() {

        }

        @Override
        public void close() throws Exception {
            super.close();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
        }
    }

    public static class UnicodeFlatMap implements FlatMapFunction<JSONObject, JSONObject> {
        @Override
        public void flatMap(JSONObject value, Collector<JSONObject> out) throws Exception {
            out.collect(value.fluentPut("data_hub_id", UUID.randomUUID().toString().replace("-", "").toUpperCase()).fluentPut("data_hub_oper_time", new Date()));
        }
    }

    public static class SinkClickhouse extends RichSinkFunction {
        public static ClickHouseClient instance;
        private static String targetDatabase = "";

        @Override
        public void open(Configuration parameters) throws Exception {
            ParameterTool parameterTool = (ParameterTool)
                    getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
            targetDatabase = parameterTool.get("target");
            super.open(parameters);
            ClickHouseConfig clickHouseConfig = new ClickHouseConfig();
            clickHouseConfig.setAddress("jdbc:clickhouse://192.168.80.43:8123/default");
            clickHouseConfig.setPassword("123456");
            clickHouseConfig.setUsername("default");
            clickHouseConfig.setDriverName("ru.yandex.clickhouse.ClickHouseDriver");
            instance = ClickHouseClient.instance(clickHouseConfig);
        }

        @Override
        public void invoke(Object value, Context context) throws Exception {
            JSONObject f0 = (JSONObject) value;
            Object[] data = dataPkg(f0);
            instance.executeUpdate("INSERT INTO default." + targetDatabase + " (id,code,name,`level`,is_publish,home_page_url,logout_url,description,create_date,update_date,img_name,item_order,is_manage,sys_type_id,data_hub_id,data_hub_oper_time) " +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);", data);
        }
//
//        @Override
//        public void invoke(Object value, Context context) throws Exception {
//            System.out.println(JSON.toJSONString(value));
//        }

        @Override
        public void close() throws Exception {
            super.close();
        }

        private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private static String[] cols = "id,code,name,level,is_publish,home_page_url,logout_url,description,create_date,update_date,img_name,item_order,is_manage,sys_type_id,data_hub_id,data_hub_oper_time".split(",");

        private static Object[] dataPkg(JSONObject row) {
            Object[] data = new Object[cols.length];
            try {
                for (int i = 0; i < cols.length; i++) {
                    if (row.get(cols[i]) instanceof Date) {
                        data[i] = sdf.format(row.get(cols[i]));
                        continue;
                    }
                    data[i] = row.get(cols[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("error msg : %s col : %s data : %s \n", e.getMessage(), JSON.toJSONString(cols), JSON.toJSONString(row));
            }
            return data;
        }
    }
}
