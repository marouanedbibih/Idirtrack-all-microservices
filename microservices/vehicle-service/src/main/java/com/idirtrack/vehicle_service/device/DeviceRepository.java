package com.idirtrack.vehicle_service.device;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long>{

    Boolean existsByDeviceMicroserviceId(Long deviceMicroserviceId);
    
}
