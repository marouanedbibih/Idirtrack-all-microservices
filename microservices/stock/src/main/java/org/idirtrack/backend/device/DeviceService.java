// package com.idirtrack.stock_service.device;

// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.basics.BasicValidation;
// import com.idirtrack.stock_service.basics.BasicError;
// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.MessageType;
// import com.idirtrack.stock_service.basics.MetaData;
// import com.idirtrack.stock_service.device.https.DeviceRequest;
// import com.idirtrack.stock_service.device.https.DeviceUpdateRequest;
// import com.idirtrack.stock_service.deviceType.DeviceType;
// import com.idirtrack.stock_service.deviceType.DeviceTypeRepository;
// import com.idirtrack.stock_service.deviceType.DeviceTypeService;
// import com.idirtrack.stock_service.stock.Stock;
// import com.idirtrack.stock_service.stock.StockRepository;

// import jakarta.persistence.criteria.Predicate;
// import jakarta.validation.Valid;
// import org.springframework.stereotype.Service;
// import org.springframework.validation.BindingResult;
// import lombok.RequiredArgsConstructor;

// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;

// import java.util.ArrayList;
// import java.sql.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.stream.Collectors;
// import org.springframework.data.domain.Page;
// import java.util.List;
// import org.springframework.data.jpa.domain.Specification;

// @Service
// @RequiredArgsConstructor
// public class DeviceService {

//         private final DeviceRepository deviceRepository;
//         private final DeviceTypeRepository deviceTypeRepository;
//         private final DeviceStockRepository deviceStockRepository;
//         private final StockRepository stockRepository;
//         private final DeviceTypeService deviceTypeService;

//         /**
//          * Count devices and group by status
//          * 
//          * @return BasicResponse
//          * @throws BasicException
//          */

//         public BasicResponse countDevicesGroupByStatus() {
//                 long nonInstalled = deviceRepository.countByStatus(DeviceStatus.NON_INSTALLED);
//                 long installed = deviceRepository.countByStatus(DeviceStatus.INSTALLED);
//                 long inLost = deviceRepository.countByStatus(DeviceStatus.LOST);
//                 long inPending = deviceRepository.countByStatus(DeviceStatus.PENDING);

//                 Map<String, Long> countMap = new HashMap<>();
//                 countMap.put("nonInstalled", nonInstalled);
//                 countMap.put("installed", installed);
//                 countMap.put("lost", inLost);
//                 countMap.put("pending", inPending);

//                 return BasicResponse.builder()
//                                 .content(countMap)
//                                 .status(HttpStatus.OK)
//                                 .metadata(null)
//                                 .build();
//         }

//         /**
//          * Count all devices
//          * 
//          * @return
//          * @throws BasicException
//          */
//         public BasicResponse countDevices() {
//                 long count = deviceRepository.count();
//                 return BasicResponse.builder()
//                                 .content(count)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Create a device
//          * 
//          * @param deviceRequest
//          * @return
//          * @throws BasicException
//          */
//         public BasicResponse createDevice(DeviceRequest request) throws BasicException {

//                 // Create a list of errors
//                 List<BasicError> errors = new ArrayList<>();
//                 // Check if the device already exists
//                 this.ifAlreadyExists(request.getImei());

//                 // Check if device type exists
//                 DeviceType deviceType = deviceTypeRepository.findById(request.getDeviceTypeId())
//                                 .orElseThrow(() -> {
//                                         errors.add(BasicError.builder()
//                                                         .key("deviceTypeId")
//                                                         .message("Device type not found")
//                                                         .build());
//                                         return new BasicException(BasicResponse.builder()
//                                                         .errors(errors)
//                                                         .status(HttpStatus.BAD_REQUEST)
//                                                         .messageType(MessageType.ERROR)
//                                                         .build());
//                                 });

//                 // Transform the request to entity
//                 Device device = Device.builder()
//                                 .imei(request.getImei())
//                                 .createdAt(new Date(System.currentTimeMillis()))
//                                 .status(DeviceStatus.NON_INSTALLED)
//                                 .deviceType(deviceType)
//                                 .remarque(request.getRemarque())
//                                 .build();

//                 // Save the device entity
//                 device = deviceRepository.save(device);

//                 // Check if device stock exists and update it (add quantity)
//                 // Check with device type and date
//                 this.updateDeviceStock(device);

//                 // Return a success response
//                 return BasicResponse.builder()
//                                 .content(device)
//                                 .message("Device created successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.CREATED)
//                                 .build();

//         }

//         // Update device
//         public BasicResponse updateDevice(Long id, DeviceUpdateRequest request) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();
//                 // Check if the device exists
//                 Device existingDevice = deviceRepository.findById(id)
//                                 .orElseThrow(() -> new BasicException(BasicResponse.builder()
//                                                 .message("Device not found")
//                                                 .messageType(MessageType.ERROR)
//                                                 .status(HttpStatus.NOT_FOUND)
//                                                 .build()));

//                 // Check if the imei is different from the current one
//                 if (!existingDevice.getImei().equals(request.getImei())) {
//                         // Check if the new imei already exists in another device
//                         if (deviceRepository.existsByImei(request.getImei())) {
//                                 errors.add(BasicError.builder()
//                                                 .key("imei")
//                                                 .message("IMEI already exists")
//                                                 .build());
//                                 throw new BasicException(BasicResponse.builder()
//                                                 .errors(errors)
//                                                 .status(HttpStatus.BAD_REQUEST)
//                                                 .messageType(MessageType.ERROR)
//                                                 .build());
//                         }
//                 }

//                 // Find the device type
//                 DeviceType deviceType = deviceTypeRepository.findById(request.getDeviceTypeId())
//                                 .orElseThrow(() -> {
//                                         errors.add(BasicError.builder()
//                                                         .key("deviceTypeId")
//                                                         .message("Device type not found")
//                                                         .build());
//                                         return new BasicException(BasicResponse.builder()
//                                                         .errors(errors)
//                                                         .status(HttpStatus.BAD_REQUEST)
//                                                         .messageType(MessageType.ERROR)
//                                                         .build());
//                                 });

//                 // Set new values
//                 existingDevice.setImei(request.getImei());
//                 existingDevice.setDeviceType(deviceType);
//                 existingDevice.setRemarque(request.getRemarque());
//                 existingDevice.setUpdatedAt(new Date(System.currentTimeMillis()));

//                 // Save the device
//                 deviceRepository.save(existingDevice);

//                 // Build the DTO
//                 DeviceDTO deviceDTO = DeviceDTO.builder()
//                                 .id(existingDevice.getId())
//                                 .IMEI(existingDevice.getImei())
//                                 .deviceType(existingDevice.getDeviceType().getName())
//                                 .remarque(existingDevice.getRemarque())
//                                 .status(existingDevice.getStatus())
//                                 .createAt(existingDevice.getCreatedAt())
//                                 .updateAt(existingDevice.getUpdatedAt())
//                                 .build();

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .content(deviceDTO)
//                                 .message("Device updated successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Delete device by id
//          * 
//          * This function deletes a device by its id,
//          * If not found, it throws an exception.
//          * Then update the device stock on delete.
//          * And save the IMEI before deleting the device.
//          * After that, it deletes the device.
//          * Returns a response with a message.
//          * 
//          * @param id
//          * @return
//          * @throws BasicException
//          */
//         public BasicResponse deleteDevice(Long id) throws BasicException {
//                 // Find the device
//                 Device device = deviceRepository.findById(id).orElseThrow(
//                                 () -> new BasicException(BasicResponse.builder()
//                                                 .message("Device not found with id: " + id)
//                                                 .messageType(MessageType.ERROR)
//                                                 .status(HttpStatus.NOT_FOUND)
//                                                 .build()));

//                 // Update device stock on delete
//                 this.updateDeviceStockOnDelete(device);
//                 // Save the IMEI
//                 String imei = device.getImei();

//                 // Delete the device
//                 deviceRepository.delete(device);

//                 return BasicResponse.builder()
//                                 .message("Device deleted successfully with IME: " + imei)
//                                 .messageType(MessageType.WARNING)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Get device by id
//          * 
//          * This function finds a device by its id,
//          * if not found, it throws an exception.
//          * Then it builds a DTO and returns it.
//          * 
//          * @param id
//          * @return
//          * @throws BasicException
//          */
//         public BasicResponse getDeviceById(Long id) throws BasicException {
//                 // Find the device
//                 Device device = deviceRepository.findById(id).orElseThrow(
//                                 () -> new BasicException(BasicResponse.builder()
//                                                 .message("Device not found with id: " + id)
//                                                 .messageType(MessageType.ERROR)
//                                                 .status(HttpStatus.NOT_FOUND)
//                                                 .build()));

//                 // Build the dto
//                 DeviceDTO deviceDTO = DeviceDTO.builder()
//                                 .id(device.getId())
//                                 .IMEI(device.getImei())
//                                 .deviceType(device.getDeviceType().getName())
//                                 .deviceTypeId(device.getDeviceType().getId())
//                                 .remarque(device.getRemarque())
//                                 .status(device.getStatus())
//                                 .createAt(device.getCreatedAt())
//                                 .build();

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .content(deviceDTO)
//                                 .messageType(MessageType.INFO)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * Get all devices with pagination
//          * 
//          * This function retrieves all devices from the database and returns them in
//          * 
//          * @param page
//          * @param size
//          * @return
//          */
//         public BasicResponse getAllDevices(int page, int size) throws BasicException {
//                 // Create pagination
//                 Pageable pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());

//                 // Retrieve all devices from the database
//                 Page<Device> devicePage = deviceRepository.findAll(pageRequest);

//                 // if device not found
//                 if (devicePage.isEmpty()) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .message("No devices found")
//                                         .messageType(MessageType.WARNING)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build());

//                 }
//                 // Create a list of DTOs for devices
//                 List<DeviceDTO> deviceDTOs = devicePage.getContent().stream()
//                                 .map(device -> DeviceDTO.builder()
//                                                 .id(device.getId())
//                                                 .IMEI(device.getImei())
//                                                 .deviceType(device.getDeviceType().getName())
//                                                 .createAt(device.getCreatedAt())
//                                                 .status(device.getStatus())
//                                                 .remarque(device.getRemarque())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 // Create metadata
//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(devicePage.getNumber() + 1)
//                                 .totalPages(devicePage.getTotalPages())
//                                 .size(devicePage.getSize())
//                                 .build();

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .content(deviceDTOs)
//                                 .metadata(metaData)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         // Transform DTO to entity
//         public Device transformResponseDTO(DeviceDTO deviceDTO) {
//                 DeviceType deviceType = deviceTypeRepository.findByName(deviceDTO.getDeviceType());
//                 return Device.builder()
//                                 .imei(deviceDTO.getIMEI())
//                                 .createdAt(new Date(System.currentTimeMillis()))
//                                 .status(DeviceStatus.NON_INSTALLED)
//                                 .deviceType(deviceType)
//                                 .remarque(deviceDTO.getRemarque())
//                                 .build();
//         }

//         // Transform requestUpdate to DTO
//         public DeviceDTO transformRequestUpdateDTO(DeviceUpdateRequest deviceUpdateRequest) {
//                 return DeviceDTO.builder()
//                                 .IMEI(deviceUpdateRequest.getImei())
//                                 // .deviceType(deviceUpdateRequest.getTypeDevice())
//                                 .remarque(deviceUpdateRequest.getRemarque())
//                                 // .status(DeviceStatus.valueOf(deviceUpdateRequest.getStatus()))
//                                 .build();
//         }

//         // Update device stock
//         public void updateDeviceStock(Device device) {
//                 List<Stock> stocks = stockRepository.findByDateEntree(device.getCreatedAt());
//                 Stock stock = null;
//                 DeviceStock deviceStock = null;

//                 for (Stock s : stocks) {
//                         deviceStock = deviceStockRepository.findByStockAndDeviceType(s, device.getDeviceType());
//                         if (deviceStock != null) {
//                                 stock = s;
//                                 break;
//                         }
//                 }

//                 if (stock == null) {
//                         stock = Stock.builder()
//                                         .dateEntree(device.getCreatedAt())
//                                         .quantity(1)
//                                         .build();
//                         stock = stockRepository.save(stock);

//                         deviceStock = DeviceStock.builder()
//                                         .deviceType(device.getDeviceType())
//                                         .stock(stock)
//                                         .build();
//                         deviceStockRepository.save(deviceStock);
//                 } else {
//                         stock.setQuantity(stock.getQuantity() + 1);
//                         stockRepository.save(stock);
//                 }
//         }

//         // Update device stock on delete
//         private void updateDeviceStockOnDelete(Device device) {
//                 List<Stock> stocks = stockRepository.findByDateEntree(device.getCreatedAt());
//                 Stock stock = null;
//                 DeviceStock deviceStock = null;

//                 for (Stock s : stocks) {
//                         deviceStock = deviceStockRepository.findByStockAndDeviceType(s, device.getDeviceType());
//                         if (deviceStock != null) {
//                                 stock = s;
//                                 break;
//                         }
//                 }

//                 if (stock != null) {
//                         stock.setQuantity(stock.getQuantity() - 1);
//                         stockRepository.save(stock);

//                         if (stock.getQuantity() <= 0) {
//                                 deviceStockRepository.delete(deviceStock);
//                                 stockRepository.delete(stock);
//                         }
//                 }
//         }

//         // Transform request to DTO
//         public DeviceDTO transformRequestDTO(DeviceRequest deviceRequest) {
//                 return DeviceDTO.builder()
//                                 .IMEI(deviceRequest.getImei())
//                                 .deviceType(deviceTypeRepository.findById(deviceRequest.getDeviceTypeId()).get()
//                                                 .getName())
//                                 .remarque(deviceRequest.getRemarque())
//                                 .build();
//         }

//         // Check if device with the given IMEI already exists
//         private void ifAlreadyExists(String imei) throws BasicException {
//                 if (deviceRepository.existsByImei(imei)) {
//                         List<BasicError> errors = new ArrayList<>();
//                         errors.add(BasicError.builder()
//                                         .key("imei")
//                                         .message("IMEI already exists")
//                                         .build());
//                         throw new BasicException(BasicResponse.builder()
//                                         .errors(errors)
//                                         .status(HttpStatus.BAD_REQUEST)
//                                         .build());
//                 }
//         }

//         // filtr devices with pagination
//         public BasicResponse filterDevices(String imei, String deviceType, String status, Date createdTo,
//                         Date createdFrom,
//                         int page, int size) {
//                 Pageable pageable = PageRequest.of(page - 1, size);

//                 Specification<Device> specification = (root, query, criteriaBuilder) -> {
//                         List<Predicate> predicates = new ArrayList<>();

//                         if (imei != null && !imei.isEmpty()) {
//                                 predicates.add(criteriaBuilder.equal(root.get("imei"), imei));
//                         }

//                         if (deviceType != null && !deviceType.isEmpty()) {
//                                 predicates.add(criteriaBuilder.equal(root.get("deviceType").get("name"), deviceType));
//                         }

//                         if (status != null && !status.isEmpty()) {
//                                 predicates.add(criteriaBuilder.equal(root.get("status"), DeviceStatus.valueOf(status)));
//                         }
//                         // Filter by created date between createdFrom and createdTo
//                         if (createdFrom != null && createdTo != null) {
//                                 predicates.add(criteriaBuilder.between(root.get("createdAt"), createdTo, createdFrom));
//                         }
//                         // filter by created date createdFrom null take current date
//                         if (createdFrom == null && createdTo != null) {
//                                 predicates.add(criteriaBuilder.between(root.get("createdAt"), createdTo,
//                                                 new Date(System.currentTimeMillis())));
//                         }

//                         return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//                 };

//                 Page<Device> devicePage = deviceRepository.findAll(specification, pageable);
//                 if (devicePage.isEmpty()) {
//                         return BasicResponse.builder()
//                                         .content(null)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .message("No devices found with the provided filter criteria.")
//                                         .messageType(MessageType.ERROR)
//                                         .metadata(null)
//                                         .build();
//                 }

//                 List<DeviceDTO> deviceDTOs = devicePage.getContent().stream()
//                                 .map(device -> DeviceDTO.builder()
//                                                 .id(device.getId())
//                                                 .IMEI(device.getImei())
//                                                 .deviceType(device.getDeviceType().getName())
//                                                 .createAt(device.getCreatedAt())
//                                                 .updateAt(device.getUpdatedAt())
//                                                 .deviceTypeId(device.getDeviceType().getId())
//                                                 .remarque(device.getRemarque())
//                                                 .status(device.getStatus())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(devicePage.getNumber() + 1)
//                                 .totalPages(devicePage.getTotalPages())
//                                 .size(devicePage.getSize())
//                                 .build();

//                 Map<String, Object> data = new HashMap<>();
//                 data.put("devices", deviceDTOs);

//                 return BasicResponse.builder()
//                                 .content(data)
//                                 .status(HttpStatus.OK)
//                                 .message("Devices retrieved successfully")
//                                 .metadata(metaData)
//                                 .build();
//         }

//         // Count all devices have status non installed
//         public BasicResponse countDevicesNonInstalled() {
//                 long count = deviceRepository.countByStatus(DeviceStatus.NON_INSTALLED);
//                 return BasicResponse.builder()
//                                 .content(count)
//                                 .status(HttpStatus.OK)
//                                 .message("Devices count retrieved successfully")
//                                 .metadata(null)
//                                 .build();
//         }
//         // get all device non installed by pagination

//         // Get all device non installed by pagination
//         public BasicResponse getAllDevicesNonInstalled(int page, int size) {
//                 // Create pagination
//                 Pageable pageRequest = PageRequest.of(page - 1, size);

//                 // Retrieve all devices from the database
//                 Page<Device> devicePage = deviceRepository.findAllByStatus(DeviceStatus.NON_INSTALLED, pageRequest);

//                 // Create a list of DTOs for devices
//                 List<DeviceBoitierDTO> deviceDTOs = devicePage.getContent().stream()
//                                 .map(device -> DeviceBoitierDTO.builder()
//                                                 .deviceMicroserviceId(device.getId())
//                                                 .imei(device.getImei())
//                                                 .type(device.getDeviceType().getName())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(devicePage.getNumber() + 1)
//                                 .totalPages(devicePage.getTotalPages())
//                                 .size(devicePage.getSize())
//                                 .build();

//                 // if device not found
//                 if (devicePage.isEmpty()) {
//                         return BasicResponse.builder()
//                                         .content(null)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .message("No devices found")
//                                         .messageType(MessageType.ERROR)
//                                         .metadata(null)
//                                         .build();
//                 }
//                 return BasicResponse.builder()
//                                 .content(deviceDTOs)
//                                 .metadata(metaData)
//                                 .content(deviceDTOs)
//                                 .status(HttpStatus.OK)
//                                 .message("Devices retrieved successfully")
//                                 .metadata(metaData)
//                                 .build();
//         }

//         // search device non installed by imei

//         public BasicResponse searchNonInstalledDevices(String imei, int page, int size) {
//                 Pageable pageable = PageRequest.of(page - 1, size);
//                 Page<Device> devicePage = deviceRepository.findAllByStatusAndImeiContaining(DeviceStatus.NON_INSTALLED,
//                                 imei,
//                                 pageable);

//                 if (devicePage.isEmpty()) {
//                         return BasicResponse.builder()
//                                         .content(null)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .message("No non-installed devices found")
//                                         .messageType(MessageType.ERROR)
//                                         .metadata(null)
//                                         .build();
//                 }

//                 List<DeviceBoitierDTO> deviceDTOs = devicePage.getContent().stream()
//                                 .map(device -> DeviceBoitierDTO.builder()
//                                                 .deviceMicroserviceId(device.getId())
//                                                 .imei(device.getImei())
//                                                 .type(device.getDeviceType().getName())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(devicePage.getNumber() + 1)
//                                 .totalPages(devicePage.getTotalPages())
//                                 .size(devicePage.getSize())
//                                 .build();

//                 return BasicResponse.builder()
//                                 .content(deviceDTOs)
//                                 // .content(deviceDTOs)

//                                 .status(HttpStatus.OK)
//                                 .message("Non-installed devices retrieved successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .metadata(metaData)
//                                 .build();
//         }

//         /**
//          * SEARCH DEVICES BY ANY FIELD
//          * 
//          * This function searches for devices by any field (IMEI, device type, status)
//          * 
//          * @param search
//          * @param page
//          * @param size
//          * @return BasicResponse
//          * @throws BasicException
//          */
//         public BasicResponse searchDevices(String search, int page, int size) throws BasicException {
//                 // Create pagination
//                 Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

//                 // Search devices by any field
//                 Page<Device> devicePage = deviceRepository.search(search, pageable);

//                 // If no devices found throw an exception
//                 if (devicePage.isEmpty()) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .message("No devices found")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build());
//                 }

//                 // Create a list of DTOs for devices
//                 List<DeviceDTO> deviceDTOs = devicePage.getContent().stream()
//                                 .map(device -> DeviceDTO.builder()
//                                                 .id(device.getId())
//                                                 .IMEI(device.getImei())
//                                                 .deviceType(device.getDeviceType().getName())
//                                                 .createAt(device.getCreatedAt())
//                                                 .deviceTypeId(device.getDeviceType().getId())
//                                                 .remarque(device.getRemarque())
//                                                 .status(device.getStatus())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 // Create metadata
//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(devicePage.getNumber() + 1)
//                                 .totalPages(devicePage.getTotalPages())
//                                 .size(devicePage.getSize())
//                                 .build();
//                 // Return the response
//                 return BasicResponse.builder()
//                                 .content(deviceDTOs)
//                                 .status(HttpStatus.OK)
//                                 .metadata(metaData)
//                                 .build();
//         }

//         // Change device status to installed
//         public BasicResponse changeDeviceStatusInstalled(Long id) throws BasicException {
//                 Device device = deviceRepository.findById(id).orElse(null);
//                 if (device == null) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .content(null)
//                                         .message("Device not found")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build());
//                 }

//                 device.setStatus(DeviceStatus.INSTALLED);
//                 device = deviceRepository.save(device);

//                 return BasicResponse.builder()
//                                 .content(device)
//                                 .message("Device status changed to installed successfully")
//                                 .messageType(MessageType.SUCCESS)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         public BasicResponse changeDeviceStatus(Long id, String status) throws BasicException {
//                 // Find the device
//                 Device device = deviceRepository.findById(id).orElseThrow(
//                                 () -> new BasicException(BasicResponse.builder()
//                                                 .content(null)
//                                                 .message("Device not found")
//                                                 .messageType(MessageType.ERROR)
//                                                 .status(HttpStatus.NOT_FOUND)
//                                                 .build()));

//                 // Check if the status is valid by checking the enum
//                 try {
//                         DeviceStatus deviceStatus = DeviceStatus.valueOf(status.toUpperCase());
//                         device.setStatus(deviceStatus);
//                         device = deviceRepository.save(device);
//                         return BasicResponse.builder()
//                                         .message("Device status changed successfully")
//                                         .messageType(MessageType.SUCCESS)
//                                         .status(HttpStatus.OK)
//                                         .build();
//                 } catch (IllegalArgumentException e) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .content(null)
//                                         .message("Invalid status")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.BAD_REQUEST)
//                                         .build());
//                 }
//         }

//         /**
//          * FILTER DEVICES
//          */
//         public BasicResponse filterDevices(String status, Long deviceTypeId, String createdFrom, String createdTo,
//                         int page, int size) throws BasicException {
//                 // Create list of errors
//                 List<BasicError> errors = new ArrayList<>();

//                 // Create pagination
//                 Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

//                 // Convert status and dates
                
//                 DeviceStatus deviceStatus = status != null ? DeviceStatus.valueOf(status.toUpperCase()) : null;
//                 DeviceType deviceType = deviceTypeId != null ? deviceTypeRepository.findById(deviceTypeId).orElse(null)
//                                 : null;
//                 Date createdFromDate = createdFrom != null && !createdFrom.isEmpty() ? Date.valueOf(createdFrom) : null;
//                 Date createdToDate = createdTo != null && !createdTo.isEmpty() ? Date.valueOf(createdTo) : null;

//                 // Filter devices by status, device type, created date
//                 Page<Device> devicePage = deviceRepository.filterDevices(deviceStatus, deviceType, createdFromDate,
//                                 createdToDate,
//                                 pageable);

//                 // If no devices found throw an exception
//                 if (devicePage.isEmpty()) {
//                         throw new BasicException(BasicResponse.builder()
//                                         .message("No devices found")
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND)
//                                         .build());
//                 }

//                 // Create a list of DTOs for devices
//                 List<DeviceDTO> deviceDTOs = devicePage.getContent().stream()
//                                 .map(device -> DeviceDTO.builder()
//                                                 .id(device.getId())
//                                                 .IMEI(device.getImei())
//                                                 .deviceType(device.getDeviceType().getName())
//                                                 .createAt(device.getCreatedAt())
//                                                 .deviceTypeId(device.getDeviceType().getId())
//                                                 .remarque(device.getRemarque())
//                                                 .status(device.getStatus())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 // Create metadata
//                 MetaData metaData = MetaData.builder()
//                                 .currentPage(devicePage.getNumber() + 1)
//                                 .totalPages(devicePage.getTotalPages())
//                                 .size(devicePage.getSize())
//                                 .build();

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .content(deviceDTOs)
//                                 .status(HttpStatus.OK)
//                                 .metadata(metaData)
//                                 .build();
//         }

// }
