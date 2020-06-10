package com.springbootside.duang.db.ds;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Duang For Hikari数据源
 * @author laotang
 * @since 1.0
 */
public class HikariDataSourceFactory extends AbstractDataSource<HikariDataSource> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceFactory.class);

    @Override
    public HikariDataSource builderDataSource() {
        try {
            return new HikariDataSource();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setUsername(HikariDataSource ds, String username) {
        ds.setUsername(username);
    }

    @Override
    public void setPassword(HikariDataSource ds, String password) {
        ds.setPassword(password);
    }

    @Override
    public void setUrl(HikariDataSource ds, String jdbcUrl) {
        ds.setJdbcUrl(jdbcUrl);
    }

    /** 添加其它参数	**/
    @Override
    public void setInitParam(HikariDataSource ds) {
        try {
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            LOGGER.warn("初始化HikariDataSource时出错: {} {}", e.getMessage(), e);
        }
    }
}
