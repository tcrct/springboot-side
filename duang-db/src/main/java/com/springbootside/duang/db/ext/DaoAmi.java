package com.springbootside.duang.db.ext;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MapperInvoke;

import java.lang.reflect.Method;

public class DaoAmi implements MapperInvoke {
    @Override
    public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
        // 在此处实现对应的逻辑即可，参数中已经传递够多信息的了，基本上可以为所欲为了
        return null;
    }
}

