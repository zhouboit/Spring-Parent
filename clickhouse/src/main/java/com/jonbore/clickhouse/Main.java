package com.jonbore.clickhouse;

import com.jonbore.clickhouse.config.ClickHouseConfig;

import java.util.Map;
import java.util.TimeZone;

public class Main {
    public static void main(String[] args) {
        System.out.println(TimeZone.getDefault());
        System.out.println("option default connection config.\n\tuse:\n\t\t --user default\n\t\t --password 123456\n\t\t --port 8123\n\t\t --host 127.0.0.1\n\t\t --db database\n\t\t ");
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
                System.out.printf("属性名称:%s,属性值%s\n", o, map.get(o));
            }
            instance.executeMap("INSERT INTO LOGIN_LOG(S_DEPARTMENT_ID,S_CLIENT_TYPE,S_DEPARTMENT_NAME,S_OPERATOR_ID,S_USERNAME,S_DEPARTMENT_CODE,S_BUSINESS_ACCOUNT_ID,S_OPERATION_RESULT,S_OPERATION_TYPE,S_OPERATOR_NAME,S_STATION_ID,S_BUSINESS_ACCOUNT_NAME,S_DATA_OUT,S_IP,S_BROWSER,S_REQUEST_PARAM,DT_CREATE_TIME,S_ID) VALUES(3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,now(),3);");
        } else {
            Option option = Option.fromArgs(args);
            ClickHouseConfig c = new ClickHouseConfig();
            c.setUsername(option.has("user") ? option.get("user") : "default");
            c.setPassword(option.has("password") ? option.get("password") : "123456");
            String url = "jdbc:clickhouse://"+(option.has("host") ? option.get("host"):"192.168.81.43")+":"+(option.has("port") ? option.get("port"):"8123");
            c.setAddress(url);
            c.setDatabase(option.has("db") ? option.get("db") : "default");
            c.setSocketTimeout(10000);
            Map map = ClickHouseClient.instance(c).executeMap("select version() as version ,now() as now");
            for (Object o : map.keySet()) {
                System.out.printf("属性名称:%s,属性值%s\n", o, map.get(o));
            }
        }

    }
}
