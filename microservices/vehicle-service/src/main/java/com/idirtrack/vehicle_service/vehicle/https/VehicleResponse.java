package com.idirtrack.vehicle_service.vehicle.https;

import java.util.List;

import com.idirtrack.vehicle_service.basic.MetaData;
import com.idirtrack.vehicle_service.boitier.dto.BoitierDTO;
import com.idirtrack.vehicle_service.client.ClientDTO;
import com.idirtrack.vehicle_service.vehicle.VehicleDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {

    private VehicleDTO vehicle;
    private ClientDTO client;
    private List<BoitierDTO> boitiersList;
}
