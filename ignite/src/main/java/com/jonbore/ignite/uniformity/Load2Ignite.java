package com.jonbore.ignite.uniformity;

import com.jonbore.ignite.process.data.Plugin;
import com.jonbore.ignite.process.data.Rule;
import com.jonbore.ignite.process.data.Task;
import com.jonbore.ignite.util.DSPool;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Load2Ignite {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void loadData2Ignite(Task task) throws Exception {
        Ignite ignite = Ignition.ignite();
        Plugin plugin = task.getPlugins().get(0);
        Rule rule = plugin.getRules().get(0);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        long start = System.currentTimeMillis();
        long loaded;
        conn = DSPool.getDataSource(
                task.getDriver(),
                task.getUrl(),
                task.getUsername(),
                task.getPassword()
        ).getConnection();
        conn.setAutoCommit(false);
        stmt = conn.prepareStatement(rule.getSql());
        stmt.setFetchSize(task.getPkgNum());
        rs = stmt.executeQuery();
        try (IgniteDataStreamer<String, String> streamer = ignite.dataStreamer(plugin.getId() + "_" + "table" + "_" + rule.getCol())) {
            while (rs.next()) {
                streamer.allowOverwrite(true);
                streamer.addData(String.valueOf(rs.getObject("dic")), String.valueOf(rs.getObject("dic")));
            }
            loaded = System.currentTimeMillis();
            System.out.printf("[%s]质检加载关联数据耗时 %s 毫秒\n", sdf.format(new Date()), loaded - start);
        }
    }
}
