package com.jonbore.clickhouse;

import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.ClickHouseStatement;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/8/2
 */
public class JdbcOperation {
    public static void main(String[] args) {
        String url = "jdbc:clickhouse://192.168.81.43:8123/datahub";
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser("default");
        properties.setPassword("123456");
        ClickHouseDataSource dataSource = new ClickHouseDataSource(url, properties);
//        String sql = "alter table fromMysq2 update int_test=1";
        String sql = "select * from datahub.fromMysq2";
        try (ClickHouseConnection conn = dataSource.getConnection();
             ClickHouseStatement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            List<String> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getString("id"));
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.printf("字段名:%s,字段值%s%n", metaData.getColumnLabel(i), rs.getObject(metaData.getColumnLabel(i)));
                }
            }
            String update = "alter table datahub.fromMysq2 update int_test=? where id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(update);
            for (String s : list) {
                preparedStatement.setObject(1, Math.floor(Math.random() * 10));
                preparedStatement.setObject(2, s);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
