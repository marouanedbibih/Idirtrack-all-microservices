package com.idirtrack.vehicle_service.device;


import com.idirtrack.vehicle_service.boitier.Boitier;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imei;
    private String type;
    private Long deviceMicroserviceId;

    @OneToOne(mappedBy = "device")
    private Boitier boitier;

    // Build the entity to dto
    public DeviceDTO toDTO() {
        return DeviceDTO.builder()
                .id(this.id)
                .deviceMicroserviceId(this.deviceMicroserviceId)
                .imei(this.imei)
                .type(this.type)
                .build();
    }


}
