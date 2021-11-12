package com.jonbore.gofastdfs;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/8/16
 */
public class Client {
    public static void main(String[] args) {
        String fileName = "bcprov-jdk15-144.jar";
//        String fileName = "winutils-master.zip";
        String url = "http://192.168.81.132:8888/group1/default/20210817/08/54/5/" + fileName;
//        String url = "http://192.168.81.132:8888/group1/default/20210817/09/44/5/" + fileName;
        String localFile = "C:\\Users\\bo.zhou\\Downloads\\" + System.currentTimeMillis() + "\\" + fileName;
        dowaload(url, localFile);
    }

    private static void dowaload(String url, String localFile) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL remoteURL = new URL(url);
            File file = new File(localFile);
            if (file.exists()) {
                file.delete();
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            httpURLConnection = (HttpURLConnection) remoteURL.openConnection();
            httpURLConnection.setConnectTimeout(25000);
            httpURLConnection.setReadTimeout(25000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Accept", "application/octet-stream");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            httpURLConnection.setRequestProperty("Host", "192.168.81.132:8888");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");
            httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            byte[] buff = new byte[1024 * 1024 * 10];
            int len = 0;
            while ((len = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            Objects.requireNonNull(httpURLConnection).disconnect();
        }
    }
}
