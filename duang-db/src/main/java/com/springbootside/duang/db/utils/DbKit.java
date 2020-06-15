package com.springbootside.duang.db.utils;

import com.springbootside.duang.db.dto.SearchDto;
import com.springbootside.duang.db.dto.SearchListDto;
import org.beetl.sql.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class DbKit {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbKit.class);

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
            createAndQuery(query, searchDtoList);
        } else if ("or".equalsIgnoreCase(operator)){
            createOrQuery(query, searchDtoList);
        }
        return query;
    }

    private static Query createAndQuery(Query query, List<SearchDto> searchListDto) {
        for (SearchDto searchDto : searchListDto) {
            if ("=".equals(searchDto.getOperator())) {
                query.andEq(searchDto.getField(), searchDto.getValue());
            }
        }
        return query;
    }

    private static Query createOrQuery(Query query, List<SearchDto> searchListDto) {
        for (SearchDto searchDto : searchListDto) {

        }
        return query;
    }
}
