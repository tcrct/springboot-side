package com.springbootside.duang.common.enums;

public enum ConstEnums {
    ;
    /**
     * BaseController类枚举
     */
    public enum BASE_CONTROLLER_PARAM implements IEnum {
        
        ID("id", "BaseController类里findById时使用的参数名称"),
        
        ;
        private final String value;
        private final String desc;
        private BASE_CONTROLLER_PARAM(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }
        @Override
        public String getValue() {
            return value;
        }
        @Override
        public String getDesc() {
            return desc;
        }
    }


}
