package com.idirtrack.vehicle_service.boitier.https;

import java.sql.Date;

import org.springframework.data.annotation.AccessType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoitierRequest {
    // Device Informations
    @NotNull(message = "Stock Microservice ID is required")
    private Long deviceMicroserviceId;

    // @NotNull(message = "IMEI is required")
    // @Size(min = 15, max = 15, message = "IMEI must be 15 characters")
    // private String imei;

    // @NotBlank(message = "Type is required")
    // private String deviceType;

    // Card Sim Informations
    @NotNull(message = "Stock Microservice ID is required")
    private Long simMicroserviceId;

    // @NotBlank(message = "Phone number is required")
    // @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    // private String phone;

    // @NotBlank(message = "CCID number is required")
    // @Pattern(regexp = "\\d{18}", message = "CCID number must be 18 digits")
    // private String ccid;

    // @NotBlank(message = "Operator is required")
    // private String operatorName;

    // Subscription Informations
    @NotNull(message = "Start date is required")
    private Date startDate;

    @NotNull(message = "End date is required")
    private Date endDate;
}
