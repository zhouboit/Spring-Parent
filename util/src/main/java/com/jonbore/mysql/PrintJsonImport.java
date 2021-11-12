package com.jonbore.mysql;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/10/25
 */
public class PrintJsonImport {
    public static void main(String[] args) {
        Configuration configuration = new Configuration(
                "jdbc:mysql://192.168.81.46:3306/uni_bp_prd_db_v2.3.3_cctc?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false",
                "com.mysql.jdbc.Driver",
                "root",
                "123456",
                "uni_bp_prd_db_v2.3.3_cctc",
                "",
//                "DEV_APP_SETUP",
                "DEV_APP_SETUP%",
                "D:/code/Local/CodeGen-main/src/main/java/com/jonbore/gen",
                "com.bp.databus.entity.develop.servicedevelop.appmanage",
                true
        );
        ConnectionSelect.getMySQLData(configuration);
    }
}
