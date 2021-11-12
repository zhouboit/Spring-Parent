package com.jonbore.web.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * http 请求客户端工具
 *
 * @author bo.zhou
 * @date 2021/1/15 上午8:28
 */
public class HttpClient {
    private static HttpClient client = null;
    private static CloseableHttpClient httpclient;
    private static String baseUrl = null;

    public static HttpClient instance(String url) {
        baseUrl = url;
        if (client == null) {
            synchronized (HttpClient.class) {
                if (client == null) {
                    client = new HttpClient();
                }
                if (httpclient == null) {
                    RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(3000).build();
                    httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
                }
            }
        }
        return client;
    }


    /**
     * 发送http get请求
     *
     * @param path    请求路径（相对路径，不含IP、PORT）
     * @param headers 请求头
     * @param body    请求发送数据
     * @return string 类型的字符串
     * @author bo.zhou
     * @date 2021-01-15
     */
    public String sendGet(String path, JSONObject headers, JSONObject body) {
        String finalPath = path;
        if (body != null && !body.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(body.size());
            body.forEach((k, v) -> {
                if (v == null) {
                    pairs.add(new BasicNameValuePair(k, ""));
                } else {
                    pairs.add(new BasicNameValuePair(k, v.toString()));
                }
            });
            try {
                if (!pairs.isEmpty()) {
                    path += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        HttpGet httpGet = new HttpGet(baseUrl + path);
        if (headers != null && !headers.isEmpty()) {
            httpGet.addHeader(Headers.USER_AGENT, Headers.CUSTOMER_AGENT);
            headers.forEach((k, v) -> {
                if (v == null) {
                } else {
                    httpGet.addHeader(k, v.toString());
                }
            });

        }
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
                response.close();
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 发送http post请求
     *
     * @param path    请求路径（相对路径，不含IP、PORT）
     * @param headers 请求头
     * @param body    请求发送数据
     * @return string 类型的字符串
     * @author bo.zhou
     * @date 2021-01-15
     */
    public String sendPost(String path, JSONObject headers, JSONObject body) {
        HttpPost httpPost = new HttpPost(baseUrl + path);
        if (body != null && !body.isEmpty()) {
            try {
                httpPost.setEntity(new StringEntity(JSON.toJSONString(body), StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (headers != null && !headers.isEmpty()) {
            httpPost.addHeader(Headers.USER_AGENT, Headers.CUSTOMER_AGENT);
            headers.forEach((k, v) -> {
                if (v == null) {
                } else {
                    httpPost.addHeader(k, v.toString());
                }
            });

        }
        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                httpPost.abort();
                System.out.println(httpPost.getRequestLine());
                System.out.println(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
                throw new RuntimeException("response status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                EntityUtils.consume(entity);
                response.close();
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
