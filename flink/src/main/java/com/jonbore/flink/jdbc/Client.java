package com.jonbore.flink.jdbc;

import java.util.TimeZone;

public class Client {
    public static void main(String[] args) throws Exception {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.5.131:3306/uni_datahub_merge0114?useUnicode=true&characterEncoding=utf-8&useTimezone=true&&serverTimezone=GMT%2B8","root","123456");
//        Statement statement = connection.createStatement();
//        statement.executeUpdate("UPDATE `uni_datahub_merge0114`.`dg_db_task_rules` SET `id`='496', `data_item_id`='191011981', `package_size`='20000', `type`=NULL, `type_info`=NULL, `field_name`=NULL, `total_or_increment`='1', `base_sql`='SELECT unistrong1.id, unistrong1.name, unistrong1.sex, unistrong1.update_time, unistrong1.data_hub_id, unistrong1.data_hub_oper_time FROM `gbl_test_user1` unistrong1', `script`=NULL, `use_script`='0', `before_script`=NULL, `after_script`=NULL, `before_status`='false', `after_status`='false', `create_date`='2021-05-13 19:10:12', `topic_status`='01', `handle_type`='', `handle_rule_ids`='', `topic`='gbl_test_user1_213', `increment_type`='01', `increment_field`='update_time', `increment_time`='2021-05-18 00:16:16', `increment_offset`=NULL, `update_time`='2021-05-18 17:20:51', `map_filed`='[{\\\"srcFiled\\\":\\\"id\\\",\\\"targetFiled\\\":\\\"id\\\"},{\\\"srcFiled\\\":\\\"name\\\",\\\"targetFiled\\\":\\\"name\\\"},{\\\"srcFiled\\\":\\\"sex\\\",\\\"targetFiled\\\":\\\"sex\\\"},{\\\"srcFiled\\\":\\\"update_time\\\",\\\"targetFiled\\\":\\\"update_time\\\"}]', `corn_task_id`=NULL, `expand_param`=NULL WHERE (`id`='496');\n");
//        ResultSet query = statement.executeQuery("select * from `uni_datahub_merge0114`.`dg_db_task_rules` WHERE (`id`='496') ");
//        while (query.next()) {
//            System.out.println(query.getTimestamp("increment_time"));
//        }
        System.out.println(TimeZone.getDefault().toString());
    }
}
