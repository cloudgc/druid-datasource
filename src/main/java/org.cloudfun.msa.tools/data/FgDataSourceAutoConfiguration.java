package org.cloudfun.msa.tools.data;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.cloudfun.msa.tools.data.annotion.EnableFgDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

/**
 * @author sunzhongwen;
 * @Description： 开启数据库自动注入 扫描
 * @date 2018/8/29-17:45;
 */
@Configuration
@EnableFgDataSource
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
public class FgDataSourceAutoConfiguration {
}
