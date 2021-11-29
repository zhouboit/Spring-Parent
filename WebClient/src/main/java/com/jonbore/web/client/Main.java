package com.jonbore.web.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/8/27
 */
public class Main {
    public static void main(String[] args) {
        Option option = Option.fromArgs(args);
        System.out.println("usage option:\n" +
                "\t--host|-host: Network  ip address of uni-biz service,eg. 192.168.81.134\n" +
                "\t--port|-port: Network listening port address of uni-biz service,eg. 9528\n" +
                "\t--token|-token: Network request header authentication attribute to uni-biz service,eg. A62D4BC8CFDF404F8A98349C07D624D3\n" +
                "\t--dataFile|-dataFile: Config file contains query condition，matcher condition and set data,eg. data.json\n" +
                "\t\t data structure {where: {WWWWW1: WWWWW1},contains: {CCCCC1:CCCCC1V,CCCCC2:CCCCC2V},data: {DDDDD1:DDDDD1V,DDDDD2:DDDDD2V}} \n" +
                "\n"
        );
        String server = "";
        if (!option.has("host") || option.get("host").trim().isEmpty()) {
            System.out.println("Please appoint network  ip address of uni-biz service，with -host or --host");
            System.exit(127);
        }
        server = option.get("host");
        String port = "";
        if (!option.has("port") || option.get("port").trim().isEmpty()) {
            System.out.println("Please appoint network listening port address of uni-biz service，with -port or --port");
            System.exit(127);
        }
        port = option.get("port");
        String token = "";
        if (!option.has("token") || option.get("token").trim().isEmpty()) {
            System.out.println("Please appoint network request header authentication attribute to uni-biz service，with -token or --token");
            System.exit(127);
        }
        token = option.get("token");
        String dataPath = "";
        if (!option.has("dataFile") || option.get("dataFile").trim().isEmpty()) {
            System.out.println("Please appoint config file，with -dataFile or --dataFile");
            System.exit(127);
        }
        dataPath = option.get("dataFile");
        String readFile = FileUtil.readFile(dataPath);
        JSONObject data = JSON.parseObject(readFile);
        String baseUrl = String.format("http://%s:%s/design/frontdevelop/webAppComponent", server, port);
        JSONObject header = new JSONObject();
        header.put("Cookie", "tokenId=" + token);
        header.put("Content-Type", "application/json;charset=utf-8");
        String byCondition = baseUrl + "/byCondition";
        String updating = baseUrl + "/updating";
//        HttpClient instance = HttpClient.instance(baseUrl);
        String result = HttpClient.sendPost(byCondition, header, data.getJSONObject("where"));
        if (result == null || result.trim().isEmpty()) {
            System.out.println("http request exception");
            System.exit(127);
        }
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray content = jsonObject.getJSONArray("content");
        int edit = 0;
        for (Object o : content) {
            JSONObject row = (JSONObject) o;
            JSONObject config = row.getJSONObject("config");
            boolean flag = false;
            for (String contains : data.getJSONObject("contains").keySet()) {
                if (config.containsKey(contains) && config.getString(contains).equals(data.getJSONObject("contains").getString(contains))) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                config.putAll(data.getJSONObject("data"));
                row.put("config", config.toJSONString());
                String sendPost = HttpClient.sendPost(updating, header, row);
                System.out.println(sendPost);
                edit++;
            }
        }
        System.out.printf("total query %s record, revise %s record data", content.size(), edit);
    }
}
