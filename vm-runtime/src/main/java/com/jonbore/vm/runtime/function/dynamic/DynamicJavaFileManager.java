package com.jonbore.vm.runtime.function.dynamic;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * 类文件管理器
 * 用于JavaCompiler将编译好后的class，保存到JavaFunctionFileManager中
 */
public class DynamicJavaFileManager extends ForwardingJavaFileManager {

    /**
     * 保存编译后Class文件的对象
     */
    private JavaRuntimeObject functionFileObject;

    /**
     * 调用父类构造器
     */
    public DynamicJavaFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    /**
     * 将JavaFileObject对象的引用交给JavaCompiler，让它将编译好后的Class文件装载进来
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
        if (functionFileObject == null)
            functionFileObject = new JavaRuntimeObject(className, kind);
        return functionFileObject;
    }

    public JavaRuntimeObject getJavaClassObject() {
        return functionFileObject;
    }
}