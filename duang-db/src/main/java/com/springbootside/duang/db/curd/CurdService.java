package com.springbootside.duang.db.curd;

import cn.hutool.core.util.ReflectUtil;
import com.springbootside.duang.db.dto.PageDto;
import com.springbootside.duang.db.dto.SearchListDto;
import com.springbootside.duang.db.model.BaseEntity;
import com.springbootside.duang.db.model.Update;
import com.springbootside.duang.db.utils.DbKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * 公用的CURD方法服务基类
 *
 * @author Laotang
 * @since 1.0
 */
public class CurdService<T> implements ICurdService<T> {

    @Autowired
    private SQLManager manager;

    /** 取泛型对象类型
     * @return
     */
    protected Class<T> getGenericTypeClass() {
        return (Class<T>) DbKit.getSuperClassGenericType(getClass(), 0);
    }

    /**
     * 根据ID查找对象
     * @param id id主键
     * @return
     */
    @Override
    public T findById(Serializable id) {
        return manager.unique(getGenericTypeClass(), id);
    }

    /**
     * 根据ID，逻辑删除记录(将表中status字段值改为0)
     * @param id 待删除的记录ID
     * @return 成功删除返回受影响的行数
     */
    @Override
    public Integer deleteById(Serializable id) {
        Object entity = ReflectUtil.newInstance(getGenericTypeClass());
        ReflectUtil.setFieldValue(entity, BaseEntity.ID_FIELD, id);
        //设置为逻辑删除
        ReflectUtil.setFieldValue(entity, BaseEntity.STATUS_FIELD, 1);
        return save((T)entity);
    }

    /**
     * 保存操作
     * 以entity里是否有id值来判断是新增还是更新操作。
     * 更新操作时，会自动根据entity不为null的字段进行更新。
     *
     * @param entity 待持久化/更新的对象
     * @return
     */
    @Override
    public Integer save(T entity) {
        BaseEntity baseEntity = (BaseEntity) entity;
        if (null == baseEntity.getId() || "".equals(baseEntity.getId())) {
           return manager.insert(getGenericTypeClass(), baseEntity);
        } else {
            String tableName = manager.getDbStyle().getNameConversion().getTableName(entity.getClass());
            TableDesc tableDesc = manager.getMetaDataManager().getTable(tableName);
            Update update = new Update(tableDesc.getName(), baseEntity);
            return manager.executeUpdate(new SQLReady(update.getUpdateSql(), update.getParams().toArray()));
        }
    }

    /**
     * 根据条件搜索记录，返回分页对象
     * @param searchDto 搜索条件对象
     * @return PageDto
     */
    @Override
    public PageDto<T> search(SearchListDto searchListDto) {
        Class<T> genericTypeClass = getGenericTypeClass();
        PageDto<T> pageDto = new PageDto<>();
        String sqlId = searchListDto.getKey();
        List<T> resultList = null;
        Query<T> query = manager.query(genericTypeClass);
        if (null != sqlId) {
            resultList = manager.select(sqlId, genericTypeClass, searchListDto.toMap());
        } else {
            query = DbKit.createQueryCondition(query, searchListDto);
            // 默认按id desc排序
            query = query.orderBy(searchListDto.toOrderByStr());
            if (null != searchListDto.getGroupByList()) {
                query.groupBy(searchListDto.toGroupByStr());
            }
            query.limit(searchListDto.getPageNo(), searchListDto.getPageSize());
            resultList = (null == searchListDto.getFieldList()) ? query.select() :
                    query.select(searchListDto.getFieldList().toArray(new String[]{}));
        }
        pageDto.setAutoCount(true);
        pageDto.setResult(resultList);
        StringBuilder sql = query.getSql();
        sql.delete(0, sql.indexOf("WHERE"));
        query.setSql(sql);
        pageDto.setTotalCount(query.count());
        return pageDto;
    }
}
