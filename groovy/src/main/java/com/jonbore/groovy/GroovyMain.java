package com.jonbore.groovy;


import com.alibaba.fastjson.JSONObject;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.Date;

;

/**
 * Spring-Parent the name of the current project
 *
 * @author bo.zhou
 * @since 2021/10/25
 */
public class GroovyMain {

    public static void main(String[] args) throws Exception {
        ArrayList<String> list = new ArrayList<String>() {{
            add("com.alibaba.fastjson.JSON");
            add("com.alibaba.fastjson.JSONObject@BIZ@JSONObjectbiz");
            add("com.jonbore.groovy.util.IDGenerator");
        }};
        GroovyEngineBuilder.add(list);
        Bindings bindings = new SimpleBindings(new JSONObject()
                .fluentPut("obj", new JSONObject().fluentPut("name", "bbbbb").fluentPut("time", "1993").getInnerMap())
                .fluentPut("name", "aaaa")
                .fluentPut("time", new Date()).getInnerMap());
        GroovyScriptEngineImpl groovyScriptEngine = GroovyEngineBuilder.getAutoImportEngine();
        CompiledScript compile = groovyScriptEngine.compile("" +
                "\n" +
                "/**\n" +
                " *\n" +
                " * Spring-Parent the name of the current project\n" +
                " * @author bo.zhou* @since 2021/10/25\n" +
                " */\n" +
                "\n" +
                "println(name)\n" +
                "println(time)\n" +
                "println(JSON.toJSONString(obj))\n" +
                "def object = new JSONObjectbiz(obj);\n" +
                "object.put(\"time\", new Date())\n" +
                "object.put(\"id\", IDGenerator.getID())\n" +
                "object.put(\"IDGEN\", IDGenerator.getUniqueID())\n" +
                "println(JSON.toJSONString(object))" +
                "");
        compile.eval(bindings);

//        Enumeration<URL> enumeration = GroovyClassLoaderBuilder.getAutoImportClassLoader().getResources("META-INF");
//        Enumeration<URL> resources = DynamicEngine.class.getClassLoader().getResources("META-INF");
//        while (enumeration.hasMoreElements()) {
//            System.out.println(enumeration.nextElement().toString());
//        }

//        for (Map.Entry<String, String> importClass : importClassTable.entrySet()) {
//            System.out.printf("name: %s class: %s \n", importClass.getKey(), importClass.getValue());
//        }
//        System.out.println(Class.forName("com.alibaba.fastjson.JSON").getSimpleName());
//        System.out.println(Class.forName("com.alibaba.fastjson.JSON").getCanonicalName());
    }
}
