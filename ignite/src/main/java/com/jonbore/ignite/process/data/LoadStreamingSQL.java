package com.jonbore.ignite.process.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jonbore.ignite.util.DSPool;
import com.jonbore.ignite.util.IgniteNode;
import com.jonbore.ignite.util.MD5Util;
import com.jonbore.ignite.util.Option;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
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
    public static final String DP_SUCCESS = "dpSuccess";
    public static final String DP_FAIL = "dpFail";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        Option option = Option.fromArgs(args);
        IgniteNode.start(option);
        Ignite ignite = Ignition.ignite();
        //mock task start
        Task task = new Task();
//                "com.mysql.cj.jdbc.Driver",
//                "org.postgresql.Driver",
//                "jdbc:mysql://192.168.80.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8",
//                "jdbc:postgresql://192.168.80.44:1328/cindy",
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
        plugin.setId(UUID.randomUUID().toString());
        plugin.setName("????????????");
        plugin.setCode("regularExpression");
        plugin.setRules(new ArrayList<Rule>() {{
            add(rule);
        }});
//        {"dataMap":{"schema":"hfc_test","password":"","driver":"ru.yandex.clickhouse.ClickHouseDriver","table_name":"dq_data_result","url":"jdbc:clickhouse://192.168.5.131:8123/hfc_test","username":"default"}}
        Rule rule2 = new Rule();
        rule2.setCol("code");
        rule2.setReg("[A-Za-z0-9_!@#\\$%\\^&\\*\\(\\)\\|~`]");
        Plugin plugin2 = new Plugin();
        plugin2.setId(UUID.randomUUID().toString());
        plugin2.setName("???????????????");
        plugin2.setCode("notEmpty");
        plugin2.setRules(new ArrayList<Rule>() {{
            add(rule2);
        }});

        Rule rule3 = new Rule();
        rule3.setCol("is_publish");
        rule3.setReg("^[-+]?[0-9]*\\.[0-9]{1}$");
        Plugin plugin3 = new Plugin();
        plugin3.setId(UUID.randomUUID().toString());
        plugin3.setName("????????????");
        plugin3.setCode("accuracy");
        plugin3.setRules(new ArrayList<Rule>() {{
            add(rule3);
        }});
        Rule rule4 = new Rule();
        rule4.setCol("description");
        rule4.setReg("^[1-9][0-9]{5}(18|19|([23][0-9]))[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9a-zA-Z]{3}[0-9Xx]$");
        Plugin plugin4 = new Plugin();
        plugin4.setId(UUID.randomUUID().toString());
        plugin4.setName("???????????????");
        plugin4.setCode("ID");
        plugin4.setRules(new ArrayList<Rule>() {{
            add(rule4);
        }});
        Rule rule5 = new Rule();
        rule5.setCol("level");
        rule5.setReg("^(0|1)$");
        Plugin plugin5 = new Plugin();
        plugin5.setId(UUID.randomUUID().toString());
        plugin5.setName("????????????");
        plugin5.setCode("codomain");
        plugin5.setRules(new ArrayList<Rule>() {{
            add(rule5);
        }});
        Rule rule6 = new Rule();
        rule6.setCol("item_order");
        rule6.setReg("^[1-9]{1}$");
        Plugin plugin6 = new Plugin();
        plugin6.setId(UUID.randomUUID().toString());
        plugin6.setName("????????????");
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
        writePluginInfo(task);
        //task end
        CacheConfiguration cacheCfg = new CacheConfiguration();
        cacheCfg.setName(task.getId());
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
        stmt.setFetchSize(task.getPkgNum());
        rs = stmt.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        //?????????????????????????????????????????????????????????????????????
        long index = 0;
        try (IgniteDataStreamer<String, Map<String, Object>> streamer = ignite.dataStreamer(task.getId())) {
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
                    process(e, arg[0], task);
                    return null;
                }));
                String majorId = MD5Util.md5(jsonObject.toJSONString());
                row.put("majorId", majorId);
                taskMajor.put(majorId, jsonObject.toJSONString());
                streamer.addData(majorId, row);
                index++;
                if (index % task.getPkgNum() == 0) {
                    qualityInspection(table_0, report, index, streamer, task.getPkgNum(), task);
                }
            }
            if (index % task.getPkgNum() != 0) {
                qualityInspection(table_0, report, index, streamer, task.getPkgNum(), task);
                ignite.destroyCache(task.getId() + "_major");
                ignite.destroyCache(task.getId() + "_plugin");
            }
            loaded = System.currentTimeMillis();
            System.out.printf("[%s]?????????????????? %s ??????\n", sdf.format(new Date()), loaded - start);
        }
        List<Map<String, Map<String, String>>> batchReport = report.query(new ScanQuery<String, Map<String, Map<String, String>>>((k, v) -> k.startsWith(task.getId() + "_")), Cache.Entry::getValue).getAll();
        for (Map<String, Map<String, String>> batch : batchReport) {
            for (Plugin taskInfoPlugin : task.getPlugins()) {
                Map<String, String> pluginMap = batch.get(taskInfoPlugin.getId());
                taskInfoPlugin.setTotal(Long.parseLong(pluginMap.get("total")));
                taskInfoPlugin.setUnQuality(Long.parseLong(pluginMap.get("unquality")));
                taskInfoPlugin.setQuality(Long.parseLong(pluginMap.get("total")) - Long.parseLong(pluginMap.get("unquality")));
            }
        }
        System.out.printf("[%s]???????????????????????? %s ??????\n", sdf.format(new Date()), System.currentTimeMillis() - loaded);
        System.out.printf("[%s]??????ID[%s] ????????????[%s] ???????????????[%s]:\n", sdf.format(new Date()), task.getId(), task.getPlugins().size(), batchReport.size());
        for (Plugin taskInfoPlugin : task.getPlugins()) {
            System.out.printf("[%s]????????????[%s] ??????ID[%s] ??????????????????%s ????????????%s?????????????????????%s???\n", sdf.format(new Date()), taskInfoPlugin.getName(), taskInfoPlugin.getId(), taskInfoPlugin.getTotal(), taskInfoPlugin.getQuality(), taskInfoPlugin.getUnQuality());
        }
        System.out.printf("[%s]????????? %s ??????\n", sdf.format(new Date()), System.currentTimeMillis() - start);
        List<String> taskKey = report.query(new ScanQuery<String, Map<String, String>>((k, v) -> k.startsWith(task.getId() + "_")), Cache.Entry::getKey).getAll();
        report.removeAll(new HashSet<>(taskKey));
        ignite.destroyCache(task.getId());
        //stop ignite ??????
        ignite.close();
    }

    private static void process(MutableEntry<String, Map<String, Object>> e, Object o1, Task task) {
        IgniteCache<Object, Object> taskPlugin = Ignition.ignite().getOrCreateCache(task.getId() + "_plugin");
        Map<String, Object> o = (Map<String, Object>) o1;
        for (Plugin plugin : task.getPlugins()) {
            String dpRs = DP_SUCCESS;
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
                    dpRs = DP_FAIL;
                }
            }
            o.put(plugin.getId(), dpRs);
        }
        e.setValue(o);
    }

    private static void qualityInspection(IgniteCache<Object, Object> tableData, IgniteCache<Object, Object> report, long index, IgniteDataStreamer<String, Map<String, Object>> streamer, int pkgNum, Task task) {
        IgniteCache<Object, Object> taskMajor = Ignition.ignite().getOrCreateCache(task.getId() + "_major");
        IgniteCache<Object, Object> taskPlugin = Ignition.ignite().getOrCreateCache(task.getId() + "_plugin");
        streamer.flush();
        Map<String, Object> batchReport = Maps.newHashMap();
        batchReport.put("batchId", UUID.randomUUID().toString());
        List<String> mainSlave = tableData.query(new ScanQuery<String, Map<String, Object>>((k, v) -> v.containsValue(DP_FAIL)), Cache.Entry::getKey).getAll();
        boolean slave = writeUnqualifiedData(task, mainSlave, taskMajor);
        if (slave) {
            System.out.printf("[%s]??????%s?????????????????????clickhouse??????\n", sdf.format(new Date()), mainSlave.size());
        } else {
            System.out.printf("[%s]?????????????????????clickhouse??????\n", sdf.format(new Date()));
        }
        for (Plugin plugin : task.getPlugins()) {
            List<String> reg = tableData.query(new ScanQuery<String, Map<String, Object>>((k, v) -> v.get(plugin.getId()).toString().equals(DP_FAIL)), Cache.Entry::getKey).getAll();
            boolean pluginDataResult = writePluginData(buildPluginData(task, taskPlugin));
            if (pluginDataResult) {
                System.out.printf("[%s]??????[%s]????????????clickhouse??????\n", sdf.format(new Date()), plugin.getName());
            } else {
                System.out.printf("[%s]??????[%s]????????????clickhouse??????\n", sdf.format(new Date()), plugin.getName());
            }
            Map<String, String> pluginReport = Maps.newHashMap();
            pluginReport.put("total", ((index % pkgNum) == 0 ? pkgNum : index / pkgNum) + "");
            pluginReport.put("unquality", reg.size() + "");
            pluginReport.put("pluginId", plugin.getId());
            batchReport.put(plugin.getId(), pluginReport);
        }
        report.put(task.getId() + "_" + batchReport.get("batchId"), batchReport);
        tableData.clear();
        taskPlugin.clear();
        taskMajor.clear();
        System.out.printf("[%s]????????? %s ????????????????????? %s ?????????\n", sdf.format(new Date()), (index % pkgNum) == 0 ? index / pkgNum : (index / pkgNum) + 1, index);
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
