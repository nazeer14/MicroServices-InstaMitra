package com.pack.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
   private String phoneNumber;
   private String otp;
}
