package com.example.demo.datasource.dynamic.multi.build;

import com.example.demo.datasource.dynamic.multi.context.DatasourceInfo;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

public class BuildDataSource {
    //如配置文件中未指定数据源类型，使用该默认值

    private static final Object DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";
    /**

     * 创建datasource.

     * @param datasourceInfo

     * @return

     */

    @SuppressWarnings("unchecked")

    public static DataSource buildDataSource(DatasourceInfo datasourceInfo) {

        Object type = datasourceInfo.getType();

        if (type == null){

            type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource

        }

        Class<? extends DataSource> dataSourceType;

        try {

            dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);

            String driverClassName = datasourceInfo.getDriverClassName();

            String url = datasourceInfo.getUrl();

            String username = datasourceInfo.getUsername();

            String password = datasourceInfo.getPassword();

            DataSourceBuilder factory =   DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).type(dataSourceType);

            return factory.build();

        } catch (ClassNotFoundException e) {

            e.printStackTrace();

        }

        return null;

    }
}
