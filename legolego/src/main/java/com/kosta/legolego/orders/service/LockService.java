package com.kosta.legolego.orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// 분산 락 구현 위한 서비스
@Service
public class LockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 락 획득 메서드
    public boolean getLock(String lockKey, String lockValue, long expireTime) {
        boolean success = false;
        int retryCount = 5; // 재시도 횟수
        while (retryCount-- > 0) {
            Boolean gain = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
            if( gain != null && gain) {
                success = true;
                break;
            } try {
                Thread.sleep(100); // 100ms 대기 후 재시도
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return success;

    }

    // 락 해제 메서드
    public void releaseLock(String lockKey, String lockValue) {
        String value = redisTemplate.opsForValue().get(lockKey);
        if(lockValue.equals(value)) {
            redisTemplate.delete(lockKey);
        }
    }


}
