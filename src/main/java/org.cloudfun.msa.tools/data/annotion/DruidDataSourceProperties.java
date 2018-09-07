package org.cloudfun.msa.tools.data.annotion;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * @author sunzhongwen;
 * @Description： datasource 数据库配置
 * @date 2018/8/30-9:22;
 */
@Data
@ConfigurationProperties(prefix = "fullgoal.datasource")
public class DruidDataSourceProperties {


    private static final int MAXPOOLPREPAREDSTATEMENTPERCONNECTIONSIZE = 128;

    private static final int TIMEBETWEENEVICTIONRUNSMILLIS = 60000;

    private static final int MINEVICTABLEIDLETIMEMILLIS = 30000;
    /**
     * datasource-name
     */
    private String name;

    /**
     * jdbcUrl
     */
    private String url;

    /**
     * datasource username
     */
    private String username;

    /**
     *
     */
    private String password;

    /**
     * 默认使用 oracle
     */
    private String driverClassName = "oracle.jdbc.driver.OracleDriver";


    /**
     * 初始化连接数
     */
    private int initialSize;

    /**
     * 最大连接池数量
     */
    private int maxActive;

    /**
     * 最小连接池数量
     */
    private int minIdle;


    /**
     * 获取连接的等待时间，单位毫秒，默认使用公平锁，useUnfairLock=true 设置非公平锁
     */
    private long maxWait;

    /**
     * 打开preparedStatements. psCache 缓存对oracle 数据库提升巨大
     */
    private boolean poolPreparedStatements;

    /**
     * psCache 缓存池大小
     */
    private int maxPoolPreparedStatementPerConnectionSize = MAXPOOLPREPAREDSTATEMENTPERCONNECTIONSIZE;

    /**
     * 打开 psCache ,该必须配置大于0, 设置该值大于0时，psCache=true 自动开启
     */
    private int maxOpenPreparedStatements;

    /**
     * 验证连接有效的sql 默认使用 select 1 from dual
     */
    private String validationQuery = "select 1 from dual";

    /**
     * 申请连接时 执行验证sql
     */
    private boolean testOnBorrow = false;

    /**
     * 归还连接时，执行验证sql
     */
    private boolean testOnReturn = false;

    /**
     * 检查最大空闲时间没有关闭的空闲连接
     */
    private boolean testWhileIdle = true;

    /**
     * 物理连接关闭的最大空闲时间
     */
    private long timeBetweenEvictionRunsMillis = TIMEBETWEENEVICTIONRUNSMILLIS;

    /**
     * 连接的最小空闲时间
     */
    private long minEvictableIdleTimeMillis = MINEVICTABLEIDLETIMEMILLIS;

    /**
     * 默认过滤器
     */
    private String filters = "stat,wall,slf4j";

    /**
     * 默认oracle
     */
    private boolean isOracle = true;


    /**
     * jdbc连接属性
     */
    private Properties connectionProperties;

    /**
     * 默认 jdbc 连接
     * @return jdbc 连接属性
     */
    public Properties getDefaultConnectionProperties() {
        String config = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";
        Properties properties = new Properties();
        String[] data = config.split(";");
        for (String pair : data) {
            String[] kv = pair.split("=");
            properties.setProperty(kv[0], kv[1]);
        }
        return properties;
    }


}
