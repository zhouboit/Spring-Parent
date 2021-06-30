package com.jonbore.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class JSONParse {
    public static void main(String[] args) {
        String jsonObjStr = "{'name':'jon'}";
        String jsonArrayStr = "[{'name':'jon'}]";
        Object fastjson = JSON.parse(jsonObjStr);
        if(fastjson instanceof JSONArray){
            System.out.println("array");
        } else {
            System.out.println("object");
        }
    }
}
