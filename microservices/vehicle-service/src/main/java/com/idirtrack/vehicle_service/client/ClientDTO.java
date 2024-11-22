package com.idirtrack.vehicle_service.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private Long clientMicroserviceId;
    private String name;
    private String company;

    // Build dto to entity
    public Client toEntity() {
        return Client.builder()
                .id(this.id)
                .clientMicroserviceId(this.clientMicroserviceId)
                .name(this.name)
                .company(this.company)
                .build();
    }
}
