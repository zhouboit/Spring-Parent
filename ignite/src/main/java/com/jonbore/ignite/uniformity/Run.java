package com.jonbore.ignite.uniformity;

import com.jonbore.ignite.process.data.Plugin;
import com.jonbore.ignite.process.data.Rule;
import com.jonbore.ignite.process.data.Task;
import com.jonbore.ignite.util.IgniteNode;
import com.jonbore.ignite.util.Option;

import java.util.ArrayList;
import java.util.UUID;

public class Run {
    public static void main(String[] args) throws Exception {
        Option option = Option.fromArgs(args);
        IgniteNode.start(option);
        Task task = new Task();
        task.setTableName("mes_sys_app_20000");
        task.setId(UUID.randomUUID().toString());
        task.setDriver("com.mysql.cj.jdbc.Driver");
        task.setUrl("jdbc:mysql://192.168.80.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8");
        task.setUsername("root");
        task.setPassword("123456");
        task.setPkgNum(20000);
        Rule rule = new Rule();
        rule.setCol("home_page_url");
        rule.setSql("select home_page_url as dic from mes_sys_app_000");
        Plugin plugin = new Plugin();
        plugin.setId(UUID.randomUUID().toString());
        plugin.setName("一致性校验");
        plugin.setCode("uniformity");
        plugin.setRules(new ArrayList<Rule>() {{
            add(rule);
        }});
        Load2Ignite.loadData2Ignite(task);
    }
}
