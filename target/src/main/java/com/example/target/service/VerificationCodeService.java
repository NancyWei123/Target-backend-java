package com.example.target.service;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    private final StringRedisTemplate stringRedisTemplate;

    public VerificationCodeService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveCode(String email, String code) {
        System.out.println(" email "+email+" code "+code);
        stringRedisTemplate
                .opsForValue()
                .set(email, code, Duration.ofMinutes(10));
    }

    public String getCode(String email) {
        return stringRedisTemplate.opsForValue().get(email);
    }

    public void deleteCode(String email) {
        stringRedisTemplate.delete(email);
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = getCode(email);
        if (savedCode == null) {
            return false;
        }
        if (!savedCode.equals(code)) {
            return false;
        }
        return true;
    }
}