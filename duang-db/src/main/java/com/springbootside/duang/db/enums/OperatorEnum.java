package com.springbootside.duang.db.enums;

public enum  OperatorEnum {

    //query condition
//    public static final String EQ = "=";
//    public static final String GT = ">";
//    public static final String GTE = ">=";
//    public static final String LT = "<";
//    public static final String LTE = "<=";
//    public static final String NE = "!=";
//    public static final String IN = "in";
//    public static final String NIN = "not in";
//    public static final String WHERE = "where";

//    public static final String GT = "$gt";
//    public static final String GTE = "$gte";
//    public static final String LT = "$lt";
//    public static final String LTE = "$lte";
//    public static final String NE = "$ne";
//    public static final String IN = "$in";
//    public static final String NIN = "$nin";
//    public static final String MOD = "$mod";
//    public static final String ALL = "$all";
//    public static final String SLICE = "$slice";
//    public static final String SIZE = "$size";
//    public static final String EXISTS = "$exists";
//    public static final String WHERE = "$where";
//    public static final String ELEMMATCH = "$elemMatch";

    ID("_id", "id","主键ID字段名称"),
    EQ("$eq","=", "等于"),
    NE("$ne", "<>", "不等于"),
    NE2("$ne", "!=", "不等于"),
    GT("$gt",">", "大于"),
    GTE("$gte",">=", "大于等于"),
    LT("$lt","<", "小于"),
    LTE("$lte","<=", "小于等于"),
    IN("$in","in", "小于"),
    NIN("$nin","not in", "小于等于"),
    LIKE("","like", "模糊查询"),
    NLIKE("","not like", "反模糊查询"),
    ;
    /**
     * mongodb的key
     */
    private final String mkey;
    /**
     * mysql的key
     */
    private final String skey;
    /**
     * 说明
     */
    private final String desc;

    /**
     * Constructor.
     */
    private OperatorEnum(String mkey, String skey, String desc) {
        this.mkey = mkey;
        this.skey = skey;
        this.desc = desc;
    }

    /**
     * Get the value.
     *
     * @return the value
     */
    public String getMkey() {
        return mkey;
    }

    public String getSkey() {
        return skey;
    }

    public String getDesc() {
        return desc;
    }

}
