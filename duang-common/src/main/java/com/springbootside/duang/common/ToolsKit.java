package com.springbootside.duang.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 *
 * @author Laotang
 * @version 1.0
 */
public class ToolsKit {

    private final static Logger LOGGER = LoggerFactory.getLogger(ToolsKit.class);

    public static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        /**过滤对象的null属性*/
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        /**过滤map中的null key*/
        objectMapper.getSerializerProvider().setNullKeySerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException, JsonProcessingException {
                generator.writeFieldName("");
            }
        });
        /**过滤map中的null值*/
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator generator, SerializerProvider serializers) throws IOException, JsonProcessingException {
                generator.writeString("");
            }
        });
        //指定遇到date按照这种格式转换
        objectMapper.setDateFormat(SDF);
        //配置该objectMapper在反序列化时，忽略目标对象没有的属性。
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // 定义一个请求对象安全线程类
//    private static DuangThreadLocal<HeadDto> requestHeaderThreadLocal = new DuangThreadLocal<HeadDto>() {
//        @Override
//        public HeadDto initialValue() {
//            return new HeadDto();
//        }
//    };

    /***
     * 判断传入的对象是否为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,为空或等于0时返回true
     */
    public static boolean isEmpty(Object obj) {
        return checkObjectIsEmpty(obj, true);
    }

    /***
     * 判断传入的对象是否不为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,不为空或不等于0时返回true
     */
    public static boolean isNotEmpty(Object obj) {
        return checkObjectIsEmpty(obj, false);
    }

    @SuppressWarnings("rawtypes")
    private static boolean checkObjectIsEmpty(Object obj, boolean bool) {
        if (null == obj) {
            return bool;
        }
        else if (obj == "" || "".equals(obj)) {
            return bool;
        }
        else if (obj instanceof Integer || obj instanceof Long || obj instanceof Double) {
            try {
                Double.parseDouble(obj + "");
            } catch (Exception e) {
                return bool;
            }
        } else if (obj instanceof String) {
            if (((String) obj).length() <= 0) {
                return bool;
            }
            if ("null".equalsIgnoreCase(obj+"")) {
                return bool;
            }
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0) {
                return bool;
            }
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) {
                return bool;
            }
        } else if (obj instanceof Object[]) {
            if (((Object[]) obj).length == 0) {
                return bool;
            }
        }
        return !bool;
    }

    /**
     * json字符串转换为对象
     *
     * @param jsonStr json格式的字符串
     * @param clazz   待转换的对象
     * @param <T>     返回泛型值
     * @return
     * @throws Exception
     */
    public static <T> T jsonParseObject(String jsonStr, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * json格式字符串转换为List集合
     * @param jsonStr
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonParseArray(String jsonStr, TypeReference<T> typeReference) {
        try {
            return (List<T>) objectMapper.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * 序列化成json格式的字符串
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * <p>
     * 反射对象获取泛型
     * </p>
     *
     * @param clazz 对象
     * @param index 泛型所在位置
     * @return Class
     */
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


}
