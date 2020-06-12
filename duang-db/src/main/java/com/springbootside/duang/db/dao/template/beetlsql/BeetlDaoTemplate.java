package com.springbootside.duang.db.dao.template.beetlsql;

import cn.hutool.db.sql.Query;
import com.springbootside.duang.db.dao.ZatDBConnect;
import com.springbootside.duang.db.dao.impl.beetlsql.BeetlSqlBaseMapper;
import com.springbootside.duang.db.dao.impl.beetlsql.BeetlDao;
import com.springbootside.duang.db.dao.template.AbstractDaoTemplate;
import com.springbootside.duang.db.model.DBConnect;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.BeetlSqlDataSource;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @param <T>
 */
//@Component
public abstract class BeetlDaoTemplate<T> extends AbstractDaoTemplate<T> {

    public static final String TEMPLATE_NAME = "beetlSql";

    private static final Logger LOGGER = LoggerFactory.getLogger(BeetlDao.class);

    /**
     * BeetlSQL Manager
     */
    private static SQLManager manager;

    public BeetlDaoTemplate(){
        if (null == manager) {
            synchronized (BeetlDaoTemplate.class) {
                try {
                    manager = getSqlManager(new ZatDBConnect());
                    LOGGER.info("BeetlDaoTemplate init success");
//                    initEntityClass(getClass());
                } catch (Exception e) {
                    LOGGER.warn("{},{}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    private SQLManager getSqlManager(DBConnect connect) {
        try {
            ConnectionSource source = ConnectionSourceHelper.getSimple(
                    connect.getDriver(), connect.getUrl(), connect.getUsername(), connect.getPassword());
            // 指定数据库类型
            DBStyle mysql = new MySqlStyle();
            // sql语句放在classpagth的/sql 目录下
            SQLLoader loader = new ClasspathLoader("/sql");
            // 数据库命名跟java命名一样，所以采用DefaultNameConversion，
            // 名称转换样式，数据库命名跟java命名一样，采用DefaultNameConversion，下划线风格的，采用UnderlinedNameConversion
            DefaultNameConversion nc = new DefaultNameConversion();
            // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
            SQLManager manager = new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});
            LOGGER.info("connect mysql success!");
            return manager;
        } catch (Exception exception) {
            throw new RuntimeException("初始化BettlSQL SQLManager时出错: " + exception.getMessage(), exception);
        }
    }

    @Override
    public SQLManager getManager() {
        return manager;
    }

    public void execute(SQLReady ready, Class<?> clazz) {
        manager.execute(ready, clazz);
    }

    @Override
    public T save(T obj) {
        System.out.println("BeetlSqlDaoTemplate：" + obj.getClass().getName()+"                    ");
        entityClass = obj.getClass();
        System.out.println("@@@@@@@@@@@: " + getEntityClass());
        return null;
    }

    @Override
    public T findById(Serializable id) {
        return (T)manager.unique(entityClass, id);
    }

    @Override
    public List<T> findList(Query query) {
        return null;
    }

    @Override
    public boolean deleteById(Serializable id) {
        return false;
    }
}
