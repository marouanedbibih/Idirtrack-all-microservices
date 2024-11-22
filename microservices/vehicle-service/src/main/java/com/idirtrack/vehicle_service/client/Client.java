package com.idirtrack.vehicle_service.client;

import java.util.List;

import com.idirtrack.vehicle_service.vehicle.Vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clientMicroserviceId;
    private String name;
    private String company;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;

    // Build the entity to dto
    public ClientDTO toDTO() {
        return ClientDTO.builder()
                .id(this.id)
                .clientMicroserviceId(this.clientMicroserviceId)
                .name(this.name)
                .company(this.company)
                .build();
    }
}
