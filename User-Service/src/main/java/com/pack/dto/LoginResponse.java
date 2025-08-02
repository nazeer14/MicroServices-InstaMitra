package com.pack.dto;

import com.pack.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
    private LocalDateTime timeStamp=LocalDateTime.now();
}
