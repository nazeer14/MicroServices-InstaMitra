package com.pack.utils;

import com.pack.common.dto.Gender;
import com.pack.common.dto.ProviderRequestDTO;
import com.pack.common.dto.ProviderResponseDTO;
import com.pack.entity.Provider;
import com.pack.enums.ProviderType;

public class ProviderMapper {

    public static Provider toEntity(ProviderRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Provider.builder()
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .gender(Gender.valueOf(dto.getGender()))
                .age(dto.getAge())
                .profileImageUrl(dto.getProfileImageUrl())
                .location(dto.getLocation())
                .providerType(ProviderType.valueOf(dto.getProviderType()))
                .industryType(dto.getIndustryType())
                .servicesOffered(dto.getServicesOffered())
                .serviceIds(dto.getServiceIds())
                .experienceInYears(dto.getExperienceInYears())
                .hourlyRate(dto.getHourlyRate())
                .teamSize(dto.getTeamSize())
                .licenseNumber(dto.getLicenseNumber())
                .build();
    }

    public static ProviderResponseDTO toDto(Provider entity) {
        if (entity == null) {
            return null;
        }

        return ProviderResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .gender(entity.getGender().name())
                .age(entity.getAge())
                .profileImageUrl(entity.getProfileImageUrl())
                .location(entity.getLocation())
                .providerType(entity.getProviderType().name())
                .industryType(entity.getIndustryType())
                .servicesOffered(entity.getServicesOffered())
                .serviceIds(entity.getServiceIds())
                .experienceInYears(entity.getExperienceInYears())
                .hourlyRate(entity.getHourlyRate())
                .isVerified(entity.isVerified())
                .verificationStars(entity.getVerificationStars())
                .isActive(entity.getIsActive())
                .isOnline(entity.getIsOnline())
                .lastLoginAt(entity.getLastLoginAt())
                .lastLogoutAt(entity.getLastLogoutAt())
                .teamSize(entity.getTeamSize())
                .licenseNumber(entity.getLicenseNumber())
                .remarks(entity.getRemarks())
                .role(entity.getRole())
                .isEnabled(entity.isEnabled())
                .build();
    }
}
