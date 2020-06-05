package com.springbootside.duang.common.dao.impl.beetlsql;

import com.springbootside.duang.common.dao.SqlDao;
import com.springbootside.duang.common.dao.impl.beetlsql.BeetlSqlBaseMapper;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.ext.DebugInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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
final public class BeetlSqlDao<T> implements SqlDao<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeetlSqlDao.class);

    @Autowired
    private BeetlSqlBaseMapper<T> baseMapper;
    private SQLManager manager;

    public BeetlSqlDao(){
        if (null == manager) {
            synchronized (BeetlSqlDao.class) {
                try {
//                    manager = BettlSQLFactory.getSqlManager();
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
    @Override
    public T save(T obj) {

        manager.insert(obj);
        System.out.println("SqlDao：" + obj.getClass().getName()+"             "+baseMapper.hashCode());
        return (T)obj;
    }

    /**
     * 根据ID查找对象
     * @param id 对象记录的ID值
     * @return 返回泛型对象
     */
    @Override
    public T findById(Serializable id) {
        return null;
    }
}
