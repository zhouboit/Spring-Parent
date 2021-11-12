package com.jonbore.spi.main;

import com.jonbore.spi.api.Execute;

import java.util.ServiceLoader;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/8/2
 */
public class Run {
    public static void main(String[] args) {
        int max = Math.max(1, 2);
        String a = "";
        Thread thread = Thread.currentThread();
        ServiceLoader<Execute> executes = ServiceLoader.load(Execute.class);
        for (Execute execute : executes) {
            execute.print();
        }
    }
}
