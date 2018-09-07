package org.cloudfun.msa.tools.data.annotion;

/**
 * @author sunzhongwen;
 * @Description： datasource bean name定义 ;
 * @date 2018/8/23-15:27;
 */
public final class FgDataSourceConstant {

    private FgDataSourceConstant() {
    }


    public static final String DATASOURCE = "DruidDataSource";


    /**
     * SqlSessionFactory bean name=FgDataSource.name+SqlSessionFactory
     */
    public static final String SESSION_FACTORY = "SqlSessionFactory";

    /**
     * TransactionManager bean name=FgDataSource.name+TransactionManager
     */
    public static final String TRANSACTIONMANAGER = "TransactionManager";


    /**
     * SqlSessionTemplate bean name=FgDataSource.name+SqlSessionTemplate
     */
    public static final String SQLSESSIONTEMPLATE = "SqlSessionTemplate";


}
