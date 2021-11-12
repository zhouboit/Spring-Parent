package com.jonbore.groovy.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.zip.ZipFile.OPEN_READ;

public class JarFileParse {
    public static void main(String[] args) {
        HashSet<String> scanClass = new HashSet<>();
        for (String s : System.getProperty("sun.boot.class.path").split(";")) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(new File(s.trim()), true, OPEN_READ);
            } catch (Exception e) {
                continue;
            }
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && !name.contains("$")) {
                    String className = name.substring(0, name.lastIndexOf(".")).replaceAll("/", ".");
                    scanClass.add(className);
                }
            }
            try {
                jarFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> collect = new ArrayList<>(scanClass);
        Collections.sort(collect);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(collect));
        System.out.println(collect.size());

    }
}
