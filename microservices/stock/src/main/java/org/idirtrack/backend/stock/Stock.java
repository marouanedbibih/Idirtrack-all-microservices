// package com.idirtrack.stock_service.stock;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.sql.Date;

// import com.idirtrack.stock_service.device.DeviceStock;
// import com.idirtrack.stock_service.sim.SimStock;

// import jakarta.persistence.*;

// @Entity
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "stock")
// public class Stock {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     private Integer quantity;

//     @Temporal(TemporalType.DATE)
//     @Column(name = "date_entree")
//     private Date dateEntree;

//     @OneToOne(mappedBy = "stock")
//     private DeviceStock deviceStock;

//     @OneToOne(mappedBy = "stock")
//     private SimStock simStock;
// }
