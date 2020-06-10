package com.springbootside.duang.db.ds;

import com.springbootside.duang.db.model.DBConnect;

import javax.sql.DataSource;

/**
 * 数据源接口
 * @author laotang
 * @since 1.0
 */
public interface IDataSourceFactory<T> {

    /**
     *  获取数据源
     * @return		DataSource
     */
    T getDataSource(DBConnect connect) throws Exception;

}
