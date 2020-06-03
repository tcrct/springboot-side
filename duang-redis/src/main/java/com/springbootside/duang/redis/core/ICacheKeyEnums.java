package com.springbootside.duang.redis.core;

/**
 * 缓存Key枚举接口
 *
 * @author Laotang
 * @since 1.0
 */
public interface ICacheKeyEnums {

    /**
     *  默认的过期时间，默认为30分钟
     */
    int DEFAULT_TTL = 60*30;
    /**
     * 1年
     */
    int ONE_YEAR_TTL = DEFAULT_TTL * 2 * 24 * 365;
    /**
     *  因为setex方法设置-1时会抛出异常， 所以设置成10年，当永不过期处理
     */
    int NEVER_TTL = ONE_YEAR_TTL * 10 ;

    /**
     *取出缓存Key的前缀
     */
    String getKeyPrefix();

    /**
     * 取出缓存Key的有效时间,秒作单位
     * @return
     */
    int getKeyTTL();

    /**
     * 缓存用途说明
     */
    String getKeyDesc();

}
