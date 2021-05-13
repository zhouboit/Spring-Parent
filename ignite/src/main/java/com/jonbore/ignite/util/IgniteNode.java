package com.jonbore.ignite.util;

import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.*;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;
import org.apache.ignite.spi.failover.always.AlwaysFailoverSpi;
import org.apache.ignite.spi.loadbalancing.roundrobin.RoundRobinLoadBalancingSpi;

public class IgniteNode {
    public static void start(Option option){
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setIncludeEventTypes(org.apache.ignite.events.EventType.EVT_NODE_LEFT, org.apache.ignite.events.EventType.EVT_NODE_FAILED);
        RoundRobinLoadBalancingSpi roundRobinLoadBalancingSpi = new RoundRobinLoadBalancingSpi();
        roundRobinLoadBalancingSpi.setPerTask(true);
        igniteConfiguration.setLoadBalancingSpi(roundRobinLoadBalancingSpi);
        AlwaysFailoverSpi alwaysFailoverSpi = new AlwaysFailoverSpi();
        alwaysFailoverSpi.setMaximumFailoverAttempts(5);
        igniteConfiguration.setFailoverSpi(alwaysFailoverSpi);
        ExecutorConfiguration executorConfiguration = new ExecutorConfiguration();
        executorConfiguration.setName("myPool");
        executorConfiguration.setSize(20);
        igniteConfiguration.setExecutorConfiguration(executorConfiguration);
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        igniteConfiguration.setPublicThreadPoolSize(16);
        igniteConfiguration.setSystemThreadPoolSize(8);
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setPageSize(8 * 1024);
        dataStorageConfiguration.setWriteThrottlingEnabled(true);
        dataStorageConfiguration.setWalSegmentSize(1024 * 1024 * 1024);
        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
        dataRegionConfiguration.setName("40MB_Region_Eviction");
        dataRegionConfiguration.setInitialSize(20 * 1024 * 1024);
        dataRegionConfiguration.setMaxSize(40 * 1024 * 1024);
        dataRegionConfiguration.setPageEvictionMode(DataPageEvictionMode.valueOf("RANDOM_2_LRU"));
        DataRegionConfiguration[] dataRegionConfigurations = new DataRegionConfiguration[1];
        dataRegionConfigurations[0] = dataRegionConfiguration;
        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfigurations);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("SampleCache");
        cacheConfiguration.setDataRegionName("40MB_Region_Eviction");
        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
        //--local args
        if (option.has("cluster")) {
            ZookeeperDiscoverySpi zookeeperDiscoverySpi = new ZookeeperDiscoverySpi();
            zookeeperDiscoverySpi.setZkConnectionString("192.168.80.41:2181");
            zookeeperDiscoverySpi.setJoinTimeout(1000);
            zookeeperDiscoverySpi.setSessionTimeout(30000);
            igniteConfiguration.setDiscoverySpi(zookeeperDiscoverySpi);
        }
        Ignition.start(igniteConfiguration);
    }
}
