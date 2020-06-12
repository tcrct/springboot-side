package com.springbootside.duang.common.utils;

import cn.hutool.setting.Setting;
import cn.hutool.setting.SettingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.util.Arrays;
import java.util.List;

/**
 * 根据配置文件里指定key，取对应的内容值
 *
 * @author Laotang
 * @since 1.0
 */
public class SettingKit {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingKit.class);

    private static SettingKit SETTING_KIT = new SettingKit();
    private static final String PROPERTIES_NAME = "application.properties";
    private static Setting SETTING;
    private String group;
    private String key;
    private Object defalutValue;

    private SettingKit() {
        try {
            SETTING = SettingUtil.get(PROPERTIES_NAME);
        } catch (Exception e) {
            LOGGER.warn("加载[{}]配置文件时出错：{}", PROPERTIES_NAME, e.getMessage());
        }
    }

    public static SettingKit duang() {
        return SETTING_KIT;
    }

    public SettingKit group(String group) {
        this.group = group;
        return SETTING_KIT;
    }

    public SettingKit key(String key) {
        this.key = key;
        return SETTING_KIT;
    }

    public SettingKit defaultValue(Object defalutValue) {
        this.defalutValue = defalutValue;
        return SETTING_KIT;
    }

    public String getString() {
        return getValue();
    }

    public Integer getInteger() {
        String value = getValue();
        return ToolsKit.isEmpty(value) ?
                (ToolsKit.isEmpty(defalutValue) ? null : Integer.valueOf(String.valueOf(defalutValue))) :
                Integer.valueOf(value);
    }

    public Long getLong() {
        String value = getValue();
        return ToolsKit.isEmpty(value) ?
                (ToolsKit.isEmpty(defalutValue) ? null : Long.valueOf(String.valueOf(defalutValue))) :
                Long.valueOf(value);
    }

    public Double getDouble() {
        String value = getValue();
        return ToolsKit.isEmpty(value) ?
                (ToolsKit.isEmpty(defalutValue) ? null : Double.valueOf(String.valueOf(defalutValue))) :
                Double.valueOf(value);
    }

    public Boolean getBoolean() {
        String value = getValue();
        return ToolsKit.isEmpty(value) ?
                (ToolsKit.isEmpty(defalutValue) ? null : Boolean.valueOf(String.valueOf(defalutValue))) :
                Boolean.valueOf(value);
    }

    public List<String> getList() {
        String value = getValue();
        if (ToolsKit.isEmpty(value)) {
            return null;
        }
        String[] valueArray = StringUtils.commaDelimitedListToStringArray(value);
        return ToolsKit.isEmpty(value) ? null : Arrays.asList(valueArray);
    }

    private String getValue() {
        if (ToolsKit.isEmpty(key)) {
            LOGGER.info("配置文件key关键字不能为空！");
            return "";
        }
        return ToolsKit.isEmpty(group) ? SETTING.get(key) : SETTING.get(group, key);
    }




}
