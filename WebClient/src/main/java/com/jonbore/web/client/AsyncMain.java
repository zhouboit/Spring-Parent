package com.jonbore.web.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

/**
 * @author bo.zhou
 * @since 2021/11/12
 */
public class AsyncMain {
    public static void main(String[] args) {
        send();
    }

    private static void send() {
        JSONObject header = new JSONObject();
        header.put("Cookie", "IOVTOKEN=343C9A62C3504B25B223EA21D02ADDE1; userType=0");
        header.put("IOVTOKEN", "343C9A62C3504B25B223EA21D02ADDE1");
        header.put("DEPARTMENTID", "12DFC45F409A43B4A260B3A6BE37CD66");
        header.put("Content-Type", "application/json;charset=utf-8");
        String body = "{\n" +
                "    \"condition\": {\n" +
                "        \"status\": \"1\",\n" +
                "        \"search\": \"\",\n" +
                "        \"projectIdList\": []\n" +
                "    },\n" +
                "    \"pageSize\": 500,\n" +
                "    \"pageNum\": 1\n" +
                "}";
        String result = HttpClient.sendPost("http://192.168.80.45:8088/devOps/workday/getWorkDayApprove", header, JSON.parseObject(body));
        if (result == null || result.trim().isEmpty()) {
            System.out.println("http request exception");
            System.exit(127);
        }
        JSONArray jsonArray = JSON.parseObject(result).getJSONObject("content").getJSONArray("rows");
        System.out.println("待审批数量:" + jsonArray.size());
        String[] projects = {"新一代警综平台", "业务平台"};
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject approverBody = jsonArray.getJSONObject(i);
            if (Arrays.asList(projects).contains(approverBody.getString("projectName"))) {
                approverBody.entrySet().removeIf(next -> next.getValue().toString().equals(""));
                approverBody
                        .fluentPut("status", "2")
                        .fluentPut("approverOpinions", "同意");
                String approver = HttpClient.sendPost("http://192.168.80.45:8088/devOps/workday/workDayApproval", header, approverBody);
                System.out.println(approver);
            }
        }
    }
}
