package com.jonbore.ignite.process.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EntitySql {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.10.154:3306/uni_datahub_0531_Win154?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8", "root", "123456");
        Statement statement = connection.createStatement();
        ResultSet query = statement.executeQuery("select version() as version;");
        while (query.next()) {
            System.out.println(query.getString("version"));
        }
        connection.close();
    }
}
