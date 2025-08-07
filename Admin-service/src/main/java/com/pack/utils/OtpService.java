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

    public void generateAndStoreOtp(String phone,String otp) {
        redisTemplate.opsForValue().set("a-"+phone, otp, 1, TimeUnit.MINUTES);
        System.out.println("OTP sent: " + otp);//testing only
    }

    public boolean validateOtp(String phone, String otp) {
        String cachedOtp = redisTemplate.opsForValue().get("a-"+phone);
        return otp.equals(cachedOtp);
    }
}

