
#  多数据源注解

使用方法

>    @Configuration
>    public class DataSourceConfig {
>    
>    
>        @Bean        //配置datsource Bean
>                     //springboot 需要选取一个默认的datasource 加载classpath 下的sql 文件 ，
>                     // 多数据源下 从上到下加载第一个 默认的datasource
>        @ConfigurationProperties(prefix = "fcupadm.datasource") //自动装配datasource
>        //需要和 @Bean 注解一块使用,
>        @FgDataSource(
>                name = "fcupadm",
>                location = "classpath:mapper", //读取 claapath 下 mapper/fcupadm 下的xml文件
>                scan = FcupUserfoMapper.class,  //mybatis 扫描该类下的所有的文件
>                packages = {"org.cloudfun.msa.fcup.dal.fcupadm.mapper"} //packages 与scan 路径会合并扫描
>        )
>        public DruidDataSourceProperties fcupDataSource() {
>            return new DruidDataSourceProperties();
>        }
>    
>    
>        @Bean(name = "payadm")
>        @ConfigurationProperties(prefix = "payadmin.datasource")
>        @FgDataSource(name = "payadm", scan = PayadmUserMapper.class)
>        public DruidDataSourceProperties payDataSource() {
>            return new DruidDataSourceProperties();
>        }
>    
>    }
