// package com.idirtrack.stock_service.device;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.sql.Date;

// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.idirtrack.stock_service.deviceType.DeviceType;

// @Entity
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "device")
// public class Device {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     // must be unique
//     @Column(name = "imei", unique = true)
//     private String imei;

//     @Column(name = "created_at")
//     private Date createdAt;

//     @Column(name = "updated_at")
//     private Date updatedAt;

//     @Enumerated(EnumType.STRING)
//     private DeviceStatus status;

//     @Column(name = "remarque")
//     private String remarque;

//     @ManyToOne
//     @JoinColumn(name = "type_device_id")
//     @JsonBackReference // to avoid infinite loop
//     private DeviceType deviceType;

//     public void setDeviceType(DeviceType deviceType) {
//         this.deviceType = deviceType;
//     }

//     public DeviceType getDeviceType() {
//         return deviceType;
//     }
// }
