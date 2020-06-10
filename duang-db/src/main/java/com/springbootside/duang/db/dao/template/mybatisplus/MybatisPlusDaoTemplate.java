package com.springbootside.duang.db.dao.template.mybatisplus;

import cn.hutool.db.sql.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springbootside.duang.db.dao.template.AbstractDaoTemplate;
import org.beetl.sql.core.SQLManager;

import java.io.Serializable;
import java.util.List;

public class MybatisPlusDaoTemplate<T> extends AbstractDaoTemplate<BaseMapper, T> {

    public static final String TEMPLATE_NAME = "MybatisPlus";

    private BaseMapper manager;

    @Override
    public BaseMapper getManager() {
        return null;
    }

    @Override
    public T save(T obj) {
        System.out.println("MybatisPlusDaoTemplate：" + obj.getClass().getName());
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
}
