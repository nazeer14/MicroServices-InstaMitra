package com.pack.common.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderRequestDTO {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String gender;

    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    private String profileImageUrl;

    private String location;

    private String providerType; // INDIVIDUAL, TEAM, NETWORK

    private String industryType; // e.g., Beauty Parlor, Garage

    private List<String> servicesOffered;

    private List<String> serviceIds;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceInYears;

    @DecimalMin(value = "0.0", inclusive = true, message = "Hourly rate cannot be negative")
    private BigDecimal hourlyRate;

    private Integer teamSize;

    private String licenseNumber;

}
