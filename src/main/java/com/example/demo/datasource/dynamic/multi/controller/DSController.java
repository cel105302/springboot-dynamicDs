package com.example.demo.datasource.dynamic.multi.controller;

import com.example.demo.datasource.dynamic.multi.build.BuildDataSource;
import com.example.demo.datasource.dynamic.multi.context.DatasourceInfo;
import com.example.demo.datasource.dynamic.multi.context.DynamicDataSourceContextHolder;
import com.example.demo.datasource.dynamic.multi.context.DynamicDataSourceRegister;
import com.example.demo.spring.util.SpringContextUtil;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ds")
public class DSController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping("/switch/test")
    public List<String> ds1(String routeKey){
        if (!DynamicDataSourceContextHolder.containsDataSource(routeKey)) {

            System.err.println("数据源[{}]不存在，使用默认数据源 > {}"+routeKey);

        } else {

            System.out.println("Use DataSource : {} > {}"+routeKey);

            //找到的话，那么设置到动态数据源上下文中。

            DynamicDataSourceContextHolder.setDataSourceType(routeKey);}

        List res = new ArrayList<>();
        //jdbcTemplate.execute("select * from t_test");
        jdbcTemplate.query("select * from t_test", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");

                res.add(id);
                res.add(name);
            }
        });
        return res;
    }

    @PostMapping(value = "/add")
    public void addDataSource(@RequestBody DatasourceInfo datasourceInfo){
       /* String driverClassName = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test4";
        String username = "root";
        String password = "123456";*/

        String key = datasourceInfo.getKey();
        Map<Object, Object> targetDataSource = new HashMap<>();
        DynamicDataSourceRegister.customDataSources.put(key, BuildDataSource.buildDataSource(datasourceInfo));
        DynamicDataSourceContextHolder.dataSourceIds.add(key);
        targetDataSource.putAll(DynamicDataSourceRegister.customDataSources);
        registerBeanDefinition(targetDataSource);
    }

    private void registerBeanDefinition(Map<Object, Object> targetDataSource) {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextUtil.getApplicationContext();

        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        BeanDefinition beanDefinition = defaultListableBeanFactory.getBeanDefinition("dataSource");
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();

        //添加属性：AbstractRoutingDataSource.defaultTargetDataSource
        mpv.addPropertyValue("defaultTargetDataSource", DynamicDataSourceRegister.defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSource);

        //先移除已经注册的beanDefinition
        defaultListableBeanFactory.removeBeanDefinition("dataSource");
        //注册更新后的beanDefinition
        defaultListableBeanFactory.registerBeanDefinition("dataSource", beanDefinition);
    }
}
