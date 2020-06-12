package com.springbootside.duang.db.dao.impl.beetlsql;

import cn.hutool.db.sql.Query;
import com.springbootside.duang.db.dao.Dao;
import com.springbootside.duang.db.dao.ZatDBConnect;
import org.beetl.sql.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

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
final public class BeetlDao<T> { // implements Dao<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeetlDao.class);

    @Autowired
    private BeetlSqlBaseMapper<T> baseMapper;

    private static SQLManager manager;

    public BeetlDao(){
        if (null == manager) {
            synchronized (BeetlDao.class) {
                try {
                    manager = BettlSQLFactory.getSqlManager(new ZatDBConnect());
                } catch (Exception e) {
                    LOGGER.warn("{},{}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 保存对象
     * @param obj 待持久化的对象
     * @return 返回保存后的对象
     */
    public T save(T obj) {

//        manager.insert(obj);
        System.out.println("SqlDao：" + obj.getClass().getName()+"             "+baseMapper.hashCode());
        return (T)obj;
    }

    /**
     * 根据ID查找对象
     * @param id 对象记录的ID值
     * @return 返回泛型对象
     */
    public T findById(Serializable id) {
        return null;
//        System.out.println("getEntityClass():" + getEntityClass());
//        return (T)manager.unique(getEntityClass(), id);
    }

    public List<T> findList(Query query) {
        return null;
    }

    public boolean deleteById(Serializable id) {
        return false;
    }
}
