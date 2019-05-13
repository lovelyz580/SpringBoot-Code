/*
 * TOP SECRET Copyright 2006-2015 Transsion.com All right reserved. This software is the confidential and proprietary
 * information of Transsion.com ("Confidential Information"). You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement you entered into with Transsion.com.
 */
package com.code.generator;

import java.util.List;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectAllMethodGenerator;

/**
 * ClassName:CustomizeJavaMapperGenerator <br/>
 * Date: 2018年10月22日 下午1:55:25 <br/>
 * 
 * @author fenglibin1982@163.com
 * @Blog http://blog.csdn.net/fenglibing
 * @version
 * @see
 */
public class CustomizeJavaMapperGenerator extends JavaMapperGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        List<CompilationUnit> answer = super.getCompilationUnits();
        for(CompilationUnit unit:answer) {
            if(unit.getClass().equals(Interface.class)) {
                addSelectByConditionMethod((Interface)unit);
                addSelectAllMethod((Interface)unit);
                addSelectByPageMethod((Interface)unit);
                addCountByConditionMethod((Interface)unit);
                addDeleteByConditionMethod((Interface)unit);
            }
        }
        return answer;
    }
    
    protected void addSelectByConditionMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectByConditionMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    
    protected void addSelectAllMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectAllMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    
    protected void addSelectByPageMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPageMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    
    protected void addCountByConditionMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new CountByConditionMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    
    protected void addDeleteByConditionMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByConditionMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }
    
    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator(){
        return new CustomizeXMLMapperGenerator();
    }
}
