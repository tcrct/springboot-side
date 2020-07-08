package com.springbootside.duang.redis.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.springbootside.duang.redis.RedisFactory;
import com.springbootside.duang.redis.core.CacheKeyModel;
import com.springbootside.duang.redis.serializer.FstSerializer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

    public static void main(String[] args) {
        String dir = "E:\\svn\\coupon/webCpm/src/main/java/com/tangdi/production\\tdcoupon/cache";
        String daoPackage = dir.substring(dir.indexOf("com"));
        daoPackage = daoPackage.replace("\\", ".").replace("/", ".");
        System.out.println(daoPackage);
    }

    public static void main2(String[] args) {
        String remake = "我也是Spring用户，所以我想将此Web服务器与Spring MVC集成，但是到目前为止，只有可以在其中找到Spring-Undertow集成的示例在Spring Boot项目中，由于某种原因，我仍然使用纯Spring框架，因此我开始自己做。当然，由于Spring中的一切都是bean，因此我开发了自己的UndertowServerbean非常简单，更大的问题是如何在其中定义我的Web应用程序部署，因为有几种方法可以实现它。我决定使用Servlet 3.0+ ServletContainertInitializer（另一个选择是直接使用Undertow的API来指定Web应用程序部署）。";
        long stime = System.currentTimeMillis();
        for (int i=0; i<2000; i++) {
            RedisTestUser entity = new RedisTestUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "珠海市", "123456@abcdefkdfjkfkjsdl.com", new Date(), remake);
            JSON.toJSONString(entity);
        }
        System.out.println("FASTJSON: "  + (System.currentTimeMillis()- stime));

        stime = System.currentTimeMillis();
        for (int i=0; i<2000; i++) {
            RedisTestUser entity = new RedisTestUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "珠海市", "123456@abcdefkdfjkfkjsdl.com", new Date(), remake);
            FstSerializer.me.valueToBytes(entity);
        }
        System.out.println("FST: " + (System.currentTimeMillis()- stime));
    }

    public static void main3(String[] args) {
        RedisFactory redisFactory = new RedisFactory("test", "127.0.0.1",6379);
        redisFactory.start();
        String id = "123";
        CacheKeyModel cacheKeyModel =new CacheKeyModel.Builder(TestCacheKeyEnum.MER_ID).customKey(id).build();
//        String id, String name, String address, String email, Date bother, String remake
        String remake = "我也是Spring用户，所以我想将此Web服务器与Spring MVC集成，但是到目前为止，只有可以在其中找到Spring-Undertow集成的示例在Spring Boot项目中，由于某种原因，我仍然使用纯Spring框架，因此我开始自己做。当然，由于Spring中的一切都是bean，因此我开发了自己的UndertowServerbean非常简单，更大的问题是如何在其中定义我的Web应用程序部署，因为有几种方法可以实现它。我决定使用Servlet 3.0+ ServletContainertInitializer（另一个选择是直接使用Undertow的API来指定Web应用程序部署）。";
        List<RedisTestUser> userList = new ArrayList<>();
        RedisTestUser entity = null;
        Map map = new HashMap();
        for (int i=0; i<10; i++) {
            entity = new RedisTestUser(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "珠海市", "123456@abcdefkdfjkfkjsdl.com", new Date(), remake);
            map.put(new AtomicInteger().incrementAndGet()+"", entity);
//            map = JSONObject.parseObject(JSON.toJSONString(entity), Map.class);

            userList.add(entity);
        }
//        RedisFactory.getCache("test").set(cacheKeyModel, JSON.toJSONString(userList));

        RedisFactory.getCache("test").set(cacheKeyModel, JSON.toJSONString(entity));
//        List<RedisTestUser> list = RedisFactory.getCache("test").get(cacheKeyModel);
//        for (RedisTestUser user : list) {
//            System.out.println("#########: " + JSON.toJSONString(user));
//        }
        String str = RedisFactory.getCache().get(cacheKeyModel);
        System.out.println(str);
        cacheKeyModel =new CacheKeyModel.Builder(TestCacheKeyEnum.MER_ID).customKey("234").build();
        RedisFactory.getCache().hmset(cacheKeyModel, map);
//        RedisFactory.getCache().set(cacheKeyModel, map);
//        Map<String,Object> resultMap = RedisFactory.getCache().hmgetToMap(cacheKeyModel, "1");

        List<RedisTestUser> resultMap = RedisFactory.getCache().hmget(cacheKeyModel, "1");
        System.out.println(resultMap.get(0).getRemake());
//        System.out.println("#########: " + JSON.toJSONString(resultMap));
    }

}
