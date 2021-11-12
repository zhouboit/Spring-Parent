package com.jonbore.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * Spring-Parent the name of the current project
 * GroovyEngine 相关对象构造器
 *
 * @author bo.zhou
 * @since 2021/10/25
 */
public class GroovyEngineBuilder {

    /**
     * 用户配置的需要自动import的class
     */
    private static HashSet<String> imports = new HashSet<>();

    /**
     * 运行时进行class校验后可以正常加载使用的class
     * 和imports的区别是：
     * import中可以配置 a,b,c,d,e等class路径，但是在扫描当前JVM后发现c路径对应的class找不到，此时的
     * importClassTable中只会包含 a,b,d,e的class路径及class的simpleName
     */
    public static Hashtable<String, String> importClassTable = new Hashtable<>();


    /**
     * 批量添加默认import
     *
     * @param addImports 需要添加的import类名集合
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static void add(List<String> addImports) {
        imports.addAll(addImports);
        refresh();
    }

    /**
     * 单个添加默认import
     *
     * @param clazz 单个import类名
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static void add(String clazz) {
        imports.add(clazz);
        refresh();
    }

    /**
     * 批量移除默认import
     *
     * @param removeImports 需要移除的import类名集合
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static void remove(List<String> removeImports) {
        //数据量较大时remove all 效率没有remove效率高，
        removeImports.forEach(imports::remove);
        refresh();
    }

    /**
     * 单个移除默认import
     *
     * @param clazz 单个import类名
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static void remove(String clazz) {
        imports.remove(clazz);
        refresh();
    }

    /**
     * 刷新需要默认添加的import集合
     *
     * @author bo.zhou
     * @since 2021/10/25
     */
    private static void refresh() {
        if (imports != null && !imports.isEmpty()) {
            for (String anImport : imports) {
                try {
                    if (anImport.contains("@BIZ@")) {
                        String[] split = anImport.split("@BIZ@");
                        Class<?> clazz = Class.forName(split[0]);
                        importClassTable.put(split[1], clazz.getCanonicalName());
                    }else {
                        Class<?> clazz = Class.forName(anImport);
                        importClassTable.put(clazz.getSimpleName(), clazz.getCanonicalName());
                    }
                } catch (ClassNotFoundException e) {
                    //不在环境变量中的class不进行加载
                }
            }
        }
    }

    /**
     * 获取groovy默认类加载器
     *
     * @return groovy.lang.GroovyClassLoader
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static GroovyClassLoader getDefaultClassLoader() {
        ClassLoader ctxtLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> c = ctxtLoader.loadClass(Script.class.getName());
            if (c == Script.class) {
                return new GroovyClassLoader(ctxtLoader, new CompilerConfiguration(CompilerConfiguration.DEFAULT));
            }
        } catch (ClassNotFoundException cnfe) {
            /* ignore */
        }
        return new GroovyClassLoader(Script.class.getClassLoader(), new CompilerConfiguration(CompilerConfiguration.DEFAULT));
    }

    /**
     * 获取包含自定义import的类加载器
     *
     * @return com.jonbore.groovy.AutoImportGroovyClassLoader
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static DefaultImportClassLoader getAutoImportClassLoader() {
        ClassLoader ctxtLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> c = ctxtLoader.loadClass(Script.class.getName());
            if (c == Script.class) {
                return new DefaultImportClassLoader(ctxtLoader, new CompilerConfiguration(CompilerConfiguration.DEFAULT));
            }
        } catch (ClassNotFoundException cnfe) {
            /* ignore */
        }
        return new DefaultImportClassLoader(Script.class.getClassLoader(), new CompilerConfiguration(CompilerConfiguration.DEFAULT));
    }

    /**
     * 获取使用默认类加载器构建的Groovy脚本引擎
     *
     * @return org.codehaus.groovy.jsr223.GroovyScriptEngineImpl
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static GroovyScriptEngineImpl getDefaultEngine() {
        return new GroovyScriptEngineImpl(getDefaultClassLoader());
    }

    /**
     * 获取使用添加了自定义的默认import属性的类加载构建的Groovy脚本引擎
     *
     * @return org.codehaus.groovy.jsr223.GroovyScriptEngineImpl
     * @author bo.zhou
     * @since 2021/10/25
     */
    public static GroovyScriptEngineImpl getAutoImportEngine() {
        return new GroovyScriptEngineImpl(getAutoImportClassLoader());
    }
}
