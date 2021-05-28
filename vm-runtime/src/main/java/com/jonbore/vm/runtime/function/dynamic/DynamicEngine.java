package com.jonbore.vm.runtime.function.dynamic;


import com.alibaba.fastjson.JSON;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;


/**
 * 在Java SE6中最好的方法是使用StandardJavaFileManager类。
 * 这个类可以很好地控制输入、输出，并且可以通过DiagnosticListener得到诊断信息，
 * 而DiagnosticCollector类就是listener的实现。
 * 使用StandardJavaFileManager需要两步。
 * 首先建立一个DiagnosticCollector实例以及通过JavaCompiler的getStandardFileManager()方法得到一个StandardFileManager对象。
 * 最后通过CompilationTask中的call方法编译源程序。
 */
public class DynamicEngine {
    //单例
    private static DynamicEngine instance = null;
    private static final URLClassLoader parentClassLoader = (URLClassLoader) DynamicEngine.class.getClassLoader();
    private static String classpath;


    /**
     * 创建classpath
     */
    static {
        classpath = null;
        try {
            HashSet<String> jarFileSet = new HashSet<>();
            //加载父级classloader的依赖
            for (URL url : parentClassLoader.getURLs()) {
                String p = url.getFile();
                if (p.isEmpty()) {
                    jarFileSet.add(url.getFile());
                }
            }
            //获取当前工程的appClassloader的依赖
            Enumeration<URL> resources = DynamicEngine.class.getClassLoader().getResources("META-INF");
            while (resources.hasMoreElements()) {
                jarFileSet.add(resources.nextElement().toString().replaceAll("jar:file:/", "").replaceAll("!/META-INF", ""));
            }
            String sourceClasses = DynamicEngine.class.getClassLoader().getResource("").getPath();
            //如果是本地源码运行，不会引用工程的jar文件，需要讲引用的classes加到编译的环境变量里
            if (sourceClasses.endsWith("classes/")) {
                jarFileSet.add(sourceClasses);
            }
            classpath = String.join(File.pathSeparator, jarFileSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DynamicEngine getInstance() {
        if (instance == null) {
            synchronized (DynamicEngine.class) {
                if (instance == null) {
                    instance = new DynamicEngine();
                }
            }
        }
        return instance;
    }

    private DynamicEngine() {
    }

    /**
     * 编译java代码到Class
     *
     * @param fullClassName 类名
     * @param javaCode      类代码
     * @return Object
     */
    public Class javaCodeToObject(String fullClassName, String javaCode) {
        Class clazz = null;
        //获取系统编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 建立DiagnosticCollector对象
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        // 建立用于保存被编译文件名的对象
        // 每个文件被保存在一个从JavaFileObject继承的类中
        DynamicJavaFileManager fileManager = new DynamicJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new JavaCompilerObject(fullClassName, javaCode));

        //使用编译选项可以改变默认编译行为。编译选项是一个元素为String类型的Iterable集合
        List<String> options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(classpath);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
        // 编译源程序
        boolean success = task.call();
        if (success) {
            System.out.println(String.format("class %s 编译成功", fullClassName));
            //如果编译成功，用类加载器加载该类
            JavaRuntimeObject functionFileObject = fileManager.getJavaClassObject();
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
            clazz = dynamicClassLoader.loadClass(fullClassName, functionFileObject);
        } else {
            //如果想得到具体的编译错误，可以对Diagnostics进行扫描
            System.out.println(JSON.toJSONString(diagnostics));
        }
        return clazz;
    }
}