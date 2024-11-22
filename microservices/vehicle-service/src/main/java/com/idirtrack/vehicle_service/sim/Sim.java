package com.idirtrack.vehicle_service.sim;

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
@Table(name = "sims")
public class Sim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long simMicroserviceId;
    private String phone;
    private String ccid;
    private String operatorName;

    @OneToOne(mappedBy = "sim")
    private Boitier boitier;

    // Build the entity to dto
    public SimDTO toDTO() {
        return SimDTO.builder()
                .id(this.id)
                .simMicroserviceId(this.simMicroserviceId)
                .phone(this.phone)
                .operatorName(this.operatorName)
                .ccid(this.ccid)
                .build();
    }

    
}
