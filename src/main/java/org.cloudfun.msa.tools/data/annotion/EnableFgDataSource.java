package org.cloudfun.msa.tools.data.annotion;

import oracle.jdbc.OracleDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sunzhongwen;
 * @Description： 开启数据库自动注入
 * @date 2018/8/21-15:44;
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnClass({SqlSessionFactory.class, OracleDriver.class, ClassPathMapperScanner.class})
@Import({EnableFgDataSourceRegistrar.class})
public @interface EnableFgDataSource {
}
