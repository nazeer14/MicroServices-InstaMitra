package com.pack.common.dto;

import com.pack.common.enums.Role;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponseDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String gender;
    private Integer age;
    private String profileImageUrl;
    private String location;
    private String providerType;
    private String industryType;
    private List<String> servicesOffered;
    private Integer experienceInYears;
    private BigDecimal hourlyRate;
    private boolean isVerified;
    private Integer verificationStars;
    private Boolean isActive;
    private Boolean isOnline;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastLogoutAt;
    private Integer teamSize;
    private String licenseNumber;
    private boolean isLocked;
    private String reasonForLock;
    private boolean submitted;
    private List<String> remarks;
    private Role role;
    private boolean isEnabled;
    private String verificationStatus;


}
