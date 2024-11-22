package com.idirtrack.vehicle_service.subscription;

import java.sql.Date;

import com.idirtrack.vehicle_service.boitier.Boitier;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date startDate;
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "boitier_id")
    private Boitier boitier;

    public SubscriptionDTO toDTO() {
        return SubscriptionDTO.builder()
                .id(this.id)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .boitierId(this.boitier.getId())
                .build();
    }
}
