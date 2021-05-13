package com.jonbore.ignite.process.task;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jonbore.ignite.process.data.Plugin;
import com.jonbore.ignite.process.data.Rule;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskActor extends ComputeTaskSplitAdapter<ActorParam, Map<String, Boolean>> {
    @Override
    protected Collection<? extends ComputeJob> split(int gridSize, ActorParam actorParam) throws IgniteException {
        List<Plugin> plugins = actorParam.getTask().getPlugins();
        List<ComputeJob> jobs = new ArrayList<>(plugins.size());
        for (Plugin plugin : plugins) {
            jobs.add(new ComputeJobAdapter() {
                @Override
                public Map<String, Boolean> execute() throws IgniteException {
                    Map<String, Boolean> result = Maps.newHashMap();
                    Ignite ignite = Ignition.ignite();
                    IgniteCache<Object, Object> taskPlugin = ignite.getOrCreateCache(actorParam.getTask().getId() + "_plugin");
                    Map<String, Object> o = actorParam.getVal();
                    boolean pass = true;
                    for (Rule rule : plugin.getRules()) {
                        Pattern pattern = Pattern.compile(rule.getReg());
                        String colValue = o.get(rule.getCol()) == null ? "" : o.get(rule.getCol()) + "";
                        Matcher matcher = pattern.matcher(colValue);
                        if (!matcher.find()) {
                            taskPlugin.put(o.get("majorId") + "_" + plugin.getId(), new JSONObject()
                                    .fluentPut("majorId", o.get("majorId"))
                                    .fluentPut("col", colValue)
                                    .fluentPut("pluginId", plugin.getId())
                            );
                            pass = false;
                        }
                    }
                    result.put(plugin.getId(), pass);
                    return result;
                }
            });
        }
        return jobs;
    }

    @Override
    public @Nullable Map<String, Boolean> reduce(List<ComputeJobResult> results) throws IgniteException {
        Map<String, Boolean> result = Maps.newHashMap();
        for (ComputeJobResult computeJobResult : results) {
            result.putAll(computeJobResult.getData());
        }
        return result;
    }
}
