package com.idirtrack.vehicle_service.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceDTO {
    private Long id;
    private Long deviceMicroserviceId;
    private String imei;
    private String type;

    // Build the dto to entity
    public Device toEntity() {
        return Device.builder()
                .id(this.id)
                .deviceMicroserviceId(this.deviceMicroserviceId)
                .imei(this.imei)
                .type(this.type)
                .build();
    }
}
