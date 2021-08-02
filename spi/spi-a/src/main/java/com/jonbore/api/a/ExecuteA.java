package com.jonbore.api.a;

import com.jonbore.spi.api.Execute;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/8/2
 */
public class ExecuteA implements Execute {
    @Override
    public void print() {
        System.out.println(getClass().getCanonicalName());
    }
}
