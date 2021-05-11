package com.jonbore.ignite.process.data;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteCallable;

import java.util.*;
import java.util.stream.Collectors;

public class StringProcess {
    public static void main(String[] args) {
//        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
//        igniteConfiguration.setPeerClassLoadingEnabled(true);
//        igniteConfiguration.setPublicThreadPoolSize(16);
//        igniteConfiguration.setSystemThreadPoolSize(8);
//        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
//        dataStorageConfiguration.setPageSize(8 * 1024);
//        dataStorageConfiguration.setWriteThrottlingEnabled(true);
//        dataStorageConfiguration.setWalSegmentSize(1024 * 1024 * 1024);
//        DataRegionConfiguration dataRegionConfiguration = new DataRegionConfiguration();
//        dataRegionConfiguration.setName("40MB_Region_Eviction");
//        dataRegionConfiguration.setInitialSize(20 * 1024 * 1024);
//        dataRegionConfiguration.setMaxSize(40 * 1024 * 1024);
//        dataRegionConfiguration.setPageEvictionMode(DataPageEvictionMode.valueOf("RANDOM_2_LRU"));
//        DataRegionConfiguration[] dataRegionConfigurations = new DataRegionConfiguration[1];
//        dataRegionConfigurations[0] = dataRegionConfiguration;
//        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfigurations);
//        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
//        CacheConfiguration[] cacheConfigurations = new CacheConfiguration[1];
//        CacheConfiguration cacheConfiguration = new CacheConfiguration();
//        cacheConfiguration.setName("SampleCache");
//        cacheConfiguration.setDataRegionName("40MB_Region_Eviction");
//        cacheConfigurations[0] = cacheConfiguration;
//        igniteConfiguration.setCacheConfiguration(cacheConfigurations);
//        ZookeeperDiscoverySpi zookeeperDiscoverySpi = new ZookeeperDiscoverySpi();
//        zookeeperDiscoverySpi.setSessionTimeout(30000);
//        zookeeperDiscoverySpi.setJoinTimeout(10000);
//        zookeeperDiscoverySpi.setZkConnectionString("192.168.80.41:2181");
//        igniteConfiguration.setDiscoverySpi(zookeeperDiscoverySpi);
//        Ignition.start(igniteConfiguration);
        Ignition.start();
        Ignite ignite = Ignition.ignite();
//        ignite.cluster().baselineAutoAdjustEnabled(true);
//        ignite.cluster().baselineAutoAdjustTimeout(60000);
//        ignite.cluster().state(ClusterState.ACTIVE);
//        ClusterGroup cluster = getCluster(Arrays.asList("192.168.5.135".split(",").clone()));
//        ignite.compute(cluster);
        Collection<IgniteCallable<String>> calls = new ArrayList<>();
        for (String word : "How many characters".split(" ")) {
            calls.add(() -> "00000".concat(word).substring(2, 6));
        }
        Collection<String> call = ignite.compute().call(calls);
        List<String> collect = call.stream().map(String::toUpperCase).collect(Collectors.toList());
        for (String s : collect) {
            System.out.println(s);
        }
        System.out.println(123);
        ignite.close();
    }

    public static ClusterGroup getCluster(List<String> ipList) {
        ClusterGroup clusterGroup = null;
        Ignite ignite = Ignition.ignite();
        if (ignite != null && ipList != null && ipList.size() > 0) {
            Collection<UUID> uuids = new ArrayDeque<>();
            ipList = ipList.stream().filter(ip -> ip != null && !ip.isEmpty()).collect(Collectors.toList());
            Collection<ClusterNode> nodes = ignite.cluster().nodes().stream().filter(clusterNode -> !clusterNode.isClient()).collect(Collectors.toList());
            for (ClusterNode node : nodes) {
                for (String destIp : node.addresses()) {
                    if (ipList.contains(destIp)) {
                        uuids.add(node.id());
                        break;
                    }
                }
            }
            if (!uuids.isEmpty()) {
                clusterGroup = ignite.cluster().forNodeIds(uuids);
            }
        }
        return clusterGroup;
    }
}
