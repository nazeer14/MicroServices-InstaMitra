package com.pack.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final StringRedisTemplate redisTemplate;

    public void generateAndStoreOtp(String phone) {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        redisTemplate.opsForValue().set("u-"+phone, otp, 1, TimeUnit.MINUTES);
        System.out.println("OTP sent: " + otp);
    }

    public boolean validateOtp(String phone, String otp) {
        String cachedOtp = redisTemplate.opsForValue().get("u-"+phone);
        return otp.equals(cachedOtp);
    }
}

