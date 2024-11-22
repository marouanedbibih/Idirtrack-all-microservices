// package com.idirtrack.stock_service.device;
// import com.idirtrack.stock_service.deviceType.DeviceType;
// import com.idirtrack.stock_service.stock.Stock;
// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Entity
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "device_stock")
// public class DeviceStock {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne
//     @JoinColumn(name = "device_type_id")
//     private DeviceType deviceType;

//     @OneToOne
//     @JoinColumn(name = "stock_id")
//     private Stock stock;
// }
