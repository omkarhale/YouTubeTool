package com.YouTubeTool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // ================================
        // ObjectMapper with type info
        // ================================
        // This is the KEY fix!
        // Without activateDefaultTyping, Jackson
        // stores JSON but loses the class type info.
        // When Redis tries to deserialize back to
        // VideoDetails or SearchVideo — it fails silently
        // and returns null instead of throwing error.
        //
        // activateDefaultTyping tells Jackson:
        // "Store the class name inside the JSON too"
        // So Redis knows exactly what object to recreate
        // ================================
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        // Default config for all caches
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer))
                .disableCachingNullValues();

        // Per-cache TTL settings
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // Search results cached for 1 hour
        cacheConfigs.put("videoSearch",
                defaultConfig.entryTtl(Duration.ofHours(1)));

        // Video details cached for 6 hours
        cacheConfigs.put("videoDetails",
                defaultConfig.entryTtl(Duration.ofHours(6)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}