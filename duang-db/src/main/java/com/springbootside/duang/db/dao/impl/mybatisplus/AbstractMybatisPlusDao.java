package com.springbootside.duang.db.dao.impl.mybatisplus;

import cn.hutool.db.sql.Query;
import com.baomidou.mybatisplus.core.conditions.Wrapper;


public class AbstractMybatisPlusDao<T> {

    public Wrapper<T> query2Wrapper(Query query) {
        return null;
    }
}
