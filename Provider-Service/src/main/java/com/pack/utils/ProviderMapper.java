package com.pack.utils;

import com.pack.common.dto.ProviderResponseDTO;
import com.pack.entity.Provider;

public class ProviderMapper {
    public static ProviderResponseDTO toDto(Provider p) {
        return ProviderResponseDTO.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .phoneNumber(p.getPhoneNumber())
                .email(p.getEmail())
                .gender(p.getGender())
                .age(p.getAge())
                .profileImageUrl(p.getProfileImageUrl())
                .location(p.getLocation())
                .providerType(p.getProviderType().name())
                .industryType(p.getIndustryType())
                .servicesOffered(p.getServicesOffered())
                .experienceInYears(p.getExperienceInYears())
                .hourlyRate(p.getHourlyRate())
                .isVerified(p.isVerified())
                .verificationStars(p.getVerificationStars())
                .isActive(p.getIsActive())
                .isOnline(p.getIsOnline())
                .lastLoginAt(p.getLastLoginAt())
                .lastLogoutAt(p.getLastLogoutAt())
                .teamSize(p.getTeamSize())
                .licenseNumber(p.getLicenseNumber())
                .isLocked(p.isLocked())
                .reasonForLock(p.getReasonForLock())
                .submitted(p.isSubmitted())
                .remarks(p.getRemarks())
                .verificationStatus(p.getVerificationStatus().name())
                .role(p.getRole())
                .isEnabled(p.isEnabled())
                .build();
    }
}
