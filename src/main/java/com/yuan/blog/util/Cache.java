package com.yuan.blog.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 简单的内存缓存工具类
 */
public class Cache {
    //键值对集合
    private final static Map<String, Entity> map = new HashMap<>();

    /**
     * 添加缓存
     *
     * @param key  键
     * @param data 值
     */
    public synchronized static void put(String key, Object data) {
        Cache.put(key, data, 0);
    }

    /**
     * 添加缓存
     *
     * @param key    键
     * @param data   值
     * @param expire 过期时间，单位：毫秒， 0表示无限长
     */
    public synchronized static void put(String key, Object data, long expire) {
        //清除原键值对
        Cache.remove(key);
        //设置过期时间
        if (expire > 0) {
            map.put(key, new Entity(data, System.currentTimeMillis() + expire));
        } else {
            map.put(key, new Entity(data, 0L));
        }
    }

    /**
     * 读取缓存
     *
     * @param key 键
     */
    public synchronized static Object get(String key) {
        Entity entity = map.get(key);
        return entity == null ? null : entity.getValue();
    }

    /**
     * 读取缓存
     *
     * @param key 键
     *            * @param clazz 值类型
     */
    public synchronized static <T> T get(String key, Class<T> clazz) {
        return clazz.cast(Cache.get(key));
    }

    /**
     * 清除缓存
     */
    public synchronized static Object remove(String key) {
        //清除原缓存数据
        return map.remove(key);
    }

    /**
     * 查询当前缓存的键值对数量
     */
    public synchronized static int size() {
        return map.size();
    }

    // todo 测试是否是线程安全额
    // 这种遍历效率很低的
    public synchronized static int removeExpireCache() {
        if (map.isEmpty()) return 0;
        Set<Map.Entry<String, Entity>> entries = map.entrySet();
        Iterator<Map.Entry<String, Entity>> iterator = entries.iterator();
        int count = 0;
        while (iterator.hasNext()){
            Map.Entry<String, Entity> next = iterator.next();
            Entity entity = next.getValue();
            System.out.println(System.currentTimeMillis());
            if (System.currentTimeMillis() > entity.getexpireAt()){
                iterator.remove();
                count ++;
            }
        }
        return count;
    }

    /**
     * 缓存实体类
     */
    private static class Entity {
        //键值对的value
        private Object value;

        private Long expireAt;

        public Entity(Object value, Long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        /**
         * 获取值
         */
        public Long getexpireAt() {
            return expireAt;
        }

        /**
         * 获取值
         */
        public Object getValue() {
            if (expireAt > 0 && System.currentTimeMillis() > expireAt) {
                // 有过期时间 且已经过期时就直接返回null
                return null;
            }
            return value;
        }
    }
}

