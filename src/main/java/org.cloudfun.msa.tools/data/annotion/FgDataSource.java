package org.cloudfun.msa.tools.data.annotion;


import java.lang.annotation.*;

/**
 * @author sunzhongwen;
 * @Description： 数据源注解 ;
 * <p>
 * 结合mybatis mapper 自动注入 相应的datasource 到包
 * @date 2018/7/27-18:31;
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FgDataSource {

    /**
     * 数据源的名称 name.datasource.*
     *
     * @return
     */
    String name();

    /**
     * mybatis mapper loaction
     */

    String location() default "";

    /**
     * 需要注入的类名
     * 包括该包下的所有类
     *
     * @return
     */
    Class[] scan() default {};

    /**
     * 扫描的包
     */

    String[] packages() default {};

}
