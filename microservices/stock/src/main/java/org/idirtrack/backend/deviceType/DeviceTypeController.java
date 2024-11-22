// package com.idirtrack.stock_service.deviceType;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
// import com.idirtrack.stock_service.basics.BasicError;
// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.basics.MessageType;
// import com.idirtrack.stock_service.deviceType.dtos.DeviceTypeRequest;
// import com.idirtrack.stock_service.utils.ValidationUtils;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/stock-api/device/type")
// public class DeviceTypeController {

//     @Autowired
//     private DeviceTypeService deviceTypeService;

//     /**
//      * Get all device types
//      * 
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */

//     @GetMapping("/all/")
//     public ResponseEntity<BasicResponse> getListOfAllDeviceTypes() {
//         // Try to get all device types
//         try {
//             return ResponseEntity.ok(deviceTypeService.getListOfAllDeviceTypes());
//         }
//         // Catch BasicException
//         catch (BasicException e) {
//             return ResponseEntity
//                     .status(e.getResponse().getStatus())
//                     .body(e.getResponse());
//         }
//         // Catch any other exception
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(BasicResponse.builder().messageType(MessageType.ERROR).message(e.getMessage()).build());
//         }
//     }

//     /**
//      * Get all device types with pagination
//      * 
//      * @param page
//      * @param size
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */

//     @GetMapping("/")
//     public ResponseEntity<BasicResponse> getAllDeviceTypes(
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "10") int size) {
//         // Try to get all device types
//         try {
//             return ResponseEntity.ok(deviceTypeService.getAllDeviceTypes(page, size));
//         }
//         // Catch BasicException
//         catch (BasicException e) {
//             return ResponseEntity
//                     .status(e.getResponse().getStatus())
//                     .body(e.getResponse());
//         }
//         // Catch any other exception
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(BasicResponse.builder().messageType(MessageType.ERROR).message(e.getMessage()).build());
//         }
//     }

//     /**
//      * Endpoint to create a new device type
//      * 
//      * @param DeviceTypeRequest request
//      * @param BindingResult     result
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */

//     @PostMapping("/")
//     public ResponseEntity<BasicResponse> createDeviceType(
//             @Valid @RequestBody DeviceTypeRequest request,
//             BindingResult result) {
//         // Check if there are any validation errors
//         if (result.hasErrors()) {
//             List<BasicError> errors = ValidationUtils.extractErrorsFromBindingResult(result);
//             return ResponseEntity
//                     .status(HttpStatus.BAD_REQUEST)
//                     .body(
//                             BasicResponse.builder()
//                                     .messageType(MessageType.ERROR)
//                                     .errors(errors)
//                                     .build());
//         }
//         // Try to create a new device type
//         try {
//             return ResponseEntity.ok(deviceTypeService.createDeviceType(request));
//         }
//         // Catch BasicException
//         catch (BasicException e) {
//             return ResponseEntity
//                     .status(e.getResponse().getStatus())
//                     .body(e.getResponse());
//         }
//         // Catch any other exception
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(BasicResponse.builder().messageType(MessageType.ERROR).message(e.getMessage()).build());
//         }
//     }

//     /**
//      * Get a device type by id
//      * 
//      * @param id
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */

//     @GetMapping("/{id}/")
//     public ResponseEntity<BasicResponse> getDeviceTypeById(@PathVariable Long id) {
//         // Try to get a device type by id
//         try {
//             return ResponseEntity.ok(deviceTypeService.getDeviceTypeById(id));
//         }
//         // Catch BasicException
//         catch (BasicException e) {
//             return ResponseEntity
//                     .status(e.getResponse().getStatus())
//                     .body(e.getResponse());
//         }
//         // Catch any other exception
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(BasicResponse.builder().messageType(MessageType.ERROR).message(e.getMessage()).build());
//         }
//     }

//     /**
//      * Update a device type by id
//      * 
//      * @param id      Long
//      * @param request DeviceTypeRequest
//      * @param result  BindingResult
//      * @return ResponseEntity<BasicResponse>
//      */

//     @PutMapping("/{id}/")
//     public ResponseEntity<BasicResponse> updateDeviceTypeById(
//             @PathVariable Long id,
//             @Valid @RequestBody DeviceTypeRequest request,
//             BindingResult result) {
//         // Check if there are any validation errors
//         if (result.hasErrors()) {
//             List<BasicError> errors = ValidationUtils.extractErrorsFromBindingResult(result);
//             return ResponseEntity
//                     .status(HttpStatus.BAD_REQUEST)
//                     .body(
//                             BasicResponse.builder()
//                                     .messageType(MessageType.ERROR)
//                                     .errors(errors)
//                                     .build());
//         }
//         // Try to update a device type by id
//         try {
//             return ResponseEntity.ok(deviceTypeService.updateDeviceType(id, request));
//         }
//         // Catch BasicException
//         catch (BasicException e) {
//             return ResponseEntity
//                     .status(e.getResponse().getStatus())
//                     .body(e.getResponse());
//         }
//         // Catch any other exception
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(BasicResponse.builder().messageType(MessageType.ERROR).message(e.getMessage()).build());
//         }
//     }

//     /**
//      * Endpoint to delete a device type by id
//      * 
//      * @param id
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */

//     @DeleteMapping("/{id}/")
//     public ResponseEntity<BasicResponse> deleteDeviceTypeById(@PathVariable Long id) {
//         // Try to delete a device type by id
//         try {
//             return ResponseEntity.ok(deviceTypeService.deleteDeviceType(id));
//         }
//         // Catch BasicException
//         catch (BasicException e) {
//             return ResponseEntity
//                     .status(e.getResponse().getStatus())
//                     .body(e.getResponse());
//         }
//         // Catch any other exception
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(BasicResponse.builder().messageType(MessageType.ERROR).message(e.getMessage()).build());
//         }
//     }

// }
