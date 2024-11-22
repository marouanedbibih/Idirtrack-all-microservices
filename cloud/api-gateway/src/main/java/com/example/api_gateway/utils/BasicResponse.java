package com.example.api_gateway.utils;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicResponse {
    private Object content;
    private MetaData metaData;
    private String message;
    private Map<String, String> messagesList;
    private MessageType messageType;
    private String redirectUrl;
    private HttpStatus status;
    
    // errors
    private BasicError error;
    private List<BasicError> errorsList;
}
