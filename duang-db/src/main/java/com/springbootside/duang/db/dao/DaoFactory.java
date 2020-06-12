package com.springbootside.duang.db.dao;

import com.springbootside.duang.db.dao.template.beetlsql.BeetlDaoTemplate;
import org.beetl.sql.ext.spring4.BeetlSqlClassPathScanner;
import org.beetl.sql.ext.spring4.BeetlSqlScannerConfigurer;
import org.springframework.stereotype.Component;

/**
 * SqlDao实现类，基于BeetlSql
 *
 * @param <T> 泛型对象
 *
 * @author Laotang
 * @since 1.0
 */
//使用 @component注解，将普通JavaBean实例化到spring容器中。
//@Component
public abstract class DaoFactory<T>
//        extends MybatisPlusDaoTemplate<T> {
        extends BeetlDaoTemplate<T> {
//{



    public DaoFactory() {
//        initDaoTemplate();
        aa();
    }

    private void aa() {
        BeetlSqlScannerConfigurer config = new BeetlSqlScannerConfigurer();
        config.setBasePackage("com.zat.coupon.dao");
        config.setDaoSuffix("Dao");
        config.setSqlManagerFactoryBeanName("sqlManagerFactoryBean123");

//        config.postProcessBeanDefinitionRegistry();
//        BeetlSqlClassPathScanner scanner = new BeetlSqlClassPathScanner(registry);
    }



    private Dao initDaoTemplate() {
//        if (BeetlDaoTemplate.TEMPLATE_NAME.equalsIgnoreCase(BeetlDaoTemplate.TEMPLATE_NAME)) {
//            return new BeetlDaoTemplate();
//        }
//        else if (MybatisPlusDaoTemplate.TEMPLATE_NAME.equalsIgnoreCase(MybatisPlusDaoTemplate.TEMPLATE_NAME)) {
//            return new MybatisPlusDaoTemplate();
//        } else {
//
//        }
        return null;
    }



}
