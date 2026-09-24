package com.sloth.boot.starter.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 配置属性。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "sloth.redis")
public class RedisProperties {

    /**
     * 是否启用 Redis Starter。
     */
    private boolean enabled = true;

    /**
     * 统一业务 key 前缀。
     */
    private String keyPrefix = "sloth:";

    /**
     * 分布式锁默认等待时间，单位秒。
     */
    private long lockWaitTime = 3L;

    /**
     * 分布式锁默认租约时间，单位秒。
     */
    private long lockLeaseTime = 30L;

    /**
     * 是否携带类型信息进行 JSON 序列化。
     * <p>
     * 默认关闭以避免不安全的多态反序列化；开启时仅允许白名单包前缀（java.lang/java.util/java.time/com.sloth）。
     */
    private boolean enableTypeInfo = false;

    /**
     * 空值缓存时间，单位秒。
     */
    private long nullValueExpireSeconds = 60L;

    /**
     * SCAN 命令每次迭代的 count 提示值。
     */
    private int scanCount = 500;

    /**
     * 多级缓存配置。
     */
    private MultiCache multiCache = new MultiCache();

    /**
     * 分布式 ID 生成器配置。
     */
    private IdGenerator idGenerator = new IdGenerator();

    /**
     * 布隆过滤器配置。
     */
    private BloomFilter bloomFilter = new BloomFilter();

    /**
     * Pub/Sub 配置。
     */
    private PubSub pubSub = new PubSub();

    /**
     * 多级缓存配置。
     */
    @Data
    public static class MultiCache {

        /**
         * 是否启用多级缓存（Caffeine + Redis）。
         */
        private boolean enabled = false;

        /**
         * L1 Caffeine 缓存最大条目数。
         */
        private int l1MaxSize = 1000;

        /**
         * L1 Caffeine 缓存过期时间（秒）。
         */
        private long l1TtlSeconds = 300;
    }

    /**
     * 分布式 ID 生成器配置。
     */
    @Data
    public static class IdGenerator {

        /**
         * 是否启用分布式 ID 生成器。
         */
        private boolean enabled = true;

        /**
         * 机器号（0-1023）。
         */
        private int workerId = 0;

        /**
         * ID 前缀。
         */
        private String prefix = "sloth";
    }

    /**
     * 布隆过滤器配置。
     */
    @Data
    public static class BloomFilter {

        /**
         * 是否启用布隆过滤器。
         */
        private boolean enabled = false;

        /**
         * 布隆过滤器名称（对应 Redis key）。
         */
        private String name = "sloth:bloom:default";

        /**
         * 预期插入元素数量。
         */
        private long expectedInsertions = 1000000L;

        /**
         * 误判概率。
         */
        private double falsePositiveProbability = 0.01;
    }

    /**
     * Pub/Sub 配置。
     */
    @Data
    public static class PubSub {

        /**
         * 是否启用 Pub/Sub 模板。
         */
        private boolean enabled = false;
    }
}
