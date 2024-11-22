package com.idirtrack.vehicle_service.subscription;


import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {

    private Long id;
    private Date startDate;
    private Date endDate;
    private Long boitierId;

    public Subscription toEntity() {
        return Subscription.builder()
                .id(id)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}