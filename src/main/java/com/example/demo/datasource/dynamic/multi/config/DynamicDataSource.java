package com.example.demo.datasource.dynamic.multi.config;

import com.example.demo.datasource.dynamic.multi.build.BuildDataSource;
import com.example.demo.datasource.dynamic.multi.context.DatasourceInfo;
import com.example.demo.datasource.dynamic.multi.context.DynamicDataSourceContextHolder;
import com.example.demo.datasource.dynamic.multi.context.DynamicDataSourceRegister;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

public class DynamicDataSource  extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /*

     * 代码中的determineCurrentLookupKey方法取得一个字符串，

     * 该字符串将与配置文件中的相应字符串进行匹配以定位数据源，配置文件，即applicationContext.xml文件中需要要如下代码：(non-Javadoc)

     * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()

     */

    @Override

    protected Object determineCurrentLookupKey() {

        /*

         * DynamicDataSourceContextHolder代码中使用setDataSourceType

         * 设置当前的数据源，在路由类中使用getDataSourceType进行获取，

         *  交给AbstractRoutingDataSource进行注入使用。

         */

        return DynamicDataSourceContextHolder.getDataSourceType();

    }

    /*public void addDataSource(DatasourceInfo datasourceInfo, String key){
        Map<Object, Object> targetDataSource = new HashMap<>();
        DynamicDataSourceRegister.customDataSources.put(key, BuildDataSource.buildDataSource(datasourceInfo));
        DynamicDataSourceContextHolder.dataSourceIds.add(key);
        targetDataSource.putAll(DynamicDataSourceRegister.customDataSources);
        super.setTargetDataSources(targetDataSource);
        super.afterPropertiesSet();
    }*/
}
