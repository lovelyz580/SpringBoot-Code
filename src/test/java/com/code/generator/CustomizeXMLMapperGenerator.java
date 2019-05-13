/*
 * TOP SECRET
 * Copyright 2006-2015 Transsion.com All right reserved. This software is the
 * confidential and proprietary information of Transsion.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Transsion.com.
 */
package com.code.generator;

import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.SimpleSelectAllElementGenerator;

/** 
 * ClassName:CustomizeXMLMapperGenerator <br/> 
 * Date:     2018年10月22日 下午2:25:03 <br/> 
 * @author   fenglibin1982@163.com
 * @Blog     http://blog.csdn.net/fenglibing
 * @version  
 * @see       
 */
public class CustomizeXMLMapperGenerator extends XMLMapperGenerator{
    public CustomizeXMLMapperGenerator() {
        super();
    }
    protected XmlElement getSqlMapElement() {
        XmlElement xmlElement = super.getSqlMapElement();
        addSelectByConditionElement(xmlElement);
        addSelectAllElement(xmlElement);
        addSelectByPageElement(xmlElement);
        addCountByConditionELement(xmlElement);
        addDeleteByConditionELement(xmlElement);
        return xmlElement;
    }
    protected void addSelectByConditionElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectByConditionElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    protected void addSelectAllElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SimpleSelectAllElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    protected void addSelectByPageElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectByPageElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    protected void addCountByConditionELement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new CountByConditionElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    protected void addDeleteByConditionELement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new DeleteByConditionElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
}
