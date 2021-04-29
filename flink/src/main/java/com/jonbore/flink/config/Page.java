package com.jonbore.flink.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 */
public class Page implements Serializable {

    private static final long serialVersionUID = 6603381333387697977L;
    private int pageSize = 10;
    private int pageNum = 1;
    private int total = 0;
    private int totalPageNum = 0;
    private int startRowNum = 0;
    private int pageNumber = 1;
    private int endRowNum = 0;
    private Object condition;
    private List<Map<String,Object>> rows;
    private String orderBy = "";
    private String desc = "";
    private String index = "";
    private String type = "";

    public Page() {
    }

    public Page(int pageSize, int total) {
        this.pageSize = pageSize;
        this.total = total;
        int mod = total % pageSize;
        totalPageNum = mod == 0 ? (total / pageSize) : (total / pageSize) + 1;
        if (startRowNum <= 0) {
            startRowNum = 0;
            endRowNum = pageSize;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNumber) {
        this.pageNum = pageNumber;
        int startTemp = pageSize * (pageNumber - 1);
        if (startTemp > total) {
            this.startRowNum = total - pageSize;
        } else {
            this.startRowNum = startTemp;
        }
//		this.startRowNum=startTemp;
        int temp = pageSize * pageNum;
        if (temp > total) {
            this.endRowNum = total;
        } else {
            this.endRowNum = temp;
        }

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        int mod = total % pageSize;
        totalPageNum = mod == 0 ? (total / pageSize) : (total / pageSize) + 1;
        if (startRowNum <= 0) {
            startRowNum = 0;
            endRowNum = pageSize;
        }
        this.startRowNum = (pageSize * (pageNum - 1));
        int temp = pageSize * pageNum;
        if (temp > total) {
            this.endRowNum = total;
        } else {
            this.endRowNum = temp;
        }
    }

    public int getTotalPageNum() {
        return totalPageNum;
    }

    public void setTotalPageNum(int totalPageNum) {
        this.totalPageNum = totalPageNum;
    }

    public int getStartRowNum() {
        return startRowNum;
    }

    public void setStartRowNum(int startRowNum) {
        //	this.startRowNum = startRowNum;
        this.startRowNum = (pageSize * (pageNum - 1));
    }

    public int getEndRowNum() {
        int temp = pageSize * pageNum;
        if (temp > total) {
            this.endRowNum = total;
        } else {
            this.endRowNum = (pageSize * pageNum);
        }
        return endRowNum;
    }

    public void setEndRowNum(int endRowNum) {
        this.endRowNum = endRowNum;
    }

    public Object getCondition() {
        return condition;
    }

    public void setCondition(Object condition) {
        this.condition = condition;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        this.startRowNum = this.pageSize * (pageNumber - 1);
        this.endRowNum = this.pageSize * pageNumber;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
