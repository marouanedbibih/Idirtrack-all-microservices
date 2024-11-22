package com.idirtrack.vehicle_service.vehicle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>{

    boolean existsByMatricule(String matricule);

    // Page<Vehicle> findAll(Pageable pageable);
    
    
}
