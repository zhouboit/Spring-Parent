package com.jonbore.ignite.process.data;

import java.util.List;

public class Plugin {
    private String id;
    private String name;
    private String code;
    private List<Rule> rules;
    private Long total = 0L;
    private Long quality = 0L;
    private Long unQuality = 0L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total += total;
    }

    public Long getQuality() {
        return quality;
    }

    public void setQuality(Long quality) {
        this.quality += quality;
    }

    public Long getUnQuality() {
        return unQuality;
    }

    public void setUnQuality(Long unQuality) {
        this.unQuality += unQuality;
    }
}
