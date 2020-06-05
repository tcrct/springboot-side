package com.springbootside.duang.common.dao.impl.beetlsql;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeetlSqlBaseMapper<T> implements BaseMapper<T> {

    @Override
    public void insert(T t) {

    }

    @Override
    public void insert(T t, boolean b) {

    }

    @Override
    public void insertBatch(List<T> list) {

    }

    @Override
    public KeyHolder insertReturnKey(T t) {
        return null;
    }

    @Override
    public int updateById(T t) {
        return 0;
    }

    @Override
    public int updateTemplateById(T t) {
        return 0;
    }

    @Override
    public int deleteById(Object o) {
        return 0;
    }

    @Override
    public T unique(Object o) {
        return null;
    }

    @Override
    public T single(Object o) {
        return null;
    }

    @Override
    public List<T> all() {
        return null;
    }

    @Override
    public List<T> all(int i, int i1) {
        return null;
    }

    @Override
    public long allCount() {
        return 0;
    }

    @Override
    public List<T> template(T t) {
        return null;
    }

    @Override
    public List<T> template(T t, int i, int i1) {
        return null;
    }

    @Override
    public long templateCount(T t) {
        return 0;
    }

    @Override
    public List<T> execute(String s, Object... objects) {
        return null;
    }

    @Override
    public int executeUpdate(String s, Object... objects) {
        return 0;
    }

    @Override
    public SQLManager getSQLManager() {
        return null;
    }
}
