package com.jonbore.clickhouse;

import com.jonbore.clickhouse.config.ClickHouseConfig;
import com.jonbore.clickhouse.config.DSPool;
import ru.yandex.clickhouse.ClickHouseDataSource;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Created on 2021/3/26.
 *
 * @author bo.zhou
 */
public class ClickHouseClient {
    private static ClickHouseClient instance;
    private static ClickHouseDataSource clickHouseDataSource;
    private ClickHouseConfig clickHouseConfig;

    public ClickHouseClient(ClickHouseConfig clickHouseConfig) {
        this.clickHouseConfig = clickHouseConfig;
    }

    public static ClickHouseClient instance(ClickHouseConfig clickHouseConfig) {
        if (instance == null) {
            synchronized (ClickHouseClient.class) {
                if (instance == null) {
                    instance = new ClickHouseClient(clickHouseConfig);
                }
            }
        }
        return instance;
    }

    /**
     * 获取连接
     *
     * @return: Connection
     * @author: pf.kang
     * @date: 2021/3/37
     */
    public Connection getConnection() {
        try {
            return DSPool.getDataSource(clickHouseConfig.getDriverName(), clickHouseConfig.getAddress(), clickHouseConfig.getUsername(), clickHouseConfig.getPassword()).getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * @param sql 查询sql语句
     * @return
     * @author: pf.kang
     * @return: List<Map> 列表
     */
    public List<Map> executeList(String sql) {
        return executeList(sql, new Object[]{});
    }

    /**
     * 查询列表数据
     *
     * @param sql     查询sql语句
     * @param objects 入参
     * @return: List<Map> 列表
     * @author: zy.xiao
     * @date: 2021/3/19
     */
    public List<Map> executeList(String sql, Object[] objects) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet results = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
            }
            results = ps.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();
            List<Map> list = new ArrayList<>();
            while (results.next()) {
                Map row = new HashMap();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.put(getLowerCamelCaseName(rsmd.getColumnName(i)), results.getObject(rsmd.getColumnName(i)));
                }
                list.add(row);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            allClose(connection, ps, results);
        }
        return null;
    }

    public Map executeMap(String sql) {
        return this.executeMap(sql, new Object[]{});
    }

    /**
     * 查询对象数据
     *
     * @param sql     查询sql语句
     * @param objects 入参
     * @return: Map 对象
     * @author: pf.kang
     * @date: 2021/3/30
     */
    public Map executeMap(String sql, Object[] objects) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet results = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
            }
            results = ps.executeQuery();
            if (results == null) {
                return null;
            }
            ResultSetMetaData rsmd = results.getMetaData();
            Map row = new HashMap();
            while (results.next()) {
                row.clear();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.put(getLowerCamelCaseName(rsmd.getColumnName(i)), results.getObject(rsmd.getColumnName(i)));
                }
            }
            return row;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            allClose(connection, ps, results);
        }
        return null;
    }

    public String executeValue(String sql) {
        return this.executeValue(sql, new Object[]{});
    }

    /**
     * 查询基础类型 如count()
     *
     * @param sql     查询sql语句
     * @param objects 入参
     * @return: String 如果查询数量也是按string类型返回
     * @author: pf.kang
     * @date: 2021/3/30
     */
    public String executeValue(String sql, Object[] objects) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet results = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
            }
            results = ps.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();
            String value = null;
            while (results.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    value = results.getString(rsmd.getColumnName(i));
                }
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            allClose(connection, ps, results);
        }
        return null;
    }

    /**
     * 执行增删改
     *
     * @param sql     sql操作语句
     * @param objects sql语句中的参数，可以为null
     * @return int > 0 操作成功；<0 操作失败
     */
    public int executeUpdate(String sql, Object[] objects) {
        int updateResult = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
            }
            updateResult = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            allClose(connection, ps, null);
        }
        return updateResult;
    }

    /**
     * 查询单条数据
     *
     * @param sql     sql操作语句
     * @param objects sql语句中的参数，可以为null
     * @param <T>     泛型
     * @return 返回单个实体对象
     */
    public <T> T getSingleResult(String sql, Object[] objects, Class<T> tClass) {
        T object = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
            }
            //执行查询语句
            resultSet = ps.executeQuery();
            //通过反射创建tClass类的对象
            //从结果集中获取数据
            if (resultSet.next()) {
                object = getObject(resultSet, tClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            allClose(connection, ps, resultSet);
        }
        return object;
    }

    /**
     * 查询多条数据
     *
     * @param sql     sql操作语句
     * @param objects sql语句中的参数，可以为null
     * @param <T>     泛型
     * @return 返回多个对象的集合
     */
    public <T> List<T> getComplexResult(String sql, Object[] objects, Class<T> tClass) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        List<T> tList = new ArrayList<>();
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
            }
            //执行查询语句
            resultSet = ps.executeQuery();
            //通过反射创建tClass类的对象
            //从结果集中获取数据
            while (resultSet.next()) {
                T object = getObject(resultSet, tClass);
                tList.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            allClose(connection, ps, resultSet);
        }
        return tList;
    }

    /**
     * 通过反射+内省将结果集中的数据拿出来
     *
     * @param resultSet 结果集
     * @param tClass    传递过来的操作类
     * @param <T>       泛型
     * @return 返回一个泛型对象Object
     * @throws Exception
     */
    public <T> T getObject(ResultSet resultSet, Class<T> tClass) throws Exception {
        T object = tClass.newInstance();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            //获取列名
            String colName = resultSetMetaData.getColumnName(i + 1);
            //通过内省获取包含列名指定方法，一般是get和set
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(colName, tClass);
            if (propertyDescriptor != null) {
                //获取到方法后再获取set方法对private修饰的相关属性进行赋值
                Method method = propertyDescriptor.getWriteMethod();
                //通过反射将属性添加到object对象中
                method.invoke(object, resultSet.getObject(colName));
            }
        }
        return object;
    }

    /**
     * 释放资源
     *
     * @param connection 连接
     * @param ps         执行命令
     * @param rs         结果集
     */
    public void allClose(Connection connection, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLowerCamelCaseName(String columnName) {
        String col = null;
        if (!columnName.contains("_")) {
            col = columnName.toLowerCase();
        } else {
            StringBuilder stringBuffer = new StringBuilder();
            String[] words = columnName.toLowerCase().split("_");
            for (int i = 1; i < words.length; i++) {
                if (i == 1) {
                    stringBuffer.append(words[i]);
                } else {
                    stringBuffer.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1));
                }
            }
            col = stringBuffer.toString();
        }
        return col;
    }

    public boolean executeBatchWithParams(String sql, List<Object[]> vals) throws SQLException {
        boolean flag = true;
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            for (Object[] val : vals) {
                for (int i = 1; i <= val.length; i++) {
                    ps.setObject(i, val[i - 1]);
                }
                ps.addBatch();
            }
            int[] ints = ps.executeBatch();
            flag = Arrays.stream(ints).sum() == vals.size();
        } catch (Exception e) {
            flag = false;
            throw e;
        } finally {
            allClose(conn, ps, null);
        }
        return flag;
    }
}
