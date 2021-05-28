package com.jonbore.ignite.process.stream;

import com.alibaba.druid.pool.DruidDataSource;
import com.jonbore.ignite.util.Constants;
import com.jonbore.ignite.util.DSPool;

import java.sql.*;

public class DatabaseFetchSize {
    public static void main(String[] args) throws SQLException {
        //                "com.mysql.cj.jdbc.Driver",
//                "org.postgresql.Driver",
//                "jdbc:mysql://192.168.80.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8",
//                "jdbc:postgresql://192.168.80.44:1328/cindy",
        DruidDataSource druidDataSource = DSPool.getDataSource(
//                "com.mysql.cj.jdbc.Driver",
//                "org.postgresql.Driver",
                Constants.DbDriver.ORACLE_DRIVER,
//                "jdbc:mysql://192.168.80.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8",
//                "jdbc:postgresql://192.168.80.44:1328/cindy",
                "jdbc:oracle:thin:@69.231.154.223:9999/helowin",
//                "root",
//                "beyondb",
                "username",
                "123456"
        );
        //增量字段值统计 zhou_2000_orcl_bo
        Connection connection = druidDataSource.getConnection();
        connection.setAutoCommit(false);
//        PreparedStatement preparedStatement = connection.prepareStatement("select * from public.zhou_2000_by_bo order by id");
        PreparedStatement preparedStatement = connection.prepareStatement("select * from \"USERNAME\".\"zhou_2000_orcl_bo\" order by id");
        preparedStatement.setFetchSize(5000);
//        PreparedStatement preparedStatement = druidDataSource.getConnection().prepareStatement("select * from mes_sys_app_20000", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//        preparedStatement.setFetchSize(Integer.MIN_VALUE);
        ResultSet query = preparedStatement.executeQuery();
        ResultSetMetaData metaData = query.getMetaData();
        int index = 0;
        while (query.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.printf("字段 %s 值为 %s", metaData.getColumnName(i), query.getObject(metaData.getColumnName(i)));
            }
            System.out.println();
            index++;
        }
        System.out.printf("数据总数%s", index);
        System.out.println();
    }

//    {
//        String driver;
//        String url;
//        switch (dbType) {
//            case MYSQL:
//                url = "jdbc:mysql://" + dbUrl + "?TreatTinyAsBoolean=false&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&tinyInt1isBit=false";
//                driver = MYSQL_DRIVER;
//                break;
//            case ORACLE:
//                url = "jdbc:oracle:thin:@" + dbUrl;
//                driver = ORACLE_DRIVER;
//                break;
//            case DM:
//                url = "jdbc:dm://" + dbUrl;
//                driver = DM_DRIVER;
//                break;
//            case SQLSERVER:
//                String ipAndPort = dbUrl.substring(0, dbUrl.lastIndexOf("/"));
//                url = "jdbc:sqlserver://" + ipAndPort + ";databaseName=" + dbName;
//                driver = SQLSERVER_DRIVER;
//                break;
//            case POSTGRESQL:
//                url = "jdbc:postgresql://" + dbUrl;
//                driver = POSTGRESQL_DRIVER;
//                break;
//            case BEYON:
//                url = "jdbc:beyondb://" + dbUrl;
//                driver = BEYON_DRIVER;
//                break;
//            case GAUSS:
//                url = "jdbc:gaussdb://" + dbUrl;
//                driver = GAUSS_DRIVER;
//                break;
//            case ESGYN:
//                url = "jdbc:t4jdbc://" + dbUrl;
//                driver = ESGYN_DRIVER;
//                break;
//            case HIVE:
//                url = "jdbc:hive2://" + dbUrl + "/" + dbName;
//                driver = HIVE_DRIVER;
//                break;
//            case CLICKHOUSE:
//                url = "jdbc:clickhouse://" + dbUrl;
//                driver = CLICKHOUSE_DRIVER;
//                break;
//
//            default:
//                throw new ServiceException("[DSUtils] getConn error. Unknown dbType.");
//        }
//        return DSUtils.getConn(driver, url, userName, pwd);
//    }
}
