package com.springbootside.duang.common.dao.impl.beetlsql;

import cn.hutool.setting.SettingUtil;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.spring4.BeetlSqlDataSource;
import org.beetl.sql.ext.spring4.SqlManagerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * BettlSQL Factory
 *
 * @author Laotang
 * @since 1.0
 */
public class BettlSQLFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeetlSqlDao.class);

    /**
     *
     * @return
     */
    static SQLManager getSqlManager() {
        try {
            SqlManagerFactoryBean factoryBean = new SqlManagerFactoryBean();
            // 指定数据源
            factoryBean.setCs(new BeetlSqlDataSource());
            // 指定数据库类型
            factoryBean.setDbStyle(new MySqlStyle());
            // DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
            factoryBean.setInterceptors(new Interceptor[]{new DebugInterceptor()});
            // 名称转换样式，数据库命名跟java命名一样，采用DefaultNameConversion，下划线风格的，采用UnderlinedNameConversion
            factoryBean.setNc(new DefaultNameConversion());
            // 指定sql语句目录
            factoryBean.setSqlLoader(new ClasspathLoader("/sql"));
            return factoryBean.getObject();
        } catch (Exception exception) {
            throw new RuntimeException("初始化BettlSQL SQLManager时出错: " + exception.getMessage(), exception);
        }
    }

    /*
    static SQLManager getSqlManager2() {

        try {
            String driver = "";
            String url = "";
            String userName = "";
            String password = "";
            ConnectionSource source = ConnectionSourceHelper.getSimple(driver, url, "", userName, password);
            DBStyle mysql = new MySqlStyle();
            // sql语句放在classpagth的/sql 目录下
            SQLLoader loader = new ClasspathLoader("/sql");
            // 数据库命名跟java命名一样，所以采用DefaultNameConversion，
            // 还有一个是UnderlinedNameConversion，下划线风格的        所以采用DefaultNameConversion nc = new  所以采用DefaultNameConversion();
            DefaultNameConversion nc = new DefaultNameConversion();
            // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
            return new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});
        } catch (Exception e) {
            LOGGER.warn("初始化BettlSQL SQLManager时出错: {}", e.getMessage(), e);
            return null;
        }
    }
     */
}
