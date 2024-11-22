// package com.idirtrack.stock_service.deviceType;

// import java.util.ArrayList;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import java.util.stream.Collectors;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;

// import com.idirtrack.stock_service.basics.BasicError;
// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.basics.MessageType;

// import lombok.RequiredArgsConstructor;

// import com.idirtrack.stock_service.basics.MetaData;
// import com.idirtrack.stock_service.device.DeviceRepository;
// import com.idirtrack.stock_service.deviceType.dtos.DeviceTypeDTO;
// import com.idirtrack.stock_service.deviceType.dtos.DeviceTypeRequest;

// @Service
// @RequiredArgsConstructor
// public class DeviceTypeService {

//         @Autowired
//         private DeviceTypeRepository deviceTypeRepository;

//         private final DeviceRepository deviceRepository;

//         // ------------------- APIs Methods -------------------

//         /**
//          * GET DEVICE TYPE BY ID
//          * 
//          * This method returns a device type by id, if the device type does not exist,
//          * it throws an exception
//          * 
//          * @param id
//          * @return BasicResponse
//          * @throws BasicException
//          */
//         public BasicResponse getDeviceTypeById(Long id) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();
//                 // Check if the device type exists, if it does not, throw an exception
//                 DeviceType deviceType = deviceTypeRepository.findById(id)
//                                 .orElseThrow(() -> {
//                                         errors.add(BasicError.of("id", "Device type not found"));
//                                         return new BasicException(BasicResponse.builder()
//                                                         .errors(errors)
//                                                         .messageType(MessageType.ERROR)
//                                                         .status(HttpStatus.NOT_FOUND).build());
//                                 });

//                 // Build the DeviceType DTO
//                 DeviceTypeDTO deviceTypeDTO = DeviceTypeDTO.builder()
//                                 .id(deviceType.getId())
//                                 .name(deviceType.getName())
//                                 .build();

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .content(deviceTypeDTO)
//                                 .messageType(MessageType.INFO)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * DELETE DEVICE TYPE
//          * 
//          * This method deletes a device type, if the device type does not exist, it
//          * throws an exception
//          * 
//          * @param id
//          * @return BasicResponse
//          * @throws BasicException
//          */
//         public BasicResponse deleteDeviceType(Long id) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();
//                 // Check if the device type exists, if it does not, throw an exception
//                 DeviceType deviceType = deviceTypeRepository.findById(id)
//                                 .orElseThrow(() -> {
//                                         errors.add(BasicError.of("id", "Device type not found"));
//                                         return new BasicException(BasicResponse.builder()
//                                                         .errors(errors)
//                                                         .messageType(MessageType.ERROR)
//                                                         .status(HttpStatus.NOT_FOUND).build());
//                                 });

//                 // Delete the device type
//                 deviceTypeRepository.delete(deviceType);

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .message("Device type deleted successfully")
//                                 .messageType(MessageType.INFO)
//                                 .status(HttpStatus.OK)
//                                 .build();
//         }

//         /**
//          * GET LIST OF DEVICE TYPES
//          * 
//          * This method returns all device types with pagination, if not devices types
//          * exist, returns throw an exception
//          * 
//          * @param page
//          * @param size
//          * 
//          * @return BasicResponse
//          * @throws BasicException
//          */

//         public BasicResponse getAllDeviceTypes(int page, int size) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();
//                 // Create a pageable object
//                 Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

//                 // Get all device types
//                 Page<DeviceType> deviceTypes = deviceTypeRepository.findAll(pageable);

//                 // Check if there are device types, if there are not, throw an exception
//                 if (deviceTypes.isEmpty()) {
//                         errors.add(BasicError.of("deviceTypes", "No device types found"));
//                         throw new BasicException(BasicResponse.builder()
//                                         .errors(errors)
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND).build());
//                 }

//                 // Map each device type to include the total number of devices
//                 List<DeviceTypeDTO> deviceTypeDTOs = deviceTypes.stream().map(deviceType -> {
//                         long deviceCount = deviceRepository.countByDeviceType(deviceType);
//                         return DeviceTypeDTO.builder()
//                                         .id(deviceType.getId())
//                                         .name(deviceType.getName())
//                                         .totalDevices(deviceCount)
//                                         .build();

//                 }).collect(Collectors.toList());

//                 // Build the metadata
//                 MetaData metadata = MetaData.builder()
//                                 .currentPage(page)
//                                 .size(size)
//                                 .totalPages(deviceTypes.getTotalPages())
//                                 .totalElements(deviceTypes.getTotalElements())
//                                 .build();

//                 // Build the response
//                 return BasicResponse.builder()
//                                 .content(deviceTypeDTOs)
//                                 .message("Device types found successfully of Page: " + page + " and Size: " + size)
//                                 .messageType(MessageType.INFO)
//                                 .status(HttpStatus.OK)
//                                 .metadata(metadata)
//                                 .build();
//         }

//         /**
//          * UPDATE DEVICE TYPE
//          * 
//          * This method updates a existing device type, if the device type does not
//          * exists, it throws an exception.
//          * If the device type already exists except the one
//          * being updated, throws an exception.
//          * 
//          * @param id
//          * @param request
//          * @return BasicResponse
//          * @throws BasicException
//          */
//         public BasicResponse updateDeviceType(Long id, DeviceTypeRequest request) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();

//                 // Check if the new device type name already exists except the one being
//                 // updated, if it does, throw an exception
//                 if (deviceTypeRepository.existsByNameAndIdNot(request.getName(), id)) {
//                         errors.add(BasicError.of("name", "Device type already exists"));
//                         throw new BasicException(BasicResponse.builder()
//                                         .errors(errors)
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.BAD_REQUEST).build());
//                 }

//                 // Check if the device type exists, if it does not, throw an exception
//                 DeviceType deviceType = deviceTypeRepository.findById(id)
//                                 .orElseThrow(() -> {
//                                         errors.add(BasicError.of("id", "Device type not found"));
//                                         return new BasicException(BasicResponse.builder()
//                                                         .errors(errors)
//                                                         .messageType(MessageType.ERROR)
//                                                         .status(HttpStatus.NOT_FOUND).build());
//                                 });

//                 // Update the device type name
//                 deviceType.setName(request.getName());
//                 deviceTypeRepository.save(deviceType);

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .message("Device type updated successfully")
//                                 .messageType(MessageType.INFO)
//                                 .status(HttpStatus.OK)
//                                 .build();

//         }

//         /**
//          * CREATE NEW DEVICE TYPE
//          * 
//          * This method creates a new device type, if the device type already exists, it
//          * throws an exception
//          * 
//          * @param deviceTypeRequest
//          * @return BasicResponse
//          * @throws BasicException
//          */
//         public BasicResponse createDeviceType(DeviceTypeRequest request) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();
//                 // Check if the device type already exists, if it does, throw an exception
//                 if (deviceTypeRepository.existsByName(request.getName())) {
//                         errors.add(BasicError.of("name", "Device type already exists"));
//                         throw new BasicException(BasicResponse.builder()
//                                         .errors(errors)
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.BAD_REQUEST).build());
//                 }

//                 // Build the device entity, and save it
//                 DeviceType deviceType = DeviceType.builder().name(request.getName()).build();
//                 deviceTypeRepository.save(deviceType);

//                 // Return the response
//                 return BasicResponse.builder()
//                                 .message("Device type created successfully")
//                                 .messageType(MessageType.INFO)
//                                 .status(HttpStatus.CREATED)
//                                 .build();

//         }

//         // ------------------- Utils Methods -------------------

//         /**
//          * Check if the device type exists
//          * 
//          * @param id
//          * @return void
//          * @throws BasicException
//          */

//         public void checkDeviceTypeExists(Long id) throws BasicException {
//                 List<BasicError> errors = new ArrayList<>();
//                 // Check if the device type exists, if it does not, throw an exception
//                 if (!deviceTypeRepository.existsById(id)) {
//                         // Add the error to the list
//                         errors.add(BasicError.of("id", "Device type not found"));
//                         // Throw the exception
//                         throw new BasicException(BasicResponse.builder()
//                                         .errors(errors)
//                                         .messageType(MessageType.ERROR)
//                                         .status(HttpStatus.NOT_FOUND).build());
//                 }

//         }

//         /**
//          * Find device type entity by id
//          * 
//          * @param id
//          * @return DeviceType
//          * @throws BasicException
//          */

//         public DeviceType findDeviceTypeById(Long deviceTypeId) throws BasicException {
//                 return deviceTypeRepository.findById(deviceTypeId)
//                                 .orElseThrow(() -> {
//                                         List<BasicError> errors = new ArrayList<>();
//                                         errors.add(BasicError.of("deviceTypeId", "Device type not found"));
//                                         return new BasicException(BasicResponse.builder()
//                                                         .errors(errors)
//                                                         .messageType(MessageType.ERROR)
//                                                         .status(HttpStatus.NOT_FOUND).build());
//                                 });
//         }


//         /**
//          * Get list of all device types
//          * 
//          * This method returns a list of all device types
//          * @return
//          * @throws BasicException
//          */
//         public BasicResponse getListOfAllDeviceTypes() throws BasicException {
//                 // Find all device types and map them to DTOs
//                 List<DeviceTypeDTO> deviceTypeDTOs = deviceTypeRepository.findAll().stream()
//                                 .map(deviceType -> DeviceTypeDTO.builder()
//                                                 .id(deviceType.getId())
//                                                 .name(deviceType.getName())
//                                                 .build())
//                                 .collect(Collectors.toList());

//                 // Return the list of device type DTOs in a BasicResponse
//                 return BasicResponse.builder()
//                                 .status(HttpStatus.OK)
//                                 .messageType(MessageType.SUCCESS)
//                                 .content(deviceTypeDTOs)
//                                 .build();
//         }
// }
