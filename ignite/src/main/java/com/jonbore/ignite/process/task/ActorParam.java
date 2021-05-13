package com.jonbore.ignite.process.task;

import com.jonbore.ignite.process.data.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ActorParam {
    private Task task;
    private Map<String, Object> val;
}
