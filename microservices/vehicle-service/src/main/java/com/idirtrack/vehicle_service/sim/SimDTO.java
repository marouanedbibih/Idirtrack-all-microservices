package com.idirtrack.vehicle_service.sim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimDTO {

    private Long id;
    private Long simMicroserviceId;
    private String phone;
    private String ccid;
    private String operatorName;

    // Build the entity to dto
    public Sim toEntity() {
        return Sim.builder()
                .id(this.id)
                .simMicroserviceId(this.simMicroserviceId)
                .phone(this.phone)
                .operatorName(this.operatorName)
                .build();
    }
}