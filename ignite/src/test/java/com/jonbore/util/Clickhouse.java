package com.jonbore.util;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.TimeZone;

public class Clickhouse {
    public static void main(String[] args) throws ParseException {
        System.out.println(TimeZone.getDefault());

    }
//    public static void main(String[] args) throws SQLException {
//        Connection connection = DSPool.getDataSource("ru.yandex.clickhouse.ClickHouseDriver", "jdbc:clickhouse://192.168.80.43:8123/default", "default", "123456").getConnection();
//        Statement statement = connection.createStatement();
//        List<String> list = readTableSql("D:\\code\\GIt\\dataHub\\uni-dataaccess-ignite\\uni-ignite\\src\\main\\resources\\script\\clickhouse-create-table.template");
//        for (String s : list) {
//            statement.execute(s);
//        }
//        connection.close();
//    }

    private static List<String> readTableSql(String path) {
        BufferedReader reader = null;
        List<String> tables = Lists.newArrayList();
        StringBuilder tableSql = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (tableSql.toString().length() > 0) {
                        tables.add(tableSql.toString());
                    }
                    tableSql = new StringBuilder();
                } else {
                    tableSql.append(line).append(" ");
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tables;
    }
}
