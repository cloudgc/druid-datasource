package org.cloudfun.msa.tools.data.annotion;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author sunzhongwen;
 * @Description： 扫描 数据源注解并注入sqlfactory sqlsession  ;
 * @date 2018/7/30-14:12;
 */
public class FgDataSourceScan extends ClassPathBeanDefinitionScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(FgDataSourceScan.class);


    private FgDataSource fgDataSourceAnno;

    private String dataSourcePropertiesBeanName;


    private String dataSourceBeanName;

    /**
     * 初始化
     * @param fgDataSourceAnno 标识注解
     * @param beanName bean名称
     */
    public void setFgDataSourceAnno(FgDataSource fgDataSourceAnno, String beanName) {
        this.fgDataSourceAnno = fgDataSourceAnno;
        this.dataSourcePropertiesBeanName = beanName;
    }

    public FgDataSourceScan(BeanDefinitionRegistry registry) {
        super(registry);
    }


    private LinkedHashSet<String> scanPackages;


    @Override
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
    }


    public LinkedHashSet<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(LinkedHashSet<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    private Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {


        //get mapping scan
        addMappingScan(this.fgDataSourceAnno);


        try {

            //add druid datasource
            dataSourceBeanName = this.fgDataSourceAnno.name() + FgDataSourceConstant.DATASOURCE;
            addDruidDatasourceDefine();

            //add SessionFactory
            String sqlSessionFactoryRef = this.fgDataSourceAnno.name() + FgDataSourceConstant.SESSION_FACTORY;
            addSessionFactoryDefine(sqlSessionFactoryRef);

            //add  TransactionManager
            String dsTmRef = this.fgDataSourceAnno.name() + FgDataSourceConstant.TRANSACTIONMANAGER;
            addTransactionManagerDefine(dsTmRef);

            //add SqlSessionTemplate
            String sqlSessionTemplateRef = this.fgDataSourceAnno.name() + FgDataSourceConstant.SQLSESSIONTEMPLATE;
            addSqlSessionTemplateDefine(sqlSessionTemplateRef, sqlSessionFactoryRef);


            //set mybatis mapping scan
            ClassPathMapperScanner mapperScanner = new ClassPathMapperScanner(getRegistry());
            mapperScanner.setSqlSessionTemplateBeanName(sqlSessionTemplateRef);
            mapperScanner.setSqlSessionFactoryBeanName(sqlSessionFactoryRef);
            mapperScanner.registerFilters();
            mapperScanner.doScan(StringUtils.toStringArray(this.getScanPackages()));

        } catch (IOException e) {
            logger.error("初始化数据源错误", e);
            return beanDefinitions;
        }
        return beanDefinitions;
    }

    /**
     * 添加 datasource 定义
     */
    private void addDruidDatasourceDefine() {
        BeanDefinitionBuilder dataSourceFactoryBuild = BeanDefinitionBuilder.genericBeanDefinition();
        dataSourceFactoryBuild.setFactoryMethodOnBean("fgDataSourceBuild",
                EnableFgDataSourceRegistrar.FGDATASOURCE_FACTORY);
        String primaryDataSource = "dataSource";


        //set primary datasource
        if (!this.getRegistry().containsBeanDefinition(primaryDataSource)) {
            dataSourceFactoryBuild.getBeanDefinition().setPrimary(true);
            dataSourceBeanName = primaryDataSource;
        }

        dataSourceFactoryBuild.addConstructorArgReference(dataSourcePropertiesBeanName);
        this.getRegistry().registerBeanDefinition(dataSourceBeanName,
                dataSourceFactoryBuild.getBeanDefinition());
    }

    /**
     * generation sqlSessionFactory
     *
     * @param sqlSessionFactoryRef sqlSessionFactory bean 引用
     * @throws IOException 应用异常
     */
    private void addSessionFactoryDefine(String sqlSessionFactoryRef) throws IOException {

        BeanDefinitionBuilder sqlSessionFactoryBuild = BeanDefinitionBuilder.genericBeanDefinition();
        sqlSessionFactoryBuild.setFactoryMethodOnBean("fgSqlSessionFactory",
                EnableFgDataSourceRegistrar.FGDATASOURCE_FACTORY);
        sqlSessionFactoryBuild.addDependsOn(dataSourceBeanName);
        sqlSessionFactoryBuild.addConstructorArgReference(dataSourceBeanName);
        sqlSessionFactoryBuild.addConstructorArgValue(this.fgDataSourceAnno.name());
        sqlSessionFactoryBuild.addConstructorArgValue(this.fgDataSourceAnno.location());

        this.getRegistry().registerBeanDefinition(sqlSessionFactoryRef,
                sqlSessionFactoryBuild.getBeanDefinition());
    }

    /**
     * generation SqlSessionTemplate
     *
     * @param sqlSessionTemplateRef sqlSessionTemplate bean 引用
     * @param sqlSessionFactoryRef sqlSessionFactory bean 引用
     */
    private void addSqlSessionTemplateDefine(String sqlSessionTemplateRef, String sqlSessionFactoryRef) {


        BeanDefinitionBuilder sqlSqlSessionTemplateBuild = BeanDefinitionBuilder.genericBeanDefinition();
        sqlSqlSessionTemplateBuild.setFactoryMethodOnBean("fgSqlSessionTemplate",
                EnableFgDataSourceRegistrar.FGDATASOURCE_FACTORY);
        sqlSqlSessionTemplateBuild.addConstructorArgReference(sqlSessionFactoryRef);
        this.getRegistry().registerBeanDefinition(sqlSessionTemplateRef,
                sqlSqlSessionTemplateBuild.getBeanDefinition());
    }

    /**
     * generation DataSourceTransactionManager
     *
     * @param dsTmRef transactionManager bean 引用
     */
    private void addTransactionManagerDefine(String dsTmRef) {

        BeanDefinitionBuilder sqlTransactionManagerBuild = BeanDefinitionBuilder.genericBeanDefinition();
        sqlTransactionManagerBuild.setFactoryMethodOnBean("fgTransactionManager",
                EnableFgDataSourceRegistrar.FGDATASOURCE_FACTORY);
        sqlTransactionManagerBuild.addDependsOn(dataSourceBeanName);
        sqlTransactionManagerBuild.addConstructorArgReference(dataSourceBeanName);

        this.getRegistry().registerBeanDefinition(dsTmRef,
                sqlTransactionManagerBuild.getBeanDefinition());

    }

    /**
     * set mapping scan packge
     *
     * @param dataSourceAnno FgDataSource
     */
    private void addMappingScan(FgDataSource dataSourceAnno) {
        LinkedHashSet<String> pack = new LinkedHashSet<>();
        Class[] scans = dataSourceAnno.scan();
        for (Class clz : scans) {
            pack.add(clz.getPackage().getName());
        }
        String[] packages = dataSourceAnno.packages();
        pack.addAll(Arrays.asList(packages));
        setScanPackages(pack);
    }
}
