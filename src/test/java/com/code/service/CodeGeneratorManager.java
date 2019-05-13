package com.code.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedTable.TargetRuntime;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.service.impl.ControllerGenerator;
import com.code.service.impl.ModelAndMapperGenerator;
import com.code.service.impl.ServiceGenerator;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * 代码生成器基础项 (常量信息 & 通用方法) Created by zhh on 2017/09/20.
 */
public class CodeGeneratorManager extends CodeGeneratorConfig {

    protected static final Logger logger        = LoggerFactory.getLogger(CodeGeneratorManager.class);

    private static Configuration  configuration = null;
    
    /**
     * The primary columns of current generated table
     */
    public static List<String> primaryColumnsList = new ArrayList<String>();
    /**
     * all columns of current generated table.
     */
    public static List<String> allColumnsList = new ArrayList<String>();

    static {
        // 初始化配置信息
        init();
    }

    /**
     * 获取 Freemarker 模板环境配置
     * 
     * @return
     */
    public Configuration getFreemarkerConfiguration() {
        if (configuration == null) {
            configuration = initFreemarkerConfiguration();
        }
        return configuration;
    }

    /**
     * Mybatis 代码自动生成基本配置
     * 
     * @return
     */
    public Context initMybatisGeneratorContext(String mapperStorePackge) {
        Context context = new Context(ModelType.FLAT);
        context.setId("Potato");
        // context.setTargetRuntime("MyBatis3Simple");
        context.setTargetRuntime(TargetRuntime.MYBATIS3.name());
        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

        Properties p = new Properties();
        // 生成时去掉所有注释
        p.setProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS, "true");
        context.getCommentGenerator().addConfigurationProperties(p);
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty(PropertyRegistry.TYPE_RESOLVER_FORCE_BIG_DECIMALS,"false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
        jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
        jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
        jdbcConnectionConfiguration.setDriverClass(JDBC_DRIVER_CLASS_NAME);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(PROJECT_PATH + RESOURCES_PATH);
        sqlMapGeneratorConfiguration.setTargetPackage(mapperStorePackge);
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        return context;
    }

    /**
     * 通过数据库表名, 生成代码 如表名为 gen_test_demo 将生成 Demo & DemoMapper & DemoService & DemoServiceImpl & DemoController
     * 
     * @param flag 标志
     * @param tableNames 表名数组
     */
    public void genCode(String... tableNames) {
        for (String tableName : tableNames) {
            genCodeByTableName(tableName);
        }
    }

    /**
     * 通过数据库表名, 和自定义 modelName 生成代码 如表名为 gen_test_demo, 自定义 modelName 为 IDemo 将生成 IDemo & IDemoMapper & IDemoService &
     * IDemoServiceImpl & IDemoController
     * 
     * @param tableName 表名
     * @param modelName 实体类名
     * @param flag 标志
     */
    private void genCodeByTableName(String tableName) {
        new ModelAndMapperGenerator().genCode(tableName);
        new ServiceGenerator().genCode(tableName);
        new ControllerGenerator().genCode(tableName);
    }

    /**
     * Freemarker 模板环境配置
     * 
     * @return
     * @throws IOException
     */
    private Configuration initFreemarkerConfiguration() {
        Configuration cfg = null;
        try {
            cfg = new Configuration(Configuration.VERSION_2_3_23);
            cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_FILE_PATH));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        } catch (IOException e) {
            throw new RuntimeException("Freemarker 模板环境初始化异常!", e);
        }
        return cfg;
    }

    /**
     * 包转成路径 eg: com.bigsea.sns ==> com/bigsea/sns
     * 
     * @param packageName
     * @return
     */
    private static String packageConvertPath(String packageName) {
        return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
    }

    /**
     * 初始化配置信息
     */
    private static void init() {
        Properties prop = loadProperties();

        JDBC_URL = prop.getProperty("jdbc.url");
        JDBC_USERNAME = prop.getProperty("jdbc.username");
        JDBC_PASSWORD = prop.getProperty("jdbc.password");
        JDBC_DRIVER_CLASS_NAME = prop.getProperty("jdbc.driver.class.name");

        JAVA_PATH = prop.getProperty("java.path");
        RESOURCES_PATH = prop.getProperty("resources.path");
        TEMPLATE_FILE_PATH = PROJECT_PATH + prop.getProperty("template.file.path");

        MODEL_PACKAGE = prop.getProperty("model.package");
        MAPPER_PACKAGE = prop.getProperty("mapper.package");
        SERVICE_PACKAGE = prop.getProperty("service.package");
        CONTROLLER_PACKAGE = prop.getProperty("controller.package");

        PACKAGE_PATH_SERVICE = packageConvertPath(SERVICE_PACKAGE);
        PACKAGE_PATH_CONTROLLER = packageConvertPath(CONTROLLER_PACKAGE);
        PACKAGE_PATH_MAPPER = packageConvertPath(MAPPER_PACKAGE);

        AUTHOR = prop.getProperty("author");
        String dateFormat = "".equals(prop.getProperty("date-format")) ? "yyyy/MM/dd" : prop.getProperty("date-format");
        DATE = new SimpleDateFormat(dateFormat).format(new Date());
    }

    /**
     * 加载配置文件
     * 
     * @return
     */
    private static Properties loadProperties() {
        Properties prop = null;
        try {
            prop = new Properties();
            InputStream in = CodeGeneratorManager.class.getClassLoader().getResourceAsStream("generatorConfig.properties");
            prop.load(in);
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件异常!", e);
        }
        return prop;
    }

}
