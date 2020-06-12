package com.springbootside.duang.db.dao.impl.beetlsql;

import cn.hutool.db.sql.Query;
import com.springbootside.duang.db.dao.Dao;
import com.springbootside.duang.db.dao.ZatDBConnect;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.PageQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
public class BeetlDaoImpl<T> implements Dao<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeetlDaoImpl.class);

    @Override
    public T save(T obj) {
        System.out.println("#########:" + obj.getClass().getName());
        return null;
    }

    @Override
    public T findById(Serializable id) {
        return null;
    }

    @Override
    public List<T> findList(Query query) {
        return null;
    }

    @Override
    public boolean deleteById(Serializable id) {
        return false;
    }

    @Override
    public void insert(T entity) {

    }

    @Override
    public void insert(T entity, boolean autDbAssignKey) {

    }

    @Override
    public void insertTemplate(T entity) {

    }

    @Override
    public void insertTemplate(T entity, boolean autDbAssignKey) {

    }

    @Override
    public void insertBatch(List<T> list) {

    }

    @Override
    public KeyHolder insertReturnKey(T entity) {
        return null;
    }

    @Override
    public int updateById(T entity) {
        return 0;
    }

    @Override
    public int updateTemplateById(T entity) {
        return 0;
    }

    @Override
    public int deleteById(Object key) {
        return 0;
    }

    @Override
    public T unique(Object key) {
        return null;
    }

    @Override
    public T single(Object key) {
        return null;
    }

    @Override
    public T lock(Object key) {
        return null;
    }

    @Override
    public List<T> all() {
        return null;
    }

    @Override
    public List<T> all(int start, int size) {
        return null;
    }

    @Override
    public long allCount() {
        return 0;
    }

    @Override
    public List<T> template(T entity) {
        return null;
    }

    @Override
    public <T1> T1 templateOne(T1 entity) {
        return null;
    }

    @Override
    public List<T> template(T entity, int start, int size) {
        return null;
    }

    @Override
    public void templatePage(PageQuery<T> query) {

    }

    @Override
    public long templateCount(T entity) {
        return 0;
    }

    @Override
    public List<T> execute(String sql, Object... args) {
        return null;
    }

    @Override
    public int executeUpdate(String sql, Object... args) {
        return 0;
    }

    @Override
    public SQLManager getSQLManager() {
        return null;
    }

    @Override
    public org.beetl.sql.core.query.Query<T> createQuery() {
        return null;
    }
}
