package com.pack.utils;

import com.pack.common.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final StringRedisTemplate redisTemplate;


    public void generateAndStoreOtp(String phone) {
        String otp = OtpGenerator.generateOtp(6);
        redisTemplate.opsForValue().set("p"+phone, otp, 1, TimeUnit.MINUTES);
        System.out.println("OTP sent: " + otp);
    }

    public boolean validateOtp(String phone, String otp) {
        String cachedOtp = redisTemplate.opsForValue().get("p"+phone);
        return otp.equals(cachedOtp);
    }
}

