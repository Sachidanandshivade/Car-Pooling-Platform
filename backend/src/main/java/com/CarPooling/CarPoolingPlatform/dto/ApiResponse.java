package com.CarPooling.CarPoolingPlatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private Object data;
    private LocalDateTime timestamp;
}
