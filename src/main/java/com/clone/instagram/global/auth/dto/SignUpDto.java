package com.clone.instagram.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpDto {
    @NotBlank
    private String userName;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
