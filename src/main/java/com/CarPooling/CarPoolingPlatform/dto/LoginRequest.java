package com.CarPooling.CarPoolingPlatform.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
