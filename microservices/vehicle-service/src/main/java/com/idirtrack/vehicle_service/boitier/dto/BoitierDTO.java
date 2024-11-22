package com.idirtrack.vehicle_service.boitier.dto;

import java.util.List;

import com.idirtrack.vehicle_service.boitier.Boitier;
import com.idirtrack.vehicle_service.device.DeviceDTO;
import com.idirtrack.vehicle_service.sim.SimDTO;
import com.idirtrack.vehicle_service.subscription.SubscriptionDTO;
import com.idirtrack.vehicle_service.vehicle.VehicleDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoitierDTO {
    private Long id;
    private DeviceDTO device;
    private SimDTO sim;
    private SubscriptionDTO subscription;
    private VehicleDTO vehicle;
    private List<SubscriptionDTO> subscriptionsList;

    // Build the entity to dto
    // public Boitier toEntity() {
    //     return Boitier.builder()
    //             .id(this.id)
    //             .device(this.device.toEntity())
    //             .sim(this.sim.toEntity())
    //             .subscription(this.subscription.toEntity())
    //             .vehicle(this.vehicle.toEntity())
    //             .subscriptionsList(this.subscriptionsList)
    //             .build();
    // }
}
