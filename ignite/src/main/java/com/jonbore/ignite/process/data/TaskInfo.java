package com.jonbore.ignite.process.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskInfo {
    private String id;
    private String tableName;
    private String majorId;
    private List<Plugin> plugins;
    private String driver;
    private String url;
    private String username;
    private String password;
    private Integer pkgNum;
}
