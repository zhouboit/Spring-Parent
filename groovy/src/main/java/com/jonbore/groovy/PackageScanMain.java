package com.jonbore.groovy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jonbore.groovy.util.JarUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/10/25
 */
public class PackageScanMain {
    public static void main(String[] args) throws IOException {
        List<String> collect = new ArrayList<>(JarUtil.scanClass("com.jonbore.groovy.util"));
        Collections.sort(collect);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(collect));
        System.out.println(collect.size());
    }
}
