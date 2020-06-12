package com.springbootside.duang.db.dao.impl.beetlsql;

import com.springbootside.duang.db.model.DBConnect;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.BeetlSqlDataSource;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BettlSQL Factory
 *
 * @author Laotang
 * @since 1.0
 */
public class BettlSQLFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeetlDao.class);

    /**
     *
     * @return
     */
//    static SQLManager getSqlManager() {
//        try {
//            SqlManagerFactoryBean factoryBean = new SqlManagerFactoryBean();
//            // 指定数据源
//            factoryBean.setCs(new BeetlSqlDataSource());
//            // 指定数据库类型
//            factoryBean.setDbStyle(new MySqlStyle());
//            // DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
//            factoryBean.setInterceptors(new Interceptor[]{new DebugInterceptor()});
//            // 名称转换样式，数据库命名跟java命名一样，采用DefaultNameConversion，下划线风格的，采用UnderlinedNameConversion
//            factoryBean.setNc(new DefaultNameConversion());
//            // 指定sql语句目录
//            factoryBean.setSqlLoader(new ClasspathLoader("/sql"));
//            return factoryBean.getObject();
//        } catch (Exception exception) {
//            throw new RuntimeException("初始化BettlSQL SQLManager时出错: " + exception.getMessage(), exception);
//        }
//    }

    /**
     *
     * @return
     */
    public static SQLManager getSqlManager(DBConnect connect) {
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
}
