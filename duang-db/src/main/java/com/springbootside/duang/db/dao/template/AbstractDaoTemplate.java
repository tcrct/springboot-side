package com.springbootside.duang.db.dao.template;

import com.springbootside.duang.db.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractDaoTemplate<T> implements Dao<T> {

    private final static Logger logger = LoggerFactory.getLogger(AbstractDaoTemplate.class);

    /**
     * 实体类
     */
    protected Class<?> entityClass;

    /**
     *  连接池对象
     * @return
     */
    public abstract Object getManager();

//    public Class<?> getEnitytClass() {
//        System.out.println(AbstractDaoTemplate.class.getClass());
//        Class<?> aClass = AbstractDaoTemplate.class.getInterfaces()[0];
//        return aClass.getGenericSuperclass().getClass();
////        ParameterizedType parameterizedType = (ParameterizedType) aClass;
////        Type type = parameterizedType.getActualTypeArguments()[0];
////        return type.getClass();
//    }

}
