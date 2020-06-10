package com.springbootside.duang.db.dao;

import com.springbootside.duang.db.dao.template.beetlsql.BeetlDaoTemplate;
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
@Component
public class DaoFactory<T>
//        extends MybatisPlusDaoTemplate<T> {
        extends BeetlDaoTemplate<T> {
//{



    public DaoFactory() {
        initDaoTemplate();
    }

    private Dao initDaoTemplate() {
        if (BeetlDaoTemplate.TEMPLATE_NAME.equalsIgnoreCase(BeetlDaoTemplate.TEMPLATE_NAME)) {
            return new BeetlDaoTemplate();
        }
//        else if (MybatisPlusDaoTemplate.TEMPLATE_NAME.equalsIgnoreCase(MybatisPlusDaoTemplate.TEMPLATE_NAME)) {
//            return new MybatisPlusDaoTemplate();
//        } else {
//
//        }
        return null;
    }



}
