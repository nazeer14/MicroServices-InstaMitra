package com.pack.common.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProviderRequestDTO {

    private String fullName;

    @Email
    private String email;

    private String gender;

    @Min(18)
    private Integer age;

    private String profileImageUrl;

    private String location;

    private String providerType;

    private String industryType;

    private List<String> servicesOffered;

    @Min(0)
    private Integer experienceInYears;

    @DecimalMin("0.0")
    private BigDecimal hourlyRate;

    private Integer teamSize;

    private String licenseNumber;
}

