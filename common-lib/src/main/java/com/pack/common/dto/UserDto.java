package com.pack.common.dto;

import com.pack.common.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Role role;
    private boolean isVerified;

}
