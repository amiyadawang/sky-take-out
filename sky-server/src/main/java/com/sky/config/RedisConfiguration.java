package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Configurable
@Slf4j
public class RedisConfiguration {
public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
    log.info("创建RedisTemplate");
    RedisTemplate redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    return redisTemplate;
}}
