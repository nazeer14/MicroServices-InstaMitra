package com.pack.entity;

import com.pack.common.enums.Role;
import com.pack.enums.ProviderType;
import com.pack.enums.VerificationStatus;
import jakarta.persistence.*;
import jdk.jshell.ImportSnippet;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "providers")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(nullable = false, unique = true, length = 15)
    private String phoneNumber;

    private String email;
    private String gender;

    private Integer age;

    private String profileImageUrl;

    private String location;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType=ProviderType.INDIVIDUAL; // INDIVIDUAL or TEAM or NETWORK

    private String industryType; // e.g., Beauty Parlor, Garage

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_services", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "service")
    private List<String> servicesOffered;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_ids", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "service")
    private List<String> serviceIds;

    private Integer experienceInYears;
    private BigDecimal hourlyRate;

    private boolean isVerified;
    private Integer verificationStars;

    private Boolean isActive = true;
    private Boolean isOnline = false;

    private LocalDateTime lastLoginAt;
    private LocalDateTime lastLogoutAt;

    private Integer teamSize; // only for BUSINESS type

    private String licenseNumber; // optional for registered businesses


    @Version
    private int version;

    private boolean isLocked=false;

    private String reasonForLock;

    private boolean isSubmitted=false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_remarks", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "remarks")
    private List<String> remarks;

    @Column(name = "verification_status")
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    private Role role= Role.PROVIDER;

    private boolean isEnabled=true;

}


