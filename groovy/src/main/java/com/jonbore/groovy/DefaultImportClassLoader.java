package com.jonbore.groovy;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.*;

import java.security.CodeSource;
import java.util.Map;

/**
 * bp-component the name of the current project
 * 自动导入平台提供的import类加载器
 *
 * @author bo.zhou
 * @since 2021/10/25
 */
public class DefaultImportClassLoader extends GroovyClassLoader {
    public DefaultImportClassLoader() {
        super();
    }

    public DefaultImportClassLoader(ClassLoader loader) {
        super(loader);
    }

    public DefaultImportClassLoader(GroovyClassLoader parent) {
        super(parent);
    }

    public DefaultImportClassLoader(ClassLoader parent, CompilerConfiguration config, boolean useConfigurationClasspath) {
        super(parent, config, useConfigurationClasspath);
    }

    public DefaultImportClassLoader(ClassLoader loader, CompilerConfiguration config) {
        super(loader, config);
    }

    @Override
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
        CompilationUnit compilationUnit = super.createCompilationUnit(config, source);
        compilationUnit.addPhaseOperation(new CompilationUnit.SourceUnitOperation() {
            @Override
            public void call(SourceUnit source) throws CompilationFailedException {
                ModuleNode sourceAST = source.getAST();
                for (Map.Entry<String, String> importClass : GroovyEngineBuilder.importClassTable.entrySet()) {
                    if (sourceAST.getImport(importClass.getKey()) == null) {
                        sourceAST.addImport(importClass.getKey(), ClassHelper.make(importClass.getValue()));
                    }
                }
            }
        }, Phases.CONVERSION);
        return compilationUnit;
    }
}
