package com.jonbore.ignite.process.task;

import com.jonbore.ignite.util.IgniteNode;
import com.jonbore.ignite.util.Option;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.Ignition;

public class RunnerTask {
    public static void main(String[] args) {
        Option option = Option.fromArgs(args);
        IgniteNode.start(option);
        Ignite ignite = Ignition.ignite();
        IgniteCompute compute = ignite.compute();

    }
}
