package com.example.api_gateway.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicError {
    private String key;
    private String message;


    public static BasicError of(String key, String message) {
        return BasicError.builder()
                .key(key)
                .message(message)
                .build();
    }
}
