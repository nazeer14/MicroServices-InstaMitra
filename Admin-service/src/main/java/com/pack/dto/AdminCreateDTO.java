package com.pack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateDTO {

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$",message = "number must be 10 digits")
    private String phoneNumber;


    @NotBlank
    private String otp;
}
