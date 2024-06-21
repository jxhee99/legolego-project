package com.kosta.legolego.user.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, String> redisTemplate;


    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValuesWithTimeout(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    public String getValues(String key) {
        String value =redisTemplate.opsForValue().get(key);
        logger.info("Fetched from Redis - Key: {}, Value: {}", key, value);
        return value;
      //  return redisTemplate.opsForValue().get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
