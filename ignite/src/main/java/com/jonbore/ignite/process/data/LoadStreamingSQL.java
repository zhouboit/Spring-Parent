package com.jonbore.ignite.process.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jonbore.ignite.util.DSPool;
import com.jonbore.ignite.util.MD5Util;
import com.jonbore.ignite.util.Option;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.*;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.apache.ignite.spi.failover.always.AlwaysFailoverSpi;
import org.apache.ignite.spi.loadbalancing.roundrobin.RoundRobinLoadBalancingSpi;
import org.apache.ignite.stream.StreamTransformer;

import javax.cache.Cache;
import javax.cache.processor.MutableEntry;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadStreamingSQL {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        Option option = Option.fromArgs(args);
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setIncludeEventTypes(org.apache.ignite.events.EventType.EVT_NODE_LEFT, org.apache.ignite.events.EventType.EVT_NODE_FAILED);
        RoundRobinLoadBalancingSpi roundRobinLoadBalancingSpi = new RoundRobinLoadBalancingSpi();
        roundRobinLoadBalancingSpi.setPerTask(true);
        igniteConfiguration.setLoadBalancingSpi(roundRobinLoadBalancingSpi);
        AlwaysFailoverSpi alwaysFailoverSpi = new AlwaysFailoverSpi();
        alwaysFailoverSpi.setMaximumFailoverAttempts(5);
        igniteConfiguration.setFailoverSpi(alwaysFailoverSpi);
        ExecutorConfiguration executorConfiguration = new ExecutorConfiguration();
        executorConfiguration.setName("myPool");
        executorConfiguration.setSize(20);
        igniteConfiguration.setExecutorConfiguration(executorConfiguration);
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        igniteConfiguration.setPublicThreadPoolSize(16);
        igniteConfiguration.setSystemThreadPoolSize(8);
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setPageSize(8 * 1024);
        dataStorageConfiguration.setWriteThrottlingEnabled(true);
        dataStorageConfiguration.setWalSegmentSize(1024 * 1024 * 1024);
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName("40MB_Region_Eviction");
        dataRegionConfiguration.setInitialSize(20 * 1024 * 1024);
        dataRegionConfiguration.setMaxSize(40 * 1024 * 1024);
        dataRegionConfiguration.setPageEvictionMode(DataPageEvictionMode.valueOf("RANDOM_2_LRU"));
        DataRegionConfiguration[] dataRegionConfigurations = new DataRegionConfiguration[1];
        dataRegionConfigurations[0] = dataRegionConfiguration;
        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfigurations);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("SampleCache");
        cacheConfiguration.setDataRegionName("40MB_Region_Eviction");
        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
        //--local args
        if (option.has("cluster")) {
            ZookeeperDiscoverySpi zookeeperDiscoverySpi = new ZookeeperDiscoverySpi();
            zookeeperDiscoverySpi.setZkConnectionString("192.168.80.41:2181");
            zookeeperDiscoverySpi.setJoinTimeout(1000);
            zookeeperDiscoverySpi.setSessionTimeout(30000);
            igniteConfiguration.setDiscoverySpi(zookeeperDiscoverySpi);
        }
        Ignition.start(igniteConfiguration);
        Ignite ignite = Ignition.ignite();
        //mock taskInfo start
        TaskInfo taskInfo = new TaskInfo();
//                "com.mysql.cj.jdbc.Driver",
//                "org.postgresql.Driver",
//                "jdbc:mysql://192.168.80.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8",
//                "jdbc:postgresql://192.168.80.44:1328/cindy",
        taskInfo.setTableName("mes_app_bo_by");
        if (option.has("table")) {
            taskInfo.setTableName(option.get("table"));
        }
        taskInfo.setId(UUID.randomUUID().toString());
        taskInfo.setDriver("org.postgresql.Driver");
        taskInfo.setUrl("jdbc:postgresql://192.168.80.44:1328/cindy");
        taskInfo.setUsername("beyondb");
        taskInfo.setPassword("123456");
        taskInfo.setMajorId(new JSONArray().fluentAdd("id").toJSONString());
        taskInfo.setPkgNum(20000);
        Rule rule = new Rule();
        rule.setCol("home_page_url");
        rule.setReg("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Plugin plugin = new Plugin();
        plugin.setId(MD5Util.md5(taskInfo.getId() + "regularExpression"));
        plugin.setName("正则校验");
        plugin.setCode("regularExpression");
        plugin.setRules(new ArrayList<Rule>() {{
            add(rule);
        }});

        Rule rule2 = new Rule();
        rule2.setCol("code");
        rule2.setReg("[A-Za-z0-9_!@#\\$%\\^&\\*\\(\\)\\|~`]");
        Plugin plugin2 = new Plugin();
        plugin2.setId(MD5Util.md5(taskInfo.getId() + "notEmpty"));
        plugin2.setName("非空值校验");
        plugin2.setCode("notEmpty");
        plugin2.setRules(new ArrayList<Rule>() {{
            add(rule2);
        }});

        Rule rule3 = new Rule();
        rule3.setCol("is_publish");
        rule3.setReg("^[-+]?[0-9]*\\.[0-9]{1}$");
        Plugin plugin3 = new Plugin();
        plugin3.setId(MD5Util.md5(taskInfo.getId() + "accuracy"));
        plugin3.setName("精度校验");
        plugin3.setCode("accuracy");
        plugin3.setRules(new ArrayList<Rule>() {{
            add(rule3);
        }});
        Rule rule4 = new Rule();
        rule4.setCol("description");
        rule4.setReg("^[1-9][0-9]{5}(18|19|([23][0-9]))[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9a-zA-Z]{3}[0-9Xx]$");
        Plugin plugin4 = new Plugin();
        plugin4.setId(MD5Util.md5(taskInfo.getId() + "ID"));
        plugin4.setName("身份证校验");
        plugin4.setCode("ID");
        plugin4.setRules(new ArrayList<Rule>() {{
            add(rule4);
        }});
        Rule rule5 = new Rule();
        rule5.setCol("level");
        rule5.setReg("^[01]{1}$");
        Plugin plugin5 = new Plugin();
        plugin5.setId(MD5Util.md5(taskInfo.getId() + "codomain"));
        plugin5.setName("值域校验");
        plugin5.setCode("codomain");
        plugin5.setRules(new ArrayList<Rule>() {{
            add(rule5);
        }});
        Rule rule6 = new Rule();
        rule6.setCol("item_order");
        rule6.setReg("^[1-9]{1}$");
        Plugin plugin6 = new Plugin();
        plugin6.setId(MD5Util.md5(taskInfo.getId() + "range"));
        plugin6.setName("范围校验");
        plugin6.setCode("range");
        plugin6.setRules(new ArrayList<Rule>() {{
            add(rule6);
        }});
        taskInfo.setPlugins(new ArrayList<Plugin>() {{
            add(plugin);
            add(plugin2);
            add(plugin3);
            add(plugin4);
            add(plugin5);
            add(plugin6);
        }});
        writePluginInfo(taskInfo);
        //taskInfo end
        CacheConfiguration cacheCfg = new CacheConfiguration();
        cacheCfg.setName(taskInfo.getTableName());
        cacheCfg.setBackups(0);
        IgniteCache<Object, Object> table_0 = ignite.getOrCreateCache(cacheCfg);
        IgniteCache<Object, Object> report = ignite.getOrCreateCache("report");
        IgniteCache<Object, Object> taskMajor = ignite.getOrCreateCache(taskInfo.getId() + "_major");
        IgniteCache<Object, Object> taskPlugin = ignite.getOrCreateCache(taskInfo.getId() + "_plugin");
        long start = System.currentTimeMillis();
        long loaded;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        conn = DSPool.getDataSource(
                taskInfo.getDriver(),
                taskInfo.getUrl(),
                taskInfo.getUsername(),
                taskInfo.getPassword()
        ).getConnection();
//        stmt = conn.prepareStatement("select * from dg_mysql_0", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        conn.setAutoCommit(false);
        stmt = conn.prepareStatement("select * from " + taskInfo.getTableName());
        int pkgNum = taskInfo.getPkgNum();
        stmt.setFetchSize(pkgNum);
        rs = stmt.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        String flowId = UUID.randomUUID().toString();
        //根据缓存名获取流处理器，并往流处理器中添加数据
        long index = 0;
        try (IgniteDataStreamer<String, Map<String, Object>> streamer = ignite.dataStreamer(taskInfo.getTableName())) {
            while (rs.next()) {
                HashMap<String, Object> row = Maps.newHashMap();
                JSONArray majorIds = JSON.parseArray(taskInfo.getMajorId());
                JSONObject jsonObject = new JSONObject();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
                    if (majorIds.contains(metaData.getColumnName(i))) {
                        jsonObject.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
                    }
                }
                streamer.allowOverwrite(true);
                streamer.receiver(StreamTransformer.from((e, arg) -> {
                    process(e, arg[0], taskInfo, taskPlugin);
                    return null;
                }));
                String majorId = MD5Util.md5(jsonObject.toJSONString());
                row.put("majorId", majorId);
                taskMajor.put(majorId, jsonObject.toJSONString());
                streamer.addData(majorId, row);
                index++;
                if (index % pkgNum == 0) {
                    qualityInspection(table_0, report, flowId, index, streamer, pkgNum, taskInfo, taskMajor, taskPlugin);
                }
            }
            if (index % pkgNum != 0) {
                qualityInspection(table_0, report, flowId, index, streamer, pkgNum, taskInfo, taskMajor, taskPlugin);
            }
            loaded = System.currentTimeMillis();
            System.out.printf("[%s]质检过程耗时 %s 毫秒\n", sdf.format(new Date()), loaded - start);
        }
        List<Map<String, Map<String, String>>> batchReport = report.query(new ScanQuery<String, Map<String, Map<String, String>>>((k, v) -> k.startsWith(flowId + "_")), Cache.Entry::getValue).getAll();
        for (Map<String, Map<String, String>> batch : batchReport) {
            for (Plugin taskInfoPlugin : taskInfo.getPlugins()) {
                Map<String, String> pluginMap = batch.get(taskInfoPlugin.getId());
                taskInfoPlugin.setTotal(Long.parseLong(pluginMap.get("total")));
                taskInfoPlugin.setUnQuality(Long.parseLong(pluginMap.get("unquality")));
                taskInfoPlugin.setQuality(Long.parseLong(pluginMap.get("total")) - Long.parseLong(pluginMap.get("unquality")));
            }
        }
        System.out.printf("[%s]生成质检报告耗时 %s 毫秒\n", sdf.format(new Date()), System.currentTimeMillis() - loaded);
        System.out.printf("[%s]任务ID[%s] 插件数量[%s] 执行批数量[%s]:\n", sdf.format(new Date()), flowId, taskInfo.getPlugins().size(), batchReport.size());
        for (Plugin taskInfoPlugin : taskInfo.getPlugins()) {
            System.out.printf("[%s]插件名称[%s] 插件ID[%s] 质检数据总量%s 合格数据%s条，不合格数据%s条\n", sdf.format(new Date()), taskInfoPlugin.getName(), taskInfoPlugin.getId(), taskInfoPlugin.getTotal(), taskInfoPlugin.getQuality(), taskInfoPlugin.getUnQuality());
        }
        System.out.printf("[%s]总耗时 %s 毫秒\n", sdf.format(new Date()), System.currentTimeMillis() - start);
        List<String> taskKey = report.query(new ScanQuery<String, Map<String, String>>((k, v) -> k.startsWith(flowId + "_")), Cache.Entry::getKey).getAll();
        report.removeAll(new HashSet<>(taskKey));
        ignite.destroyCache(taskInfo.getTableName());
        //stop ignite 实例
        ignite.close();
    }

    private static void process(MutableEntry<String, Map<String, Object>> e, Object o1, TaskInfo taskInfo, IgniteCache<Object, Object> taskPlugin) {
        Map<String, Object> o = (Map<String, Object>) o1;
        for (Plugin plugin : taskInfo.getPlugins()) {
            boolean pass = true;
            for (Rule rule : plugin.getRules()) {
                Pattern pattern = Pattern.compile(rule.getReg());
                String colValue = o.get(rule.getCol()) == null ? "" : o.get(rule.getCol()) + "";
                Matcher matcher = pattern.matcher(colValue);
                if (!matcher.find()) {
                    taskPlugin.put(o.get("majorId") + "_" + plugin.getId(), new JSONObject()
                            .fluentPut("majorId", o.get("majorId"))
                            .fluentPut("col", colValue)
                            .fluentPut("pluginId", plugin.getId())
                    );
                    pass = false;
                }
            }
            o.put(plugin.getId(), pass);
        }
        e.setValue(o);
    }

    private static void qualityInspection(IgniteCache<Object, Object> table_0, IgniteCache<Object, Object> report, String flowId, long index, IgniteDataStreamer<String, Map<String, Object>> streamer, int pkgNum, TaskInfo taskInfo, IgniteCache<Object, Object> taskMajor, IgniteCache<Object, Object> taskPlugin) {
        streamer.flush();
        Map<String, Object> batchReport = Maps.newHashMap();
        batchReport.put("batchId", UUID.randomUUID().toString());
        for (Plugin plugin : taskInfo.getPlugins()) {
            List<String> reg = table_0.query(new ScanQuery<String, Map<String, Object>>((k, v) -> !(boolean) v.get(plugin.getId())), Cache.Entry::getKey).getAll();
            boolean b = writeUnqualifiedData(taskInfo, reg, taskMajor);
            if (b) {
                System.out.printf("[%s]插件[%s]写入%s条数据至clickhouse成功\n", sdf.format(new Date()), plugin.getName(), reg.size());
            } else {
                System.out.printf("[%s]插件[%s]写入clickhouse失败\n", sdf.format(new Date()), plugin.getName());
            }
            boolean pluginDataResult = writePluginData(buildPluginData(taskInfo, taskPlugin));
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

    private static boolean writeUnqualifiedData(TaskInfo taskInfo, List<String> records, IgniteCache<Object, Object> taskMajor) {
        try {
            Connection connection = DSPool.getDataSource("ru.yandex.clickhouse.ClickHouseDriver", "jdbc:clickhouse://192.168.80.43:8123/default", "default", "123456").getConnection();
            String sql = "INSERT INTO default.dq_task_main (id,task_id,table_name,major_json,major_hash_code,create_time) VALUES (?,?,?,?,?,?);";
            return executeBatchWithParams(sql, buildMasterData(taskInfo, records, taskMajor), connection);
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

    private static boolean writePluginInfo(TaskInfo taskInfo) {
        try {
            Connection connection = DSPool.getDataSource("ru.yandex.clickhouse.ClickHouseDriver", "jdbc:clickhouse://192.168.80.43:8123/default", "default", "123456").getConnection();
            String sql = "INSERT INTO default.dq_task_plugin_main (id,task_id,plugin_code,plugin_id,plugin_name,create_time) VALUES (?,?,?,?,?,?);";
            return executeBatchWithParams(sql, buildPlugin(taskInfo), connection);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    private static List<Object[]> buildMasterData(TaskInfo taskInfo, List<String> records, IgniteCache<Object, Object> taskMajor) {
        List<Object[]> result = Lists.newArrayList();
        for (String record : records) {
            Object[] data = new Object[6];
            data[0] = UUID.randomUUID().toString();
            data[1] = taskInfo.getId();
            data[2] = taskInfo.getTableName();
            data[3] = taskMajor.get(record);
            data[4] = record;
            data[5] = sdf.format(new Date());
            result.add(data);
        }
        return result;
    }

    private static List<Object[]> buildPluginData(TaskInfo taskInfo, IgniteCache<Object, Object> taskPlugin) {
        List<Object[]> result = Lists.newArrayList();
        for (Map<String, Object> pluginData : taskPlugin.query(new ScanQuery<String, Map<String, Object>>((k, v) -> !k.isEmpty()), Cache.Entry::getValue)) {
            Object[] data = new Object[7];
            data[0] = UUID.randomUUID().toString();
            data[1] = pluginData.get("majorId");
            data[2] = pluginData.get("pluginId");
            data[3] = pluginData.get("col");
            data[4] = sdf.format(new Date());
            data[5] = "0";
            data[6] = taskInfo.getId();
            result.add(data);
        }
        return result;
    }

    private static List<Object[]> buildPlugin(TaskInfo taskInfo) {
        List<Object[]> result = Lists.newArrayList();
        for (Plugin plugin : taskInfo.getPlugins()) {
            Object[] data = new Object[6];
            data[0] = UUID.randomUUID().toString();
            data[1] = taskInfo.getId();
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
