package com.pack.common.util;


import java.security.SecureRandom;

public class OtpGenerator {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }
}
