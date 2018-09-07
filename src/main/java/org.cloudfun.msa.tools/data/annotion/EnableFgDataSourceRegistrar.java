package org.cloudfun.msa.tools.data.annotion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Set;

/**
 * @author sunzhongwen;
 * @Description： 数据库添加注入
 * @date 2018/8/21-15:44;
 */
public class EnableFgDataSourceRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnableFgDataSourceRegistrar.class);


    private ResourceLoader resourceLoader;

    private String resourcePath = "classpath:./**/*.class";


    /**
     * The bean name of the {@link DataSourceFactory}.
     */
    protected static final String FGDATASOURCE_FACTORY = DataSourceFactory.class.getName();


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        //dbfactory init
        BeanDefinitionBuilder dbFactory = BeanDefinitionBuilder
                .genericBeanDefinition(DataSourceFactory.class);
        registry.registerBeanDefinition(FGDATASOURCE_FACTORY,
                dbFactory.getBeanDefinition());

        //get classpath resource
        ResourcePatternResolver resourcePatternResolver =
                ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        CachingMetadataReaderFactory metadataReaderFactory =
                new CachingMetadataReaderFactory(resourceLoader);

        try {
            Resource[] resources = resourcePatternResolver.getResources(this.resourcePath);

            if (resources != null) {

                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                    String fgDataSourceName = FgDataSource.class.getName();
                    //获取注解类
                    if (metadataReader.getAnnotationMetadata().hasAnnotatedMethods(fgDataSourceName)) {


                        Set<MethodMetadata> annoMethods = metadataReader.getAnnotationMetadata()
                                .getAnnotatedMethods(fgDataSourceName);
                        //获取 方法注解
                        for (MethodMetadata metadata : annoMethods) {
                            String configMethod = metadata.getMethodName();
                            String declarClzName = metadata.getDeclaringClassName();

                            // 获取配置
                            Class<?> declarClz = Class.forName(declarClzName);

                            Configuration configurationAnno = AnnotationUtils.findAnnotation(declarClz,
                                    Configuration.class);
                            Assert.notNull(configurationAnno, "@FgDataSource must in @Configuration inner");


                            FgDataSource dataSourceAnno = AnnotationUtils.findAnnotation(declarClz
                                            .getMethod(configMethod),
                                    FgDataSource.class);


                            Bean beanWithQualify = AnnotationUtils.findAnnotation(declarClz.getMethod(configMethod),
                                    Bean.class);

                            Assert.notNull(beanWithQualify, "@FgDataSource must with @Bean");

                            String beanQualifyName = getBeanQualifyName(beanWithQualify);

                            if (beanQualifyName == null) {
                                beanQualifyName = metadata.getMethodName();
                            }

                            Assert.notNull(beanQualifyName,
                                    "@Bean with @FgDataSource must have name property");

                            //registr definition sqlfactory and tx and sqltemplate
                            //in the same time add mybatis mapping location with FgDataSource.scan class
                            // and  @FgDataSource.package
                            FgDataSourceScan fgDataSourceScan = new FgDataSourceScan(registry);
                            fgDataSourceScan.setFgDataSourceAnno(dataSourceAnno, beanQualifyName);
                            fgDataSourceScan.scan();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * 获取 注解bean 的名称
     *
     * @param beanWithQualify bean 注解
     * @return bean name
     */
    private String getBeanQualifyName(Bean beanWithQualify) {
        String alias = null;
        if (beanWithQualify.name().length > 0) {
            alias = beanWithQualify.name()[0];
        }

        if (alias == null && beanWithQualify.value().length > 0) {
            alias = beanWithQualify.value()[0];
        }

        return alias;
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        ClassPathResource resource = new ClassPathResource(".");
        try {
            resourcePath = resource.getURI() + "**/*.class";
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        this.resourceLoader = resourceLoader;
    }

}
