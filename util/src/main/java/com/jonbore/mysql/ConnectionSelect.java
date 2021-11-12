package com.jonbore.mysql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bo.zhou
 * @date 2020/12/30 下午4:53
 */
public class ConnectionSelect {

    public static void getMySQLData(Configuration configuration) {
        HashSet<String> strings = new HashSet<>();
        try {
            HashSet<String> javaFn = getJavaFn(configuration);
            HashSet<String> function = getFunction(configuration);
            strings.addAll(javaFn);
            strings.addAll(function);
            List<String> collect = strings.stream().collect(Collectors.toList());
            Collections.sort(collect);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(collect));
            System.out.println(collect.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashSet<String> getJavaFn(Configuration configuration) {
        HashSet<String> strings = new HashSet<>();
        try {
            Class.forName(configuration.getDriver());
            Connection connection = DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM DEV_API_PLUGIN_PARAM");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String s_plugin_json = resultSet.getString("S_PLUGIN_JSON");
                if (s_plugin_json != null && !s_plugin_json.trim().isEmpty()) {
                    JSONObject jsonObject = JSON.parseObject(s_plugin_json);
                    if (jsonObject != null && jsonObject.containsKey("script") && jsonObject.getString("script") != null) {
                        String[] scripts = jsonObject.getString("script").split("\n");
                        for (String script : scripts) {
                            if (script.contains("import ")) {
                                String s = script.trim().split(";")[0];
                                if (s.startsWith("import") && !s.endsWith("*")) {
                                    strings.add(s.split(" ")[1]);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }

    public static HashSet<String> getFunction(Configuration configuration) {
        HashSet<String> strings = new HashSet<>();
        try {
            Class.forName(configuration.getDriver());
            Connection connection = DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
            PreparedStatement statement = connection.prepareStatement("SELECT\n" +
                    "\t*\n" +
                    "FROM\n" +
                    "DEV_FUNCTION");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String s_plugin_json = resultSet.getString("S_SCRIPT");
                if (s_plugin_json != null && !s_plugin_json.trim().isEmpty()) {
                    String[] scripts = s_plugin_json.split("\n");
                    for (String script : scripts) {
                        if (script.contains("import ")) {
                            String s = script.trim().split(";")[0];
                            if (s.startsWith("import") && !s.endsWith("*")) {
                                strings.add(s.split(" ")[1]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }
}
