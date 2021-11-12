package com.jonbore.web.client;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/9/2
 */
public class EnvMain {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getenv("user.home"));
        System.out.println("===============");
        System.getenv().forEach((k, v) -> {
            System.out.printf("key:%s val:%s%n", k, v);
        });
        System.getProperties().forEach((k, v) -> {
            System.out.printf("key:%s val:%s%n", k, v);
        });
    }
}
