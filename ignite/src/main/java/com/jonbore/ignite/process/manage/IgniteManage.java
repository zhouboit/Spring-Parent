package com.jonbore.ignite.process.manage;

import com.alibaba.fastjson.JSON;
import com.jonbore.ignite.util.IgniteNode;
import com.jonbore.ignite.util.Option;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteCallable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class IgniteManage {
    public static void main(String[] args) {
        Option option = Option.fromArgs(args);
        IgniteNode.start(option);
        Ignite ignite = Ignition.ignite();
        IgniteCompute compute = ignite.compute();
        Collection<IgniteCallable<Map<String, Object>>> igniteTaskBatch = new LinkedList<>();

        compute.execute(Task.class, "i have a dream");
//        compute.
        System.out.println(JSON.toJSONString(compute.localTasks()));
        ignite.close();
    }
}
