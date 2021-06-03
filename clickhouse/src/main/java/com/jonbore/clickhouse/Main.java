package com.jonbore.clickhouse;

import com.jonbore.clickhouse.config.ClickHouseConfig;

import java.util.Map;
import java.util.TimeZone;

public class Main {
    public static void main(String[] args) {
        Option option = Option.fromArgs(args);
        System.out.println("option default using.\n" +
                "\tclickhouse usage:\n" +
                "\t\t --user default ,no user input null\n" +
                "\t\t --password 123456 ,no password input null\n" +
                "\t\t --port 8123\n" +
                "\t\t --host 192.168.81.43\n" +
                "\t\t --db default\n" +
                "\t\t --sql \"select version() as version ,now() as now\"\n" +
                "\ttimezone usage:\n" +
                "\t\t --tz 打印当前JVM默认时区\n"
        );
        if (args.length == 0) {
            ClickHouseConfig c = new ClickHouseConfig();
            c.setUsername("default");
            c.setPassword("123456");
            c.setAddress("jdbc:clickhouse://192.168.81.43:8123/log_engine");
            c.setDatabase("log_engine");
            c.setSocketTimeout(10000);
            ClickHouseClient instance = ClickHouseClient.instance(c);
            Map map = instance.executeMap("select version() as version ,now() as now");
            for (Object o : map.keySet()) {
                System.out.printf("属性名称:%s\t\t属性值%s\n", o, map.get(o));
            }
        } else {
            ClickHouseConfig c = new ClickHouseConfig();
            c.setUsername(option.has("user") ? option.get("user") : "default");
            c.setPassword(option.has("password") ? option.get("password") : "123456");
            String url = "jdbc:clickhouse://" + (option.has("host") ? option.get("host") : "192.168.81.43") + ":" + (option.has("port") ? option.get("port") : "8123");
            c.setAddress(url);
            c.setDatabase(option.has("db") ? option.get("db") : "default");
            c.setSocketTimeout(10000);
            Map map = ClickHouseClient.instance(c).executeMap(option.has("sql") ? option.get("sql") : "select version() as version ,now() as now");
            for (Object o : map.keySet()) {
                System.out.printf("属性名称:%s\t\t属性值%s\n", o, map.get(o));
            }
        }
        if (option.has("tz")) {
            System.out.println(TimeZone.getDefault());
        }
    }
}
