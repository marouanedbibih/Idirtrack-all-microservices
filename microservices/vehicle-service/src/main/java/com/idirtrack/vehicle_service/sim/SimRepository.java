package com.idirtrack.vehicle_service.sim;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SimRepository extends JpaRepository<Sim, Long>{
    
    Boolean existsBySimMicroserviceId(Long simMicroserviceId);
    
}
