package com.jonbore.ignite.util;

/**
 * EnvConstants.java
 *
 * @author daowan.hu
 */
public final class Constants {


    /**
     * mysql 的字段类型
     */
    public final static class MysqlColumn {

        public final static String YEAR = "YEAR";
        public final static String DATETIME = "DATETIME";
        public final static String BINARY = "BINARY";
        public final static String VARBINARY = "VARBINARY";
        public final static String TINYINT = "TINYINT";
        public final static String SMALLINT = "SMALLINT";
        public final static String MEDIUMINT = "MEDIUMINT";
        public final static String INT = "INT";
        public final static String BIGINT = "BIGINT";
        public final static String BIT = "BIT";
        public final static String CHAR = "CHAR";
        public final static String VARCHAR = "VARCHAR";

        public final static String DECIMAL = "DECIMAL";
        public final static String DOUBLE = "DOUBLE";
        public final static String FLOAT = "FLOAT";
    }

    /**
     * mysql 的字段类型
     */
    public final static class OracleColumn {

        public final static String VARCHAR2 = "VARCHAR2";
        public final static String TIMESTAMP = "TIMESTAMP";
        public final static String ORACLE_CHAR = "CHAR";
        public final static String RAW = "RAW";
        public final static String NVARCHAR2 = "NVARCHAR2";
        public final static String TIMESTAMP_WITH_LOCAL_TIME_ZONE = "TIMESTAMP_WITH_LOCAL_TIME_ZONE";
        public final static String TIMESTAMP_WITH_TIME_ZONE = "TIMESTAMP_WITH_TIME_ZONE";

        public final static String INTERVAL_DAY_TO_SECOND = "INTERVAL_DAY_TO_SECOND";
        public final static String INTERVAL_YEAR_TO_MONTH = "INTERVAL_YEAR_TO_MONTH";
        public final static String NUMBER = "NUMBER";
    }

    /**
     * 数据表相关
     */
    public final static class TableInfo {

        public static final String COLUMN_NAME = "COLUMN_NAME";
        public static final String TYPE_NAME = "TYPE_NAME";
        public static final String COLUMN_SIZE = "COLUMN_SIZE";
        public static final String IS_PK = "isPk";
        public static final String ISNULL = "isNull";
        public static final String NULLABLE = "NULLABLE";
        public static final String COLUMN_DEF = "COLUMN_DEF";
        public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
    }

    public final static class TextFormat{
        public static final String XML = "XML";
        public static final String JSON = "JSON";
        public static final String EXCEL = "EXCEL";
        public static final String CSV = "CSV";
    }

    /**
     * 数据库
     */
    public final static class DataBase {
        public static final String MYSQL = "mysql";
        public static final String ORACLE = "oracle";
        public static final String ESGYN = "esgyn";
        public static final String IMPALA = "impala";
        public static final String POSTGRESQL = "postgresql";
        public static final String HIVE = "hive";
        public static final String BEYON = "beyondb";
        public static final String CLICKHOUSE = "clickhouse";

        public static final String MYSQL_QUERY_SQL = "SELECT '1' ";
        public static final String ORACLE_QUERY_SQL = "SELECT 'X' FROM DUAL";
        public static final String HIVE_QUERY_SQL = "SELECT 1";
        public static final String DEFAULT_VALIDATION_QUERY = "SELECT 'x'";
    }

    /**
     * 数据库类型代号，dbType。
     */
    public static final class DbTypeCode {
        public static final String MYSQL = "01";
        public static final String ORACLE = "08";
        public static final String IMPALA = "10";
        public static final String SQLSERVER = "11";
        public static final String POSTGRESQL = "12";
        public static final String INFLUXDB = "14";
        public static final String GREENPLUM = "15";
        public static final String BEYON = "16";
        public static final String GAUSS = "17";
        public static final String ESGYN = "18";
        public static final String HIVE = "19";
        public static final String DM = "20";
        public static final String CLICKHOUSE = "21";
    }

    /**
     * 数据源的驱动
     */
    public static final class DbDriver {
        public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
        public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
        public static final String SQLSERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
        public static final String BEYON_DRIVER = "com.beyondb.jdbc.BeyondbDriver";
        public static final String GAUSS_DRIVER = "com.huawei.gauss200.jdbc.Driver";
        public static final String ESGYN_DRIVER = "org.trafodion.jdbc.t4.T4Driver";
        public static final String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";
        public static final String DM_DRIVER = "dm.jdbc.driver.DmDriver";
        public static final String CLICKHOUSE_DRIVER = "ru.yandex.clickhouse.ClickHouseDriver";
    }

    /**
     * 标准定义类型
     */
    public static final class StandardCategoryType {
        public static final String NAME = "NAME";
        public static final String FIELD = "FIELD";
        public static final String CLASSIFY = "CLASSIFY";
    }

    /**
     * 用户资源权限定义
     */
    public static final class UserResource {
        public static final String READ_AUTHORITY = "1";
        public static final String READ_WRITE_AUTHORITY = "2";
        public static final String READ_WRITE_DOWNLOAD_AUTHORITY = "3";
        public static final String READ_DOWNLOAD_AUTHORITY = "4";
        public static final String AUTHORITY = "authority";
    }
}
