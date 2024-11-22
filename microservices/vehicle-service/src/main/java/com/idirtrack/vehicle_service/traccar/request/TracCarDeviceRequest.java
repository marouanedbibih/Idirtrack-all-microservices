package com.idirtrack.vehicle_service.traccar.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TracCarDeviceRequest {
    private String name;
    private String uniqueId;
}
