package com.pack.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private String phoneNumber;
    private boolean isVerified;
    private boolean grantAccess;
}

