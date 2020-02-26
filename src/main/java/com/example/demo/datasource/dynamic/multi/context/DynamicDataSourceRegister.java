package com.example.demo.datasource.dynamic.multi.context;

import com.example.demo.datasource.dynamic.multi.build.BuildDataSource;
import com.example.demo.datasource.dynamic.multi.config.DynamicDataSource;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {



    // 默认数据源

    public static DataSource defaultDataSource;



    public static Map<String, DataSource> customDataSources = new HashMap<String, DataSource>();



    /**

     * 加载多数据源配置

     */

    @Override

    public void setEnvironment(Environment environment) {

        System.out.println("DynamicDataSourceRegister.setEnvironment()");

        initDefaultDataSource(environment);

        initCustomDataSources(environment);

    }



    /**

     * 加载主数据源配置.

     * @param env

     */

    private void initDefaultDataSource(Environment env){

        // 读取主数据源

        DatasourceInfo datasourceInfo = Binder.get(env).bind("spring.datasource", Bindable.of(DatasourceInfo.class)).get();

        //创建数据源;
        defaultDataSource = BuildDataSource.buildDataSource(datasourceInfo);

    }



    /**

     * 初始化更多数据源

     *

     * @author SHANHY

     * @create 2016年1月24日

     */

    private void initCustomDataSources(Environment env) {

        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源

        String dsPrefixs = env.getProperty("custom.datasource.names");

        for (String dsPrefix : dsPrefixs.split(",")) {// 多个数据源

            DatasourceInfo datasourceInfo = Binder.get(env).bind("custom.datasource." + dsPrefix, Bindable.of(DatasourceInfo.class)).get();

            DataSource ds = BuildDataSource.buildDataSource(datasourceInfo);

            customDataSources.put(dsPrefix, ds);

        }

    }


    @Override

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        System.out.println("DynamicDataSourceRegister.registerBeanDefinitions()");

        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();

        // 将主数据源添加到更多数据源中

        targetDataSources.put("dataSource", defaultDataSource);

        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");

        // 添加更多数据源

            targetDataSources.putAll(customDataSources);

        for (String key : customDataSources.keySet()) {

            DynamicDataSourceContextHolder.dataSourceIds.add(key);

        }



        // 创建DynamicDataSource

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

        beanDefinition.setBeanClass(DynamicDataSource.class);

        beanDefinition.setSynthetic(true);

        MutablePropertyValues mpv = beanDefinition.getPropertyValues();

        //添加属性：AbstractRoutingDataSource.defaultTargetDataSource

        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);

        mpv.addPropertyValue("targetDataSources", targetDataSources);

        registry.registerBeanDefinition("dataSource", beanDefinition);

    }

}
