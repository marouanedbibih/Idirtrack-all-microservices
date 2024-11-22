// package com.idirtrack.stock_service.device.https;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import jakarta.validation.constraints.Pattern;
// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Builder
// public class DeviceRequest {

//     @NotBlank(message = "The IMEI is required")
//     @Pattern(regexp = "\\d{15}", message = "The IMEI must be 15 digits")
//     private String imei;

//     @NotNull(message = "The Type is required")
//     private Long deviceTypeId;
    
//     @Size(max = 100, message = "The remarque must be less than 100 characters")
//     @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "The remarque must contain only alphanumeric characters and spaces")
//     private String remarque;    
    
// }
