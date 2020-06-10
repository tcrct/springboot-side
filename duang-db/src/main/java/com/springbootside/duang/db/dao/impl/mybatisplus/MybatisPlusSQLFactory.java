package com.springbootside.duang.db.dao.impl.mybatisplus;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MybatisPlus 工厂类
 * @author Laotang
 * @version 1.0
 */
public class MybatisPlusSQLFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusSQLFactory.class);

    public static BaseMapper getBaseMapper() {
        try {
            MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
            factoryBean.setConfiguration(new MybatisConfiguration());
            factoryBean.setDataSource(new DruidDataSource());
            factoryBean.setTypeAliasesPackage(getTypeAliasesPackage());
            factoryBean.setMapperLocations();
            GlobalConfig globalConfig = new GlobalConfig();
            globalConfig.setSqlSessionFactory(factoryBean.getObject());
            LOGGER.info("初始化Mybatis-Plus BaseMapper成功！");
        } catch (Exception exception) {
            throw new RuntimeException("初始化Mybatis-Plus BaseMapper时出错: " + exception.getMessage(), exception);
        }

//        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        return null;
    }

    private static String getTypeAliasesPackage() {
        return "";
    }

}
