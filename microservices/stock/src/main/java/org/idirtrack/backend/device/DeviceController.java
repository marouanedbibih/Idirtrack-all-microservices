// package com.idirtrack.stock_service.device;

// import java.text.ParseException;
// import java.text.SimpleDateFormat;
// import java.sql.Date;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.RestController;

// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;

// import com.idirtrack.stock_service.basics.MessageType;
// import com.idirtrack.stock_service.device.https.DeviceRequest;
// import com.idirtrack.stock_service.device.https.DeviceUpdateRequest;
// import com.idirtrack.stock_service.utils.ValidationUtils;

// import jakarta.validation.Valid;

// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.DeleteMapping;

// @RestController
// @RequestMapping("/stock-api/device")
// public class DeviceController {

//     @Autowired
//     private DeviceService deviceService;

//     /**
//      * FILTER DEVICES API
//      * 
//      * @apiNote
//      *          This endpoint is used to get the list of devices by status, device
//      *          type, created from and created to
//      *          GET
//      *          /stock-api/device/filter/?status=installed&type=1&createdFrom=2021-01-01&createdTo=2021-12-31&page=1&size=5
//      * 
//      * @param status
//      * @param deviceTypeId
//      * @param createdFrom
//      * @param createdTo
//      * @param page
//      * @param size
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */

//     @GetMapping("/filter/")
//     public ResponseEntity<BasicResponse> filterDevices(
//             @RequestParam(value = "status", required = false) String status,
//             @RequestParam(value = "type", required = false) Long deviceTypeId,
//             @RequestParam(value = "createdFrom", required = false) String createdFrom,
//             @RequestParam(value = "createdTo", required = false) String createdTo,
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) throws BasicException {

//         // Try to filter the devices
//         try {
//             BasicResponse response = deviceService.filterDevices(status, deviceTypeId, createdFrom, createdTo, page, size);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         }
//         // Catch any BasicException and return the response
//         catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         }
//         // Catch any exception and return a 500 error
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .metadata(null)
//                     .build());
//         }
//     }

//     /**
//      * Endpoint to get total number of devices and group by status
//      * 
//      * @apiNote
//      *          This endpoint is used to get the total number of devices and group
//      *          by status
//      *          GET /stock-api/device/quantity-of-status/
//      * 
//      * @return ResponseEntity<BasicResponse>
//      */
//     @GetMapping("/quantity-of-status/")
//     public ResponseEntity<BasicResponse> getQuantityOfStatus() {
//         BasicResponse response = deviceService.countDevicesGroupByStatus();
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     /**
//      * Endpoint to get total number of devices
//      * 
//      * @apiNote
//      *          This endpoint is used to get the total number of devices
//      *          GET /stock-api/device/total/
//      * 
//      * @return ResponseEntity<BasicResponse>
//      */
//     @GetMapping("/total/")
//     public ResponseEntity<BasicResponse> getTotalDevices() {
//         BasicResponse response = deviceService.countDevices();
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     /**
//      * Endpoint to create device
//      * 
//      * @apiNote
//      *          This endpoint is used to create a new device
//      *          POST /stock-api/device/
//      * 
//      * @param deviceRequest
//      * @param bindingResult
//      * @return
//      */
//     @PostMapping("/")
//     public ResponseEntity<BasicResponse> createDeviceApi(
//             @RequestBody @Valid DeviceRequest deviceRequest,
//             BindingResult bindingResult) throws BasicException {

//         // Handle validation errors
//         ResponseEntity<BasicResponse> errorResponse = ValidationUtils.handleValidationErrors(bindingResult);
//         if (errorResponse != null) {
//             return errorResponse;
//         }
//         // If there are no validation errors, call the service to create the device
//         else {
//             // Try to create the device
//             try {
//                 BasicResponse response = deviceService.createDevice(deviceRequest);
//                 return ResponseEntity.status(response.getStatus()).body(response);
//             }
//             // Catch any BasicException and return the response
//             catch (BasicException e) {
//                 return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//             }
//             // Catch any exception and return a 500 error
//             catch (Exception e) {
//                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                         .content(null)
//                         .message(e.getMessage())
//                         .messageType(MessageType.ERROR)
//                         .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                         .metadata(null)
//                         .build());
//             }
//         }

//     }

//     /**
//      * Endpoint to update device
//      * 
//      * @apiNote
//      *          This endpoint is used to update the device information
//      *          PUT /stock-api/device/{id}/
//      * @param id
//      * @param deviceUpdateRequest
//      * @param bindingResult
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */
//     @PutMapping("/{id}/")
//     public ResponseEntity<BasicResponse> updateDeviceApi(
//             @PathVariable Long id,
//             @RequestBody @Valid DeviceUpdateRequest deviceUpdateRequest,
//             BindingResult bindingResult) throws BasicException {

//         // Handle validation errors
//         ResponseEntity<BasicResponse> errorResponse = ValidationUtils.handleValidationErrors(bindingResult);
//         if (errorResponse != null) {
//             return errorResponse;
//         }
//         // If there are no validation errors, call the service to update the device
//         else {
//             // Try to update the device
//             try {
//                 BasicResponse response = deviceService.updateDevice(id, deviceUpdateRequest);
//                 return ResponseEntity.status(response.getStatus()).body(response);
//             }
//             // Catch any BasicException and return the response
//             catch (BasicException e) {
//                 return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//             }
//             // Catch any exception and return a 500 error
//             catch (Exception e) {
//                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                         .content(null)
//                         .message(e.getMessage())
//                         .messageType(MessageType.ERROR)
//                         .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                         .metadata(null)
//                         .build());
//             }
//         }

//     }

//     /**
//      * Endpoint to delete device
//      * 
//      * @apiNote
//      *          This endpoint is used to delete the device by id
//      *          DELETE /stock-api/device/{id}/
//      * @param id
//      * @return ResponseEntity<BasicResponse>
//      * @throws BasicException
//      */
//     @DeleteMapping("/{id}/")
//     public ResponseEntity<BasicResponse> deleteDeviceApi(@PathVariable Long id) throws BasicException {

//         // Call the service to delete the device, and return the response
//         BasicResponse response = deviceService.deleteDevice(id);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     /**
//      * Endpoint to get device by id
//      * 
//      * @apiNote
//      *          This endpoint is used to get the device by id
//      *          GET /stock-api/device/{id}/
//      * @param id
//      * @return
//      * @throws BasicException
//      */
//     @GetMapping("/{id}/")
//     public ResponseEntity<BasicResponse> getDeviceApi(@PathVariable Long id) throws BasicException {

//         // Call the service to get the device by ID, and return the response
//         BasicResponse response = deviceService.getDeviceById(id);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     /**
//      * Endpoint to get all devices
//      * 
//      * @apiNote
//      *          This endpoint is used to get all devices with pagination
//      *          GET /stock-api/device/?page=1&size=5
//      * @param page
//      * @param size
//      * @return
//      * @throws BasicException
//      */
//     @GetMapping("/")
//     public ResponseEntity<BasicResponse> getAllBoitiers(
//             @RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) throws BasicException {

//         // Call the service to get all devices, and return the response
//         BasicResponse response = deviceService.getAllDevices(page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);

//     }

//     // Search Devices API
//     @GetMapping("/filter")
//     public ResponseEntity<BasicResponse> filterDevicesApi(@RequestParam(value = "imei", required = false) String imei,
//             @RequestParam(value = "type", required = false) String deviceType,
//             @RequestParam(value = "status", required = false) String status,
//             @RequestParam(value = "createdTo", required = false) String createdTo,
//             @RequestParam(value = "createdFrom", required = false) String createdFrom,
//             @RequestParam(value = "page", defaultValue = "1") int page,
//             @RequestParam(value = "size", defaultValue = "5") int size) {
//         Date dateAt = null;
//         Date dateFrom = null;

//         if (createdTo != null && !createdTo.isEmpty() && createdFrom != null && !createdFrom.isEmpty()) {
//             try {
//                 dateAt = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(createdTo).getTime());
//                 dateFrom = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(createdFrom).getTime());
//             } catch (ParseException e) {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BasicResponse.builder()
//                         .content(null)
//                         .message("Invalid date format")
//                         .messageType(MessageType.ERROR)
//                         .status(HttpStatus.BAD_REQUEST)
//                         .metadata(null)
//                         .build());
//             }
//         }

//         BasicResponse response = deviceService.filterDevices(imei, deviceType, status, dateAt, dateFrom, page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     // Count Devices by Status API
//     @GetMapping("/count-non-install/")
//     public ResponseEntity<BasicResponse> countNonInstallDevicesApi() {
//         BasicResponse response = deviceService.countDevicesNonInstalled();
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     // get list of devices by status non-installed
//     @GetMapping("/not-installed/")

//     public ResponseEntity<BasicResponse> getNonInstalledDevicesApi(@RequestParam(defaultValue = "1") int page,
//             @RequestParam(defaultValue = "5") int size) {
//         BasicResponse response = deviceService.getAllDevicesNonInstalled(page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     // search device non installed by imei
//     @GetMapping("/not-installed/search/")

//     public ResponseEntity<BasicResponse> searchNonInstalledDevicesApi(
//             @RequestParam(value = "imei", required = false) String imei,
//             @RequestParam(value = "page", defaultValue = "0") int page,
//             @RequestParam(value = "size", defaultValue = "5") int size) {
//         BasicResponse response = deviceService.searchNonInstalledDevices(imei, page, size);
//         return ResponseEntity.status(response.getStatus()).body(response);
//     }

//     // Change Device Status to Installed API
//     @PutMapping("/status/installed/{id}/")
//     public ResponseEntity<BasicResponse> changeDeviceStatusToInstalledApi(@PathVariable Long id) {
//         try {
//             BasicResponse response = deviceService.changeDeviceStatusInstalled(id);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         } catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }
//     }

//     /**
//      * Endpoint to chnage the status of device
//      * 
//      * @param id
//      * @param status
//      * @return ResponseEntity<BasicResponse>
//      */

//     @PutMapping("/status/")
//     public ResponseEntity<BasicResponse> changeDeviceStatusApi(@RequestParam Long id, @RequestParam String status) {
//         try {
//             BasicResponse response = deviceService.changeDeviceStatus(id, status);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         } catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }
//     }

//     // Search Device by IMEI API
//     @GetMapping("/search/")
//     public ResponseEntity<BasicResponse> searchDeviceByImeiApi(
//             @RequestParam(value = "search") String search,
//             @RequestParam(value = "page", defaultValue = "1") int page,
//             @RequestParam(value = "size", defaultValue = "5") int size) {
//         // Try to search the device by IMEI
//         try {
//             BasicResponse response = deviceService.searchDevices(search, page, size);
//             return ResponseEntity.status(response.getStatus()).body(response);
//         }
//         // Catch any BasicException and return the response
//         catch (BasicException e) {
//             return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
//         }
//         // Catch any exception and return a 500 error
//         catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
//                     .content(null)
//                     .message(e.getMessage())
//                     .messageType(MessageType.ERROR)
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .build());
//         }
//     }
// }
