// package com.idirtrack.stock_service.stock;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class StockService {

//     @Autowired
//     private StockRepository stockRepository;

//     // Get all stocks
//     public List<Stock> getAllStocks() {
//         return stockRepository.findAll();
//     }

//     // Save stock
//     public Stock saveStock(Stock stock) {
//         return stockRepository.save(stock);
//     }

//     // Find stocks by entry date
//     public List<Stock> findByDateEntree(java.sql.Date dateEntree) {
//         return stockRepository.findByDateEntree(dateEntree);
//     }
// }


// // public class StockService {

// //     private final StockRepository stockRepository;

// //     // Save stock
// //     public Stock saveStock(Stock stock) {
// //         return stockRepository.save(stock);
// //     }

// //     // Find stock by date
// //     public List<Stock> findByDateEntree(Date dateEntree) {
// //         return stockRepository.findByDateEntree(dateEntree);
// //     }

// //     // Get all stocks
// //     public List<Stock> getAllStocks() {
// //         return stockRepository.findAll();
// //     }
// // }


