package com.pack.dto;

import com.pack.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Provider provider;
}
