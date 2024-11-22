// package com.idirtrack.stock_service.deviceType;

// import java.util.List;

// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.idirtrack.stock_service.device.Device;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Entity
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "type_device")

// public class DeviceType {

//   @Id
//   @GeneratedValue(strategy = GenerationType.IDENTITY)
//   private Long id;
//   // name of the device type unique
//   private String name;

//   @OneToMany(mappedBy = "deviceType")
//   @JsonBackReference // to avoid infinite loop
//   private List<Device> devices;

// }