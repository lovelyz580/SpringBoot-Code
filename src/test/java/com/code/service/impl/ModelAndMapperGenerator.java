package com.code.service.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.code.generator.CustomizeJavaMapperGenerator;
import com.code.service.CodeGenerator;
import com.code.service.CodeGeneratorManager;
import com.code.util.StringUtils;

/**
 * Model & Mapper 代码生成器 Created by zhh on 2017/09/20.
 */
public class ModelAndMapperGenerator extends CodeGeneratorManager implements CodeGenerator {

    @Override
    public void genCode(String tableName) {
        String modelName = StringUtils.tableNameConvertUpperCamel(tableName);
        List<String> warnings = null;
        MyBatisGenerator generator = null;
        try {
            generator = generateFromJavaConfig(tableName, modelName, warnings);
        } catch (Exception e) {
            throw new RuntimeException("Model 和  Mapper 生成失败!", e);
        }

        if (generator == null || generator.getGeneratedJavaFiles().isEmpty()
            || generator.getGeneratedXmlFiles().isEmpty()) {
            throw new RuntimeException("Model 和  Mapper 生成失败, warnings: " + warnings);
        }

        logger.info(modelName, "{}.java 生成成功!");
        logger.info(modelName, "{}Mapper.java 生成成功!");
        logger.info(modelName, "{}Mapper.xml 生成成功!");
    }

    private MyBatisGenerator generateFromJavaConfig(String tableName, String modelName,
                                                    List<String> warnings) throws InvalidConfigurationException,
                                                                           SQLException, IOException,
                                                                           InterruptedException {
        MyBatisGenerator generator = null;
        Context initConfig = initConfig(tableName, modelName);
        Configuration cfg = new Configuration();
        cfg.addContext(initConfig);
        cfg.validate();

        DefaultShellCallback callback = new DefaultShellCallback(true);
        warnings = new ArrayList<String>();
        generator = new MyBatisGenerator(cfg, callback, warnings);
        generator.generate(null);
        getPrimaryKeys(cfg);
        return generator;
    }
    /**
     * Get the primary keys of current table, which will be used for cache
     * @param cfg
     */
    private void getPrimaryKeys(Configuration cfg) {
        try {
            Context context = cfg.getContexts().get(0);
            Field field = context.getClass().getDeclaredField("introspectedTables");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<IntrospectedTable> list = (List<IntrospectedTable>)field.get(context);
            IntrospectedTable introspectedTable = list.get(0);
            List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
            for(IntrospectedColumn column :allColumns) {
                allColumnsList.add(column.getActualColumnName());
            }
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
            if(primaryKeyColumns!=null && primaryKeyColumns.size()>0) {
                for(IntrospectedColumn column :primaryKeyColumns) {
                    primaryColumnsList.add(column.getActualColumnName());
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 完善初始化环境
     * 
     * @param tableName 表名
     * @param modelName 自定义实体类名, 为null则默认将表名下划线转成大驼峰形式
     */
    private Context initConfig(String tableName, String modelName) {
        Context context = null;
        try {
            context = initMybatisGeneratorContext(MAPPER_PACKAGE);
            JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
            javaModelGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
            javaModelGeneratorConfiguration.setTargetPackage(MODEL_PACKAGE);
            context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

            JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
            javaClientGeneratorConfiguration.setTargetProject(PROJECT_PATH + JAVA_PATH);
            javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE);
            // javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
            // javaClientGeneratorConfiguration.setConfigurationType("org.mybatis.generator.code.mybatis3.javamapper.JavaMapperGenerator");
            javaClientGeneratorConfiguration.setConfigurationType(CustomizeJavaMapperGenerator.class.getName());
            context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
            deleteExistsXmlMapperFile(tableName, modelName);

            TableConfiguration tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(tableName);
            tableConfiguration.setDomainObjectName(modelName);
            tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
            tableConfiguration.setCountByExampleStatementEnabled(false);
            // org.mybatis.generator.code.mybatis3.javamapper.JavaMapperGenerator
            tableConfiguration.setDeleteByExampleStatementEnabled(false);
            tableConfiguration.setSelectByExampleStatementEnabled(false);
            tableConfiguration.setUpdateByExampleStatementEnabled(false);
            context.addTableConfiguration(tableConfiguration);
        } catch (Exception e) {
            throw new RuntimeException("ModelAndMapperGenerator 初始化环境异常!", e);
        }
        return context;
    }

    private void deleteExistsXmlMapperFile(String tableName, String modelName) {
        String modelNameUpperCamel = modelName;
        String xmlMapperFilePath = PROJECT_PATH + RESOURCES_PATH + "/mapper/"
                + modelNameUpperCamel + "Mapper.xml";
        System.out.println(xmlMapperFilePath);
        File xmlMapperFile = new File(xmlMapperFilePath);
        /**
         * delete the exists mapper file, or the content will be merged with the old when do regenerate.
         */
        if (xmlMapperFile.exists()) {
            xmlMapperFile.delete();
        }
    }
}
