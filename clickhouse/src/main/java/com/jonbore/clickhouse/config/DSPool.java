package com.jonbore.clickhouse.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;

import java.util.Map;


public class DSPool {

    private static Map<String, DruidDataSource> dsPools;


    private static final String MYSQL_QUERY_SQL = "SELECT 'X' ";

    static {
        dsPools = Maps.newHashMap();
    }

    public static DruidDataSource removeDataSource(String id) {
        return dsPools.remove(id);
    }

    public static DruidDataSource getDataSource(String id, String driveName, String url, String userName, String password) {
        DruidDataSource dataSource = null;
        /*if (dsPools.containsKey( id )) {
            dataSource = dsPools.get( id );
        } else {*/
        dataSource = createDataSource(driveName, url, userName, password);
        if (dataSource != null) {
            dsPools.put(id, dataSource);
        }
        // }
        return dataSource;
    }

    public static DruidDataSource getDataSource(String driveName, String url, String userName, String password) {
        DruidDataSource dataSource = null;
        String key = url + "_" + userName + "_" + password;
        if (dsPools.containsKey(key)) {
            dataSource = dsPools.get(key);
        } else {
            dataSource = createDataSource(driveName, url, userName, password);
            dsPools.put(key, dataSource);
        }
        return dataSource;
    }

    public static DruidDataSource createDataSource(String driveName, String url, String userName, String password) {
        DruidDataSource dataSource = null;
        try {
            dataSource = new DruidDataSource();

            dataSource.setDriverClassName(driveName);
            dataSource.setUrl(url);
            dataSource.setUsername(userName);
            dataSource.setPassword(password);

            //参数设置
//            dataSource.setFilters("stat");
            dataSource.setMaxActive(500);
            dataSource.setInitialSize(20);

            dataSource.setMaxWait(10 * 60 * 1000);
            dataSource.setMinIdle(10);

            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            dataSource.setValidationQuery("SELECT 'x'");
            dataSource.setValidationQueryTimeout(30);

            //timeBetweenEvictionRunsMillis
            //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            dataSource.setTimeBetweenEvictionRunsMillis(1000 * 60);
            //配置一个连接在池中最小生存的时间，单位是毫秒
            dataSource.setMinEvictableIdleTimeMillis(1000 * 60 * 10);
            dataSource.setLogAbandoned(true);
            dataSource.init();
            System.out.println("init dataSource pool success,{userName:" + userName + "url:" + url + "}");
        } catch (Exception e) {
            System.out.println("init dataSource pool fail,{userName:" + userName + ",url:" + url + "}," + e.getMessage());
            if (dataSource != null) {
                dataSource.close();
            }
            dataSource = null;
        }

        return dataSource;
    }

}
