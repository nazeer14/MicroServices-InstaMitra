package com.pack.dto;

import com.pack.common.dto.Gender;
import com.pack.enums.ProviderType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDetails {

    private String fullName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private Gender gender;

    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    private String profileImageUrl;

    private String location;

    private ProviderType providerType; // INDIVIDUAL, TEAM, NETWORK

    private String industryType; // e.g., Beauty Parlor, Garage

    @NotNull(message = "service names not be null")
    private List<String> servicesOffered;

    @NotNull(message = "service ids not be null")
    private List<String> serviceIds;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceInYears;

    @DecimalMin(value = "0.0", inclusive = true, message = "Hourly rate cannot be negative")
    private BigDecimal hourlyRate;

    private Integer teamSize;

    @NotNull(message = "license number is required")
    private String licenseNumber;


}
