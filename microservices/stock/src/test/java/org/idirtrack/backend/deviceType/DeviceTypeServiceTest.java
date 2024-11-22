// package com.idirtrack.stock_service.deviceType;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpStatus;

// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.basics.MessageType;
// import com.idirtrack.stock_service.device.DeviceRepository;
// import com.idirtrack.stock_service.deviceType.dtos.DeviceTypeDTO;
// import com.idirtrack.stock_service.deviceType.dtos.DeviceTypeRequest;

// @ExtendWith(MockitoExtension.class)
// class DeviceTypeServiceTest {

//     @Mock
//     private DeviceTypeRepository deviceTypeRepository;

//     @InjectMocks
//     private DeviceTypeService deviceTypeService;

//     @Mock
//     private DeviceRepository deviceRepository;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);

//     }

//     /**
//      * Tests the scenario for get lisy of all device types.
//      * 
//      * This test verifies that the method `getListOfAllDeviceTypes` returns the
//      * expected response when the list of device types is not empty.
//      * 
//      * @throws BasicException if an error occurs during the retrieval process (not
//      *                        expected in this test).
//      * 
//      * @see DeviceTypeService#getListOfAllDeviceTypes()
//      * @see DeviceTypeRepository#findAll()
//      * 
//      */
//     @Test
//     void testGetListOfAllDeviceTypes_Success() throws BasicException {
//         // Arrange
//         DeviceType deviceType1 = new DeviceType();
//         deviceType1.setId(1L);
//         deviceType1.setName("Type1");

//         DeviceType deviceType2 = new DeviceType();
//         deviceType2.setId(2L);
//         deviceType2.setName("Type2");

//         List<DeviceType> deviceTypes = Arrays.asList(deviceType1, deviceType2);

//         when(deviceTypeRepository.findAll()).thenReturn(deviceTypes);

//         List<DeviceTypeDTO> expectedDeviceTypeDTOs = Arrays.asList(
//                 DeviceTypeDTO.builder().id(1L).name("Type1").build(),
//                 DeviceTypeDTO.builder().id(2L).name("Type2").build());

//         // Act
//         BasicResponse actualResponse = deviceTypeService.getListOfAllDeviceTypes();

//         // Assert
//         assertEquals(HttpStatus.OK, actualResponse.getStatus());
//         assertEquals(MessageType.SUCCESS, actualResponse.getMessageType());
//         assertEquals(expectedDeviceTypeDTOs, actualResponse.getContent());
//     }

//     /**
//      * Tests the scenario where a device type is successfully updated.
//      * 
//      * This test verifies that the method `updateDeviceType` successfully updates
//      * the name
//      * of a device type and returns the expected response.
//      * 
//      * @throws BasicException if an error occurs during the update process (not
//      *                        expected in this test).
//      */
//     @Test
//     void updateDeviceType_ShouldUpdateDeviceTypeSuccessfully() throws BasicException {
//         // Arrange
//         Long id = 1L;
//         DeviceTypeRequest request = new DeviceTypeRequest();
//         request.setName("UpdatedDeviceType");

//         DeviceType existingDeviceType = DeviceType.builder().id(id).name("OldDeviceType").build();

//         when(deviceTypeRepository.existsByNameAndIdNot(request.getName(), id)).thenReturn(false);
//         when(deviceTypeRepository.findById(id)).thenReturn(Optional.of(existingDeviceType));

//         // Act
//         BasicResponse response = deviceTypeService.updateDeviceType(id, request);

//         // Assert
//         assertEquals("Device type updated successfully", response.getMessage());
//         assertEquals(MessageType.INFO, response.getMessageType());
//         assertEquals(HttpStatus.OK, response.getStatus());

//         verify(deviceTypeRepository, times(1)).existsByNameAndIdNot(request.getName(), id);
//         verify(deviceTypeRepository, times(1)).findById(id);
//         verify(deviceTypeRepository, times(1)).save(existingDeviceType);
//         assertEquals("UpdatedDeviceType", existingDeviceType.getName());
//     }

//     /**
//      * Tests the scenario where the device type update fails due to the new name
//      * already existing.
//      * 
//      * This test verifies that the method `updateDeviceType` throws a
//      * `BasicException`
//      * with the appropriate error message and status code when a device type with
//      * the
//      * new name already exists under a different ID.
//      */
//     @Test
//     void updateDeviceType_ShouldThrowExceptionIfDeviceTypeNameExists() {
//         // Arrange
//         Long id = 1L;
//         DeviceTypeRequest request = new DeviceTypeRequest();
//         request.setName("ExistingDeviceType");

//         when(deviceTypeRepository.existsByNameAndIdNot(request.getName(), id)).thenReturn(true);

//         // Act & Assert
//         BasicException exception = assertThrows(BasicException.class, () -> {
//             deviceTypeService.updateDeviceType(id, request);
//         });

//         assertEquals(HttpStatus.BAD_REQUEST, exception.getResponse().getStatus());
//         assertEquals(MessageType.ERROR, exception.getResponse().getMessageType());
//         assertEquals("Device type already exists", exception.getResponse().getError().getMessage());

//         verify(deviceTypeRepository, times(1)).existsByNameAndIdNot(request.getName(), id);
//         verify(deviceTypeRepository, times(0)).findById(anyLong());
//         verify(deviceTypeRepository, times(0)).save(any(DeviceType.class));
//     }

//     /**
//      * Tests the scenario where the device type update fails because the device type
//      * does not exist.
//      * 
//      * This test verifies that the method `updateDeviceType` throws a
//      * `BasicException`
//      * with the appropriate error message and status code when the device type to be
//      * updated
//      * is not found in the database.
//      */
//     @Test
//     void updateDeviceType_ShouldThrowExceptionIfDeviceTypeNotFound() {
//         // Arrange
//         Long id = 1L;
//         DeviceTypeRequest request = new DeviceTypeRequest();
//         request.setName("NewDeviceType");

//         when(deviceTypeRepository.existsByNameAndIdNot(request.getName(), id)).thenReturn(false);
//         when(deviceTypeRepository.findById(id)).thenReturn(Optional.empty());

//         // Act & Assert
//         BasicException exception = assertThrows(BasicException.class, () -> {
//             deviceTypeService.updateDeviceType(id, request);
//         });

//         assertEquals(HttpStatus.NOT_FOUND, exception.getResponse().getStatus());
//         assertEquals(MessageType.ERROR, exception.getResponse().getMessageType());
//         assertEquals("Device type not found", exception.getResponse().getError().getMessage());

//         verify(deviceTypeRepository, times(1)).existsByNameAndIdNot(request.getName(), id);
//         verify(deviceTypeRepository, times(1)).findById(id);
//         verify(deviceTypeRepository, times(0)).save(any(DeviceType.class));
//     }

//     @Test
//     void createDeviceType_ShouldCreateDeviceTypeSuccessfully() throws BasicException {
//         // Arrange
//         DeviceTypeRequest request = new DeviceTypeRequest();
//         request.setName("NewDeviceType");

//         when(deviceTypeRepository.existsByName(request.getName())).thenReturn(false);

//         // Act
//         BasicResponse response = deviceTypeService.createDeviceType(request);

//         // Assert
//         assertEquals("Device type created successfully", response.getMessage());
//         assertEquals(MessageType.INFO, response.getMessageType());
//         assertEquals(HttpStatus.CREATED, response.getStatus());

//         verify(deviceTypeRepository, times(1)).existsByName(request.getName());
//         verify(deviceTypeRepository, times(1)).save(any(DeviceType.class));
//     }

//     @Test
//     void createDeviceType_ShouldThrowExceptionIfDeviceTypeExists() {
//         // Arrange
//         DeviceTypeRequest request = new DeviceTypeRequest();
//         request.setName("ExistingDeviceType");

//         when(deviceTypeRepository.existsByName(request.getName())).thenReturn(true);

//         // Act & Assert
//         BasicException exception = assertThrows(BasicException.class, () -> {
//             deviceTypeService.createDeviceType(request);
//         });

//         assertEquals(HttpStatus.BAD_REQUEST, exception.getResponse().getStatus());
//         assertEquals(MessageType.ERROR, exception.getResponse().getMessageType());
//         assertEquals("Device type already exists", exception.getResponse().getError().getMessage());

//         verify(deviceTypeRepository, times(1)).existsByName(request.getName());
//         verify(deviceTypeRepository, times(0)).save(any(DeviceType.class));
//     }
// }
