package com.jonbore.groovy.util;


import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static java.util.zip.ZipFile.OPEN_READ;

public class JarUtil {
    /**
     * 扫描普通应用环境变量中的包下class（不包含jdk中的）
     *
     * @param packageName 指定包名
     * @return 扫描到的class名称集合
     * @author bo.zhou
     * @since 2021/10/26
     */
    public static Set<String> scanClass(String packageName) {
        HashSet<String> scanClass = new HashSet<>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    if (url.getProtocol().equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            scanClass(scanClass, jarFile, packageName);
                        }
                    } else if (url.getProtocol().equals("file")) {
                        List<String> classList = Arrays.stream(Objects.requireNonNull(new File(url.getPath()).listFiles())).map(File::getName).filter(name -> name.endsWith(".class")).map(name -> packageName.concat(".").concat(name.substring(0, name.length() - ".class".length()))).collect(Collectors.toList());
                        scanClass.addAll(classList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scanClass;
    }

    /**
     * 扫描jdk中指定包下的class
     *
     * @param packageName 指定包名
     * @return 扫描到的class名称集合
     * @author bo.zhou
     * @since 2021/10/26
     */
    public static Set<String> scanBootstrapClass(String packageName) {
        HashSet<String> scanClass = new HashSet<>();
        try {
            for (String s : System.getProperty("sun.boot.class.path").split(";")) {
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(new File(s.trim()), true, OPEN_READ);
                } catch (Exception e) {
                    continue;
                }
                scanClass(scanClass, jarFile, packageName);
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scanClass;
    }

    /**
     * 根据指定的包名将jarFile中符合如下条件的文件名称添加到scanClass集合中
     * 条件:
     * 1. class文件结尾
     * 2. 不包含内部类(类名中包含$的)
     * 3. 指定包下的class，不包含子包
     *
     * @param scanClass   类名结果集合
     * @param jarFile     jar文件对象
     * @param packageName 指定包名
     * @author bo.zhou
     * @since 2021/10/26
     */
    private static void scanClass(HashSet<String> scanClass, JarFile jarFile, String packageName) {
        if (jarFile != null) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && !name.contains("$")) {
                    String className = name.substring(0, name.lastIndexOf(".")).replaceAll("/", ".");
                    if (className.startsWith(packageName) && className.split("\\.").length == (packageName.split("\\.").length + 1)) {
                        scanClass.add(className);
                    }
                }
            }
        }
    }
}