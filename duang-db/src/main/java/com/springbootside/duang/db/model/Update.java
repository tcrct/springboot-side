package com.springbootside.duang.db.model;

import com.springbootside.duang.db.utils.DbKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 更新对象
 * 只适用于CurdService.save方法调用，根据ID字段作为查询条件
 *
 * @author Laotang
 * @since 1.0
 */
public class Update {

    private static final Logger LOGGER = LoggerFactory.getLogger(Update.class);

    private String tableName;
    private IdEntity entity;
    private List<Object> paramList = new ArrayList<>();
    private String updateParamString;

    public Update(String tableName, IdEntity entity) {
        this.tableName = tableName;
        this.entity = entity;
        getParams();
    }

    public String getUpdateSql() {
        if (null == updateParamString) {
            throw new NullPointerException("生成update sql语句时出错，更新条件不允许不存在！");
        }
        StringBuilder updateStr = new StringBuilder("update ");
        updateStr.append("`").append(tableName).append("` set ")
                .append(updateParamString);
        return updateStr.toString();
    }

    public List<Object> getParams() {
        if (!paramList.isEmpty()) {
            return paramList;
        }
        Field[] fields = DbKit.getFields(entity.getClass());
        try {
            StringBuilder updateParamStr = new StringBuilder();
            Field idField = null;
            for (Field field : fields) {
                String fieldName = field.getName();
                if (null == idField && IdEntity.ID_FIELD.equalsIgnoreCase(fieldName)) {
                    idField = field;
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(entity);
                if (null != value && !IdEntity.ID_FIELD.equalsIgnoreCase(fieldName)) {
                    paramList.add(value);
                    updateParamStr.append("`").append(fieldName).append("`").append("=?,");
                }
            }
            if (updateParamStr.length()>1){
                updateParamStr.deleteCharAt(updateParamStr.length()-1);
            }

            if (null == idField) {
                throw new IllegalAccessException("执行该Update操作时，id值必须要作为查询条件！");
            }

            if (updateParamStr.length()>1 && null != idField) {
                updateParamStr.append(" where ")
                        .append("`").append(IdEntity.ID_FIELD).append("`")
                        .append("=?");
                paramList.add(idField.get(entity));
            }
            updateParamString = updateParamStr.toString();
        } catch (Exception e){
            LOGGER.warn(e.getMessage(), e);
        }
        return paramList;
    }

}
