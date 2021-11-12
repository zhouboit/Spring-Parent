package com.jonbore.web.client;

import com.alibaba.fastjson.JSONObject;

import java.util.Base64;

/**
 * @author bo.zhou
 * @date 2020/9/8 下午1:19
 */
public class Headers {
    public static final String ACCEPT = "Accept";
    public static final String AUTHORIZATION = "Authorization";
    public static final String HOST = "Host";
    public static final String USER_AGENT = "User-Agent";
    public static final String CONTENT_TYPE = "Content-Type";

    public static String authorization(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static final String ACCEPT_ALL = "*/*";
    public static final String CUSTOMER_AGENT = "Jonbore";
    public static final String CONTENT_TYPE_XML = "text/xml";

    public static void main(String[] args) {
        System.out.println(new JSONObject().fluentPut("error", "请传入认证令牌 tokenId").toString());
    }
}
