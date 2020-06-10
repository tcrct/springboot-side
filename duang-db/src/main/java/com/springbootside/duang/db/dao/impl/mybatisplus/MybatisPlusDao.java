package com.springbootside.duang.db.dao.impl.mybatisplus;

import cn.hutool.db.sql.Query;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springbootside.duang.common.ToolsKit;
import com.springbootside.duang.db.dao.Dao;
import com.springbootside.duang.common.entity.IdEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * SqlDao实现类，基于MybatisPlus
 *
 * @param <T> 泛型对象
 *
 * @author Laotang
 * @since 1.0
 */
//使用 @component注解，将普通JavaBean实例化到spring容器中。
//@Component
public class MybatisPlusDao<T> extends AbstractMybatisPlusDao<T>  implements Dao<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusDao.class);

    private static BaseMapper mapper;

    public MybatisPlusDao() {
        if (null == mapper) {
            synchronized (MybatisPlusDao.class) {
                try {
                    mapper = MybatisPlusSQLFactory.getBaseMapper();
                } catch (Exception e) {
                    LOGGER.warn("{},{}", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public T save(T obj) {
        if (null == obj) {
            throw new NullPointerException("保存时，entity对象不能为空！");
        }

        IdEntity idEntity = (IdEntity) obj;
        if (ToolsKit.isEmpty(idEntity.getId())) {
            mapper.insert(obj);
        } else {
            mapper.update(obj, null);
        }
        return null;
    }

    @Override
    public T findById(Serializable id) {
        return (T)mapper.selectById(id);
    }

    @Override
    public List<T> findList(Query query) {
        Wrapper<T>  wrapper = query2Wrapper(query);
        return mapper.selectList(wrapper);
    }

    @Override
    public boolean deleteById(Serializable id) {
        int count =  mapper.deleteById(id);
        return  (count > 1) ? true : false;
    }
}
