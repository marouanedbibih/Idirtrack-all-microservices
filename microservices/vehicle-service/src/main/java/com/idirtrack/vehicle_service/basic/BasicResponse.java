package com.idirtrack.vehicle_service.basic;


import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.idirtrack.vehicle_service.utils.Error;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicResponse {

    private Object content;
    private String message;
    private Map<String, String> messageObject;
    private HttpStatus status;
    private MessageType messageType;
    private String redirectUrl;
    private MetaData metadata;

    // Error attributes
    private com.idirtrack.vehicle_service.utils.Error error;
    private List<Error> errorsList;
}