package com.jonbore.mysql;

/**
 * @author bo.zhou
 * @date 2020/12/30 下午8:17
 */
public class Configuration {
    private final String url;
    private final String driver;
    private final String username;
    private final String password;
    private final String database;
    private final String table;
    private final String home;
    private final String parentPackage;
    /*
    如果home路径下存在带生成的已有目录或者文件：cleanHome 为true时删除整个home的文件夹重写生成，为false时只删除对应生成的文件
     */
    private final Boolean cleanHome;

    private final String tableNameExp;

    public Configuration(String url, String driver, String username, String password, String database, String table, String tableNameExp, String home, String parentPackage, Boolean cleanHome) {
        this.url = url;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.database = database;
        this.table = table;
        this.home = home;
        this.parentPackage = parentPackage;
        this.cleanHome = cleanHome;
        this.tableNameExp = tableNameExp;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public String getHome() {
        return home;
    }

    public String getParentPackage() {
        return parentPackage;
    }

    public boolean getCleanHome() {
        return cleanHome;
    }

    public String getTableNameExp() {
        return tableNameExp;
    }

    public String getResourceHome() {
        return home.substring(0, home.indexOf("/java"));
    }
}
