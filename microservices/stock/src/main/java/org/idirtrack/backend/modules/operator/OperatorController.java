// package org.idirtrack.backend.modules.operator;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.idirtrack.stock_service.errors.AlreadyExistException;
// import com.idirtrack.stock_service.errors.NotFoundException;
// import com.idirtrack.stock_service.utils.MyResponse;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/stock-api/operators")

// public class OperatorController {

//     @Autowired
//     private OperatorService operatorService;

//     /**
//      * Endpoint API to create a new Operator
//      * 
//      * @param request
//      * @param bindingResult
//      * @return
//      * @throws NotFoundException
//      */
//     @PostMapping("/")
//     public ResponseEntity<MyResponse> createOperator(
//             // Parameters
//             @Valid @RequestBody OperatorRequest request)
//             // Exceptions
//             throws AlreadyExistException {
//         // Call Service
//         MyResponse response = operatorService.createOperator(request);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     @PutMapping("/{id}/")
//     public ResponseEntity<MyResponse> updateSimType(
//             // Parameters
//             @PathVariable Long id,
//             @Valid @RequestBody OperatorRequest request)
//             // Exceptions
//             throws NotFoundException, AlreadyExistException {
//         // Call Service
//         MyResponse response = operatorService.updateOperator(id, request);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     @DeleteMapping("/{id}/")
//     public ResponseEntity<MyResponse> deleteOperator(@PathVariable Long id) throws NotFoundException {
//         MyResponse response = operatorService.deleteOperatorById(id);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     @GetMapping("/all/")
//     public ResponseEntity<MyResponse> getAllOperators() {
//         MyResponse response = operatorService.getAllOperators();
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     @GetMapping("/{id}/")
//     public ResponseEntity<MyResponse> getOperatorById(@PathVariable Long id) throws NotFoundException {
//         MyResponse response = operatorService.getOperatorById(id);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     @GetMapping("/")
//     public ResponseEntity<MyResponse> getOperatorWithPagination(
//             // Parameters
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) {
//         // Call Service
//         MyResponse response = operatorService.getOperatorsWithPagination(page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

// }
