package com.jonbore.ignite.process.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jonbore.ignite.process.data.Plugin;
import com.jonbore.ignite.process.data.Rule;
import com.jonbore.ignite.process.data.Task;
import com.jonbore.ignite.util.DSPool;
import com.jonbore.ignite.util.IgniteNode;
import com.jonbore.ignite.util.MD5Util;
import com.jonbore.ignite.util.Option;
import org.apache.ignite.*;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.stream.StreamTransformer;

import javax.cache.Cache;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class StreamingSQLTask {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        Option option = Option.fromArgs(args);
        IgniteNode.start(option);
        Ignite ignite = Ignition.ignite();
        //mock task start
        Task task = new Task();
        task.setTableName("mes_app_bo_by");
        if (option.has("table")) {
            task.setTableName(option.get("table"));
        }
        task.setId(UUID.randomUUID().toString());
        task.setDriver("org.postgresql.Driver");
        task.setUrl("jdbc:postgresql://192.168.80.44:1328/cindy");
        task.setUsername("beyondb");
        task.setPassword("123456");
        task.setMajorId(new JSONArray().fluentAdd("id").toJSONString());
        task.setPkgNum(20000);
        Rule rule = new Rule();
        rule.setCol("home_page_url");
        rule.setReg("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Plugin plugin = new Plugin();
        plugin.setTaskId(task.getId());
        plugin.setId(MD5Util.md5(task.getId() + "regularExpression"));
        plugin.setName("正则校验");
        plugin.setCode("regularExpression");
        plugin.setRules(new ArrayList<Rule>() {{
            add(rule);
        }});

        Rule rule2 = new Rule();
        rule2.setCol("code");
        rule2.setReg("[A-Za-z0-9_!@#\\$%\\^&\\*\\(\\)\\|~`]");
        Plugin plugin2 = new Plugin();
        plugin2.setTaskId(task.getId());
        plugin2.setId(MD5Util.md5(task.getId() + "notEmpty"));
        plugin2.setName("非空值校验");
        plugin2.setCode("notEmpty");
        plugin2.setRules(new ArrayList<Rule>() {{
            add(rule2);
        }});

        Rule rule3 = new Rule();
        rule3.setCol("is_publish");
        rule3.setReg("^[-+]?[0-9]*\\.[0-9]{1}$");
        Plugin plugin3 = new Plugin();
        plugin3.setTaskId(task.getId());
        plugin3.setId(MD5Util.md5(task.getId() + "accuracy"));
        plugin3.setName("精度校验");
        plugin3.setCode("accuracy");
        plugin3.setRules(new ArrayList<Rule>() {{
            add(rule3);
        }});
        Rule rule4 = new Rule();
        rule4.setCol("description");
        rule4.setReg("^[1-9][0-9]{5}(18|19|([23][0-9]))[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9a-zA-Z]{3}[0-9Xx]$");
        Plugin plugin4 = new Plugin();
        plugin4.setTaskId(task.getId());
        plugin4.setId(MD5Util.md5(task.getId() + "ID"));
        plugin4.setName("身份证校验");
        plugin4.setCode("ID");
        plugin4.setRules(new ArrayList<Rule>() {{
            add(rule4);
        }});
        Rule rule5 = new Rule();
        rule5.setCol("level");
        rule5.setReg("^[01]{1}$");
        Plugin plugin5 = new Plugin();
        plugin5.setTaskId(task.getId());
        plugin5.setId(MD5Util.md5(task.getId() + "codomain"));
        plugin5.setName("值域校验");
        plugin5.setCode("codomain");
        plugin5.setRules(new ArrayList<Rule>() {{
            add(rule5);
        }});
        Rule rule6 = new Rule();
        rule6.setCol("item_order");
        rule6.setReg("^[1-9]{1}$");
        Plugin plugin6 = new Plugin();
        plugin6.setTaskId(task.getId());
        plugin6.setId(MD5Util.md5(task.getId() + "range"));
        plugin6.setName("范围校验");
        plugin6.setCode("range");
        plugin6.setRules(new ArrayList<Rule>() {{
            add(rule6);
        }});
        task.setPlugins(new ArrayList<Plugin>() {{
            add(plugin);
            add(plugin2);
            add(plugin3);
            add(plugin4);
            add(plugin5);
            add(plugin6);
        }});
//        {"batchNum":"20210511090456286","beforeRule":"[{\"createTime\":1620691200000,\"id\":201,\"name\":\"值域校验\",\"param\":\"{\\\"dataField\\\":[{\\\"fieldName\\\":\\\"level\\\",\\\"array\\\":\\\"0,1\\\"}]}\",\"pluginCode\":\"QUALITY_VALUE_RANGE\",\"pluginId\":\"aca49b05-a187-4248-83d4-bdd3803bd862\",\"qId\":177,\"status\":1}]","dbTarget":"{\"dataMap\":{\"schema\":\"default\",\"password\":\"\",\"driver\":\"ru.yandex.clickhouse.ClickHouseDriver\",\"table_name\":\"dq_data_result\",\"url\":\"jdbc:clickhouse://192.168.5.131:8123/hfc_test\",\"username\":\"default\"}}","inMap":{"normalTaskStr":"{\"dataAmount\":27,\"qId\":177,\"executeKey\":\"20210511090456286\",\"createTime\":1620723896000,\"id\":46172,\"taskInfo\":\"SELECT * FROM mes_sys_app_copy limit 0, 5000\",\"totalTask\":1,\"status\":0}","db_id":"223","major_json":"[{\"fieldNameCn\":\"id\",\"fieldName\":\"id\",\"scale\":0,\"isPk\":\"1\",\"isNull\":\"1\",\"isBusinessPk\":\"1\",\"fieldType\":\"VARCHAR\",\"fieldLength\":\"50\"}]","DataColumn":[{"fieldNameCn":"id","fieldName":"id","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"1","fieldType":"VARCHAR","fieldLength":"50"},{"fieldNameCn":"编码","fieldName":"code","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"VARCHAR","fieldLength":"50"},{"fieldNameCn":"名称","fieldName":"name","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"VARCHAR","fieldLength":"50"},{"fieldNameCn":"排序级别","fieldName":"level","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"INT","fieldLength":"10"},{"fieldNameCn":"发布状态 0:未发布；1：已发布","fieldName":"is_publish","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"TEXT"},{"fieldNameCn":"home页","fieldName":"home_page_url","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"TEXT"},{"fieldNameCn":"登出","fieldName":"logout_url","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"TEXT"},{"fieldNameCn":"描述","fieldName":"description","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"TEXT"},{"fieldNameCn":"创建时间","fieldName":"create_date","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"DATETIME"},{"fieldNameCn":"更新时间","fieldName":"update_date","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"DATETIME"},{"fieldNameCn":"img_name","fieldName":"img_name","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"VARCHAR","fieldLength":"100"},{"fieldNameCn":"序号","fieldName":"item_order","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"INT","fieldLength":"10"},{"fieldNameCn":"is_manage","fieldName":"is_manage","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"TINYINT","fieldLength":"3"},{"fieldNameCn":"系统类型id","fieldName":"sys_type_id","isPk":"1","isNull":"1","scale":0,"isBusinessPk":"0","fieldType":"VARCHAR","fieldLength":"50"}],"mysql":{"password":"123456","driver":"com.mysql.cj.jdbc.Driver","url":"jdbc:mysql://192.168.80.41:3306/bo_test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&tinyInt1isBit=false","username":"root"},"table_name":"mes_sys_app_copy","ipList":"[]"},"outMap":{},"runPluginCode":"mysql","runStatus":"0","runSuccess":false,"taskId":46172,"taskInfo":"SELECT * FROM mes_sys_app_copy limit 0, 5000","type":"quality"}
        writePluginInfo(task);
        //task end
        CacheConfiguration cacheCfg = new CacheConfiguration();
        cacheCfg.setName(task.getTableName());
        cacheCfg.setBackups(0);
        IgniteCache<Object, Object> table_0 = ignite.getOrCreateCache(cacheCfg);
        IgniteCache<Object, Object> report = ignite.getOrCreateCache("report");
        IgniteCache<Object, Object> taskMajor = ignite.getOrCreateCache(task.getId() + "_major");
        IgniteCache<Object, Object> taskPlugin = ignite.getOrCreateCache(task.getId() + "_plugin");
        long start = System.currentTimeMillis();
        long loaded;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        conn = DSPool.getDataSource(
                task.getDriver(),
                task.getUrl(),
                task.getUsername(),
                task.getPassword()
        ).getConnection();
//        stmt = conn.prepareStatement("select * from dg_mysql_0", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        conn.setAutoCommit(false);
        stmt = conn.prepareStatement("select * from " + task.getTableName());
        int pkgNum = task.getPkgNum();
        stmt.setFetchSize(pkgNum);
        rs = stmt.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        String flowId = UUID.randomUUID().toString();
        //根据缓存名获取流处理器，并往流处理器中添加数据
        long index = 0;
        try (IgniteDataStreamer<String, Map<String, Object>> streamer = ignite.dataStreamer(task.getTableName())) {
            while (rs.next()) {
                HashMap<String, Object> row = Maps.newHashMap();
                JSONArray majorIds = JSON.parseArray(task.getMajorId());
                JSONObject jsonObject = new JSONObject();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
                    if (majorIds.contains(metaData.getColumnName(i))) {
                        jsonObject.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
                    }
                }
                streamer.allowOverwrite(true);
                streamer.receiver(StreamTransformer.from((e, arg) -> {
                    Map<String, Object> o = (Map<String, Object>) arg[0];
                    IgniteCompute compute = ignite.compute();
                    ActorParam actorParam = new ActorParam();
                    actorParam.setVal(o);
                    actorParam.setTask(task);
                    Map<String, Boolean> booleanMap = compute.execute(TaskActor.class, actorParam);
                    o.putAll(booleanMap);
                    e.setValue(o);
                    return null;
                }));
                String majorId = MD5Util.md5(jsonObject.toJSONString());
                row.put("majorId", majorId);
                taskMajor.put(majorId, jsonObject.toJSONString());
                streamer.addData(majorId, row);
                index++;
                if (index % pkgNum == 0) {
                    qualityInspection(table_0, report, flowId, index, streamer, pkgNum, task, taskMajor, taskPlugin);
                }
            }
            if (index % pkgNum != 0) {
                qualityInspection(table_0, report, flowId, index, streamer, pkgNum, task, taskMajor, taskPlugin);
            }
            loaded = System.currentTimeMillis();
            System.out.printf("[%s]质检过程耗时 %s 毫秒\n", sdf.format(new Date()), loaded - start);
        }
        List<Map<String, Map<String, String>>> batchReport = report.query(new ScanQuery<String, Map<String, Map<String, String>>>((k, v) -> k.startsWith(flowId + "_")), Cache.Entry::getValue).getAll();
        for (Map<String, Map<String, String>> batch : batchReport) {
            for (Plugin pluginInfo : task.getPlugins()) {
                Map<String, String> pluginMap = batch.get(pluginInfo.getId());
                pluginInfo.setTotal(Long.parseLong(pluginMap.get("total")));
                pluginInfo.setUnQuality(Long.parseLong(pluginMap.get("unquality")));
                pluginInfo.setQuality(Long.parseLong(pluginMap.get("total")) - Long.parseLong(pluginMap.get("unquality")));
            }
        }
        System.out.printf("[%s]生成质检报告耗时 %s 毫秒\n", sdf.format(new Date()), System.currentTimeMillis() - loaded);
        System.out.printf("[%s]任务ID[%s] 插件数量[%s] 执行批数量[%s]:\n", sdf.format(new Date()), flowId, task.getPlugins().size(), batchReport.size());
        for (Plugin pluginInfo : task.getPlugins()) {
            System.out.printf("[%s]插件名称[%s] 插件ID[%s] 质检数据总量%s 合格数据%s条，不合格数据%s条\n", sdf.format(new Date()), pluginInfo.getName(), pluginInfo.getId(), pluginInfo.getTotal(), pluginInfo.getQuality(), pluginInfo.getUnQuality());
        }
        System.out.printf("[%s]总耗时 %s 毫秒\n", sdf.format(new Date()), System.currentTimeMillis() - start);
        List<String> taskKey = report.query(new ScanQuery<String, Map<String, String>>((k, v) -> k.startsWith(flowId + "_")), Cache.Entry::getKey).getAll();
        report.removeAll(new HashSet<>(taskKey));
        ignite.destroyCache(task.getTableName());
        //stop ignite 实例
        ignite.close();
    }


    private static void qualityInspection(IgniteCache<Object, Object> table_0, IgniteCache<Object, Object> report, String flowId, long index, IgniteDataStreamer<String, Map<String, Object>> streamer, int pkgNum, Task task, IgniteCache<Object, Object> taskMajor, IgniteCache<Object, Object> taskPlugin) {
        streamer.flush();
        Map<String, Object> batchReport = Maps.newHashMap();
        batchReport.put("batchId", UUID.randomUUID().toString());
        for (Plugin plugin : task.getPlugins()) {
            List<String> reg = table_0.query(new ScanQuery<String, Map<String, Object>>((k, v) -> !(boolean) v.get(plugin.getId())), Cache.Entry::getKey).getAll();
            boolean b = writeUnqualifiedData(task, reg, taskMajor);
            if (b) {
                System.out.printf("[%s]插件[%s]写入%s条数据至clickhouse成功\n", sdf.format(new Date()), plugin.getName(), reg.size());
            } else {
                System.out.printf("[%s]插件[%s]写入clickhouse失败\n", sdf.format(new Date()), plugin.getName());
            }
            boolean pluginDataResult = writePluginData(buildPluginData(task, taskPlugin));
            if (pluginDataResult) {
                System.out.printf("[%s]插件[%s]详情写入clickhouse成功\n", sdf.format(new Date()), plugin.getName());
            } else {
                System.out.printf("[%s]插件[%s]详情写入clickhouse失败\n", sdf.format(new Date()), plugin.getName());
            }
            Map<String, String> pluginReport = Maps.newHashMap();
            pluginReport.put("total", ((index % pkgNum) == 0 ? pkgNum : index / pkgNum) + "");
            pluginReport.put("unquality", reg.size() + "");
            pluginReport.put("pluginId", plugin.getId());
            batchReport.put(plugin.getId(), pluginReport);
        }
        report.put(flowId + "_" + batchReport.get("batchId"), batchReport);
        table_0.clear();
        taskMajor.clear();
        taskPlugin.clear();
        System.out.printf("[%s]提交第 %s 次数据，共提交 %s 条数据\n", sdf.format(new Date()), (index % pkgNum) == 0 ? index / pkgNum : (index / pkgNum) + 1, index);
    }

    private static boolean writeUnqualifiedData(Task task, List<String> records, IgniteCache<Object, Object> taskMajor) {
        try {
            Connection connection = DSPool.getDataSource("ru.yandex.clickhouse.ClickHouseDriver", "jdbc:clickhouse://192.168.80.43:8123/default", "default", "123456").getConnection();
            String sql = "INSERT INTO default.dq_task_main (id,task_id,table_name,major_json,major_hash_code,create_time) VALUES (?,?,?,?,?,?);";
            return executeBatchWithParams(sql, buildMasterData(task, records, taskMajor), connection);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private static boolean writePluginData(List<Object[]> pluginData) {
        try {
            Connection connection = DSPool.getDataSource("ru.yandex.clickhouse.ClickHouseDriver", "jdbc:clickhouse://192.168.80.43:8123/default", "default", "123456").getConnection();
            String sql = "INSERT INTO default.dq_task_plugin_detail (id,major_hash_code,plugin_id,col_value,create_time,status,task_id) VALUES (?,?,?,?,?,?,?);";
            return executeBatchWithParams(sql, pluginData, connection);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private static boolean writePluginInfo(Task task) {
        try {
            Connection connection = DSPool.getDataSource("ru.yandex.clickhouse.ClickHouseDriver", "jdbc:clickhouse://192.168.80.43:8123/default", "default", "123456").getConnection();
            String sql = "INSERT INTO default.dq_task_plugin_main (id,task_id,plugin_code,plugin_id,plugin_name,create_time) VALUES (?,?,?,?,?,?);";
            return executeBatchWithParams(sql, buildPlugin(task), connection);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    private static List<Object[]> buildMasterData(Task task, List<String> records, IgniteCache<Object, Object> taskMajor) {
        List<Object[]> result = Lists.newArrayList();
        for (String record : records) {
            Object[] data = new Object[6];
            data[0] = UUID.randomUUID().toString();
            data[1] = task.getId();
            data[2] = task.getTableName();
            data[3] = taskMajor.get(record);
            data[4] = record;
            data[5] = sdf.format(new Date());
            result.add(data);
        }
        return result;
    }

    private static List<Object[]> buildPluginData(Task task, IgniteCache<Object, Object> taskPlugin) {
        List<Object[]> result = Lists.newArrayList();
        for (Map<String, Object> pluginData : taskPlugin.query(new ScanQuery<String, Map<String, Object>>((k, v) -> !k.isEmpty()), Cache.Entry::getValue)) {
            Object[] data = new Object[7];
            data[0] = UUID.randomUUID().toString();
            data[1] = pluginData.get("majorId");
            data[2] = pluginData.get("pluginId");
            data[3] = pluginData.get("col");
            data[4] = sdf.format(new Date());
            data[5] = "0";
            data[6] = task.getId();
            result.add(data);
        }
        return result;
    }

    private static List<Object[]> buildPlugin(Task task) {
        List<Object[]> result = Lists.newArrayList();
        for (Plugin plugin : task.getPlugins()) {
            Object[] data = new Object[6];
            data[0] = UUID.randomUUID().toString();
            data[1] = task.getId();
            data[2] = plugin.getCode();
            data[3] = plugin.getId();
            data[4] = plugin.getName();
            data[5] = sdf.format(new Date());
            result.add(data);
        }
        return result;
    }

    public static boolean executeBatchWithParams(String sql, List<Object[]> vals, Connection connection) throws SQLException {
        boolean flag;
        PreparedStatement ps = connection.prepareStatement(sql);
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
            if (ps != null) {
                ps.close();
            }
            if (!connection.isClosed()) {
                connection.close();
            }
        }
        return flag;
    }

}
