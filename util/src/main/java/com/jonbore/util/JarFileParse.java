package com.jonbore.util;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static java.util.zip.ZipFile.OPEN_READ;

public class JarFileParse {
    public static void main(String[] args) {
        try {
            JarFile jarFile = new JarFile(new File("D:\\code\\GIt\\dataHub\\Json-Parse\\target\\Json-Parse-1.0-SNAPSHOT.jar"), true, OPEN_READ);
            Manifest manifest = jarFile.getManifest();
            for (Map.Entry<Object, Object> objectObjectEntry : manifest.getMainAttributes().entrySet()) {
                System.out.println(String.format("%s : %s", objectObjectEntry.getKey(), objectObjectEntry.getValue()));
            }
            InputStream inputStream = pluginIdx2Stream(new File("D:\\code\\GIt\\dataHub\\Json-Parse\\target\\Json-Parse-1.0-SNAPSHOT.jar"));
            if (inputStream != null && inputStream.available() > 0) {
                List<String> strings = stream2LineListIgnoreExplanatory(inputStream);
                for (String string : strings) {
                    System.out.println(string);
                }
            } else {
                //to do something explanatory
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputStream pluginIdx2Stream(File file) {
        InputStream inputStream = null;
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                System.out.println(name);
                if (name.endsWith(".idx")) {
                    JarEntry entry = jarFile.getJarEntry(name);
                    inputStream = jarFile.getInputStream(entry);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 数据文件，返回文件内容
     *
     * @param inputStream
     * @return
     */
    public static List<String> stream2LineListIgnoreExplanatory(InputStream inputStream) {
        BufferedReader reader = null;
        List<String> list = Lists.newArrayList();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                list.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
