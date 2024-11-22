package com.idirtrack.vehicle_service.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private Long id;
    private String matricule;
    private String type;

    // Build dto to entity
    public Vehicle toEntity() {
        return Vehicle.builder()
                .id(this.id)
                .matricule(this.matricule)
                .type(this.type)
                .build();
    }
}
