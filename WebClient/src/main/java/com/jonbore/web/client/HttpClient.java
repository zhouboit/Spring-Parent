package com.jonbore.web.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

/**
 * http 请求客户端工具
 *
 * @author bo.zhou
 * @date 2021/1/15 上午8:28
 */
public class HttpClient {

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
    public static String sendPost(String path, JSONObject headers, JSONObject body) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(3000).build();
        CloseableHttpClient build = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpPost httpPost = new HttpPost(path);
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
            CloseableHttpResponse response = build.execute(httpPost);
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
