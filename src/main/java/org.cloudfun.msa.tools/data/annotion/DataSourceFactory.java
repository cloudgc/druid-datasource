package org.cloudfun.msa.tools.data.annotion;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.cloudfun.msa.tools.data.sqlplugin.SqlPrintInterceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author sunzhongwen;
 * @Description： sqlSessionFactory 工厂类
 * @date 2018/8/22-11:02;
 */

public class DataSourceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);

    private static final String CLASSPATH_PREFIX = "classpath:";

    private static final String LOCALTION_SUFFIX = "/*.xml";

    private static String mybatisConfigLocation = "classpath:config/mybatis/mybatis-config.xml";

    public static void setMybatisConfigLocation(String path) {
        DataSourceFactory.mybatisConfigLocation = path;
    }

    /**
     * datasource 工厂类
     *
     * @param properties 配置config
     * @return druid datasource
     * @throws SQLException sql异常
     */
    public DataSource fgDataSourceBuild(DruidDataSourceProperties properties) throws SQLException {
        DruidDataSource druidDataSource = null;
        try {
            druidDataSource = new DruidDataSource();

            druidDataSource.setName(properties.getName());
            druidDataSource.setUrl(properties.getUrl());
            druidDataSource.setUsername(properties.getUsername());
            druidDataSource.setPassword(properties.getPassword());
            druidDataSource.setDriverClassName(properties.getDriverClassName());
            druidDataSource.setInitialSize(properties.getInitialSize());
            druidDataSource.setMaxActive(properties.getMaxActive());
            druidDataSource.setMinIdle(properties.getMinIdle());
            druidDataSource.setMaxWait(properties.getMaxWait());
            druidDataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
            druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.
                    getMaxPoolPreparedStatementPerConnectionSize());
            druidDataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());

            druidDataSource.setValidationQuery(properties.getValidationQuery());
            druidDataSource.setTestOnBorrow(properties.isTestOnBorrow());
            druidDataSource.setTestOnReturn(properties.isTestOnReturn());
            druidDataSource.setTestWhileIdle(properties.isTestWhileIdle());
            druidDataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
            druidDataSource.setFilters(properties.getFilters());
            druidDataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
            druidDataSource.setOracle(properties.isOracle());
            druidDataSource.setConnectProperties(properties.getDefaultConnectionProperties());
            return druidDataSource;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }

    }


    /**
     * //create SqlSession
     *
     * @param dataSource     druid datasource
     * @param dataSourceName datasource bean name
     * @param location  需要扫描的位置
     * @return SqlSessionFactory
     * @throws Exception 构建异常
     */
    public SqlSessionFactory fgSqlSessionFactory(DataSource dataSource, String dataSourceName,
                                                 String location) throws Exception {

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        String locationPattern;
        if (!StringUtils.isEmpty(location)) {
            if (location.startsWith(CLASSPATH_PREFIX)) {
                locationPattern = location;
            } else {
                locationPattern = CLASSPATH_PREFIX + location;
            }
        } else {
            locationPattern = "classpath:mapper/";
        }

        String fileSeparator = "/";
        if (!locationPattern.endsWith(fileSeparator)) {
            locationPattern += fileSeparator;
        }

        locationPattern = locationPattern + dataSourceName + LOCALTION_SUFFIX;
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
                locationPattern));


        ClassPathResource configResource =
                new ClassPathResource(DataSourceFactory.
                        mybatisConfigLocation.replace(CLASSPATH_PREFIX, ""));

        if (configResource.exists()) {
            bean.setConfigLocation(configResource);
        } else {
            Interceptor[] plugins = new Interceptor[]{new SqlPrintInterceptor()};
            bean.setPlugins(plugins);
        }

        return bean.getObject();
    }

    /**
     * //create  Transaction 事物管理
     *
     * @param dataSource druid datasource
     * @return DataSourceTransactionManager
     */

    public DataSourceTransactionManager fgTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * //create sqltemplate
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @return SqlSessionTemplate
     * @throws Exception 构建异常
     */

    public SqlSessionTemplate fgSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
