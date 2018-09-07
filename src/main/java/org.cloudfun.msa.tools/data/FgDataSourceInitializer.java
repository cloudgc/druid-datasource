package org.cloudfun.msa.tools.data;


import org.springframework.boot.autoconfigure.jdbc.DataSourceSchemaCreatedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author sunzhongwen;
 * @Description： 初始化系统datasource
 * @date 2018/8/23-9:11;
 */public class FgDataSourceInitializer implements ApplicationListener<DataSourceSchemaCreatedEvent> {


    @Override
    public void onApplicationEvent(DataSourceSchemaCreatedEvent event) {

        //TODO  默认使用springboot dataSourceInitializer

    }
}
