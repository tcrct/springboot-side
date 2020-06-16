package com.springbootside.duang.db.utils;

import com.springbootside.duang.db.dto.SearchDto;
import com.springbootside.duang.db.dto.SearchListDto;
import com.springbootside.duang.db.enums.OperatorEnum;
import com.springbootside.duang.db.model.IdEntity;
import com.springbootside.duang.db.model.Update;
import org.beetl.sql.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DbKit {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbKit.class);
    private static final ConcurrentMap<String, Field[]> FIELD_MAPPING_MAP = new ConcurrentHashMap<>();

    public static Class<?> getSuperClassGenericType(final Class<?> clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            LOGGER.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            LOGGER.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index,
                    clazz.getSimpleName(), params.length));
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            LOGGER.warn(String.format("Warn: %s not set the actual class on superclass generic parameter",
                    clazz.getSimpleName()));
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    public static Query createQueryCondition(Query query, SearchListDto searchListDto) {
        List<SearchDto> searchDtoList = searchListDto.getSearchDtoList();
        String operator = searchListDto.getOperator().trim();
        if ("and".equalsIgnoreCase(operator)) {
            return createAndQuery(query, searchDtoList);
        } else if ("or".equalsIgnoreCase(operator)){
            return createOrQuery(query, searchDtoList);
        }
        return query;
    }

    private static Query createAndQuery(Query query, List<SearchDto> searchListDto) {
        for (SearchDto searchDto : searchListDto) {
            String operator = searchDto.getOperator();
            // =
            if (OperatorEnum.EQ.getSkey().equalsIgnoreCase(operator)) {
                query.andEq(searchDto.getField(), searchDto.getValue());
            }// !=
            else if (OperatorEnum.NE.getSkey().equalsIgnoreCase(operator) || OperatorEnum.NE2.getSkey().equalsIgnoreCase(operator)) {
                query.andNotEq(searchDto.getField(), searchDto.getValue());
            }// >
            else if (OperatorEnum.GT.getSkey().equalsIgnoreCase(operator)) {
                query.andGreat(searchDto.getField(), searchDto.getValue());
            } // >=
            else if (OperatorEnum.GTE.getSkey().equalsIgnoreCase(operator)) {
                query.andGreatEq(searchDto.getField(), searchDto.getValue());
            }//<
            else if (OperatorEnum.LT.getSkey().equalsIgnoreCase(operator)) {
                query.andLess(searchDto.getField(), searchDto.getValue());
            } // <=
            else if (OperatorEnum.LTE.getSkey().equalsIgnoreCase(operator)) {
                query.andLessEq(searchDto.getField(), searchDto.getValue());
            }// like
            else if (OperatorEnum.LIKE.getSkey().equalsIgnoreCase(operator)) {
                query.andLike(searchDto.getField(), String.valueOf(searchDto.getValue()));
            }// not like
            else if (OperatorEnum.NLIKE.getSkey().equalsIgnoreCase(operator)) {
                query.andNotLike(searchDto.getField(), String.valueOf(searchDto.getValue()));
            } else {
                throw new IllegalArgumentException("暂不支持["+operator+"]查询");
            }
        }
        return query;
    }

    private static Query createOrQuery(Query query, List<SearchDto> searchListDto) {
        for (SearchDto searchDto : searchListDto) {
            String operator = searchDto.getOperator();
            // =
            if (OperatorEnum.EQ.getSkey().equalsIgnoreCase(operator)) {
                query.orEq(searchDto.getField(), searchDto.getValue());
            }// !=
            else if (OperatorEnum.NE.getSkey().equalsIgnoreCase(operator) || OperatorEnum.NE2.getSkey().equalsIgnoreCase(operator)) {
                query.orNotEq(searchDto.getField(), searchDto.getValue());
            }// >
            else if (OperatorEnum.GT.getSkey().equalsIgnoreCase(operator)) {
                query.orGreat(searchDto.getField(), searchDto.getValue());
            } // >=
            else if (OperatorEnum.GTE.getSkey().equalsIgnoreCase(operator)) {
                query.orGreatEq(searchDto.getField(), searchDto.getValue());
            }//<
            else if (OperatorEnum.LT.getSkey().equalsIgnoreCase(operator)) {
                query.orLess(searchDto.getField(), searchDto.getValue());
            } // <=
            else if (OperatorEnum.LTE.getSkey().equalsIgnoreCase(operator)) {
                query.orLessEq(searchDto.getField(), searchDto.getValue());
            }// like
            else if (OperatorEnum.LIKE.getSkey().equalsIgnoreCase(operator)) {
                query.orLike(searchDto.getField(), String.valueOf(searchDto.getValue()));
            }// not like
            else if (OperatorEnum.NLIKE.getSkey().equalsIgnoreCase(operator)) {
                query.orNotLike(searchDto.getField(), String.valueOf(searchDto.getValue()));
            } else {
                throw new IllegalArgumentException("暂不支持["+operator+"]查询");
            }
        }
        return query;
    }

    /**
     * 根据class对象反射出所有属性字段，静态字段除外
     * @param cls
     * @return
     */
    public static Field[] getFields(Class<?> cls){
        String key = cls.getName();
        Field[] field = null;
        if(FIELD_MAPPING_MAP.containsKey(key)){
            field = FIELD_MAPPING_MAP.get(key);
        }else{
            field = getAllFields(cls);
            FIELD_MAPPING_MAP.put(key, field);
        }
        return (null == field) ? null : field;
    }

    /**
     * 根据class对象反射出所有属性字段，静态字段除外
     * @param cls
     * @return  Map集合，key为field.getName()
     */
    public static Map<String, Field> getFieldMap(Class<?> cls) {
        Field[] fileds = getFields(cls);
        if(null == fileds) {
            return null;
        }
        Map<String, Field> map = new HashMap<>(fileds.length);
        for(Field field : fileds) {
            if(null != field) {
                map.put(field.getName(), field);
            }
        }
        return map;
    }

    /**
     * 取出类里的所有字段
     * @param cls
     * @return	Field[]
     */
    private static Field[] getAllFields(Class<?> cls) {
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(filterStaticFields(cls.getDeclaredFields()));
        Class<?> parent = cls.getSuperclass();
        //查找父类里的属性字段
        while(null != parent && parent != Object.class){
            fieldList.addAll(filterStaticFields(parent.getDeclaredFields()));
            parent = parent.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 过滤静态方法
     * @param fields
     * @return
     */
    private static List<Field> filterStaticFields(Field[] fields){
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if(!Modifier.isStatic(field.getModifiers())){		//静态字段不取
                field.setAccessible(true);	//设置可访问私有变量
                result.add(field);
            }
        }
        return result;
    }

}
