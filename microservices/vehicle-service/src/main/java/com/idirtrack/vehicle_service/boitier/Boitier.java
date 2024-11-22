package com.idirtrack.vehicle_service.boitier;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.idirtrack.vehicle_service.boitier.dto.BoitierDTO;
import com.idirtrack.vehicle_service.device.Device;
import com.idirtrack.vehicle_service.sim.Sim;
import com.idirtrack.vehicle_service.subscription.Subscription;
import com.idirtrack.vehicle_service.vehicle.Vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boitiers")
public class Boitier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Vehicle vehicle;

    @OneToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @OneToOne
    @JoinColumn(name = "sim_id")
    private Sim sim;

    @OneToMany(mappedBy = "boitier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Subscription> subscriptions;

    public BoitierDTO toDTO() {
        return BoitierDTO.builder()
                .id(this.id)
                .device(this.device.toDTO())
                .sim(this.sim.toDTO())
                .vehicle(this.vehicle.toDTO())
                .subscriptionsList(this.subscriptions.stream().map(Subscription::toDTO).collect(Collectors.toList()))
                .build();
    }

    public static List<BoitierDTO> transformToDTOList(List<Boitier> boitiers) {
        return boitiers.stream()
                .map(Boitier::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boitier boitier = (Boitier) o;
        return Objects.equals(id, boitier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
