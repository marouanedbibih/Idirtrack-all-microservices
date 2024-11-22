// package org.idirtrack.backend.modules.sim;

// import com.idirtrack.stock_service.operator.Operator;
// import com.idirtrack.stock_service.stock.Stock;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Entity
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "sim_stock")
// public class SimStock {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne
//     @JoinColumn(name = "operator_id")
//     private Operator operator;

//     @OneToOne
//     @JoinColumn(name = "stock_id")
//     private Stock stock;
// }
