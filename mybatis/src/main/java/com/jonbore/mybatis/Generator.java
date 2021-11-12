package com.jonbore.mybatis;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/9/22
 */
public class Generator {
    public static void main(String[] args) {
        List<String> warnings = new ArrayList<>();

        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("simple");
        context.setTargetRuntime("MyBatis3Simple");

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL("jdbc:mysql://192.168.81.41:3306/bo_test?useUnicode=true&characterEncoding=utf-8&tinyInt1isBit=false&serverTimezone=GMT%2B8");
        jdbcConnectionConfiguration.setUserId("root");
        jdbcConnectionConfiguration.setPassword("123456");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage("com.jonbore.mybatis.entity");
        javaModelGeneratorConfiguration.setTargetProject("D:\\home\\mybatis");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage("com.jonbore.mybatis.mapper");
        sqlMapGeneratorConfiguration.setTargetProject("D:\\home\\mybatis");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage("com.jonbore.mybatis.mapper");
        javaClientGeneratorConfiguration.setTargetProject("D:\\home\\mybatis");
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName("mes_sys_user");
//        GeneratedKey generatedKey = new GeneratedKey("id", "jdbc", true, null);
//        tableConfiguration.setGeneratedKey(generatedKey);
        tableConfiguration.addProperty("useActualColumnNames", "true");
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        tableConfiguration.setSelectByPrimaryKeyStatementEnabled(false);
//        tableConfiguration.setInsertStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(false);
        tableConfiguration.setDeleteByPrimaryKeyStatementEnabled(false);
        context.addTableConfiguration(tableConfiguration);

        Configuration configuration = new Configuration();
        context.addProperty("suppressAllComments","true");
        configuration.addContext(context);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        try {
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, callback, warnings);
            myBatisGenerator.generate(null);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
