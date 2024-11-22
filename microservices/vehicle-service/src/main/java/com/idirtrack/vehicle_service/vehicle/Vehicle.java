package com.idirtrack.vehicle_service.vehicle;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.idirtrack.vehicle_service.boitier.Boitier;
import com.idirtrack.vehicle_service.client.Client;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String matricule;
    private String type;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<Boitier> boitiers;

    // Build the entity to dto
    public VehicleDTO toDTO() {
        return VehicleDTO.builder()
                .id(this.id)
                .matricule(this.matricule)
                .type(this.type)
                .build();
    }

    // Transform the List of entities to List of dtos
    public static List<VehicleDTO> toDTOList(List<Vehicle> vehicles) {
        return vehicles.stream().map(Vehicle::toDTO).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
