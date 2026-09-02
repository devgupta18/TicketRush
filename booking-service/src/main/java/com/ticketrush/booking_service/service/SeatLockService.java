package com.ticketrush.booking_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class SeatLockService {
    @Value("${booking.seat-lock.ttl-seconds}")
    private int ttlSeconds;
    private final StringRedisTemplate stringRedisTemplate;

    public SeatLockService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private String buildLockKey(Long showId, Long seatNumber) {
        return "lock:show:" + showId + ":seat" + seatNumber;
    }

    public boolean acquireLock(Long showId, Long seatNumber, Long userId) {
        String key = buildLockKey(showId, seatNumber);
        String value = userId.toString();

        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(ttlSeconds));
        return result != null && result;
    }

    public boolean releaseLock(Long showId, Long seatNumber, Long userId) {
        String key = buildLockKey(showId, seatNumber);
        String value = stringRedisTemplate.opsForValue().get(key);

        if(value != null &&  value.equals(userId.toString())) {
            stringRedisTemplate.delete(key);
            return true;
        }
        return false;
    }

    public long getRemainingTtl(Long showId, Long seatNumber) {
        String key = buildLockKey(showId, seatNumber);
        Long ttlExpiryTime = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        if(ttlExpiryTime == null) {
            return -2L;
        }

        return ttlExpiryTime;
    }
}