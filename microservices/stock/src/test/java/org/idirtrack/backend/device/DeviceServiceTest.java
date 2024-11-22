// package com.idirtrack.stock_service.device;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpStatus;

// import com.idirtrack.stock_service.basics.BasicException;
// import com.idirtrack.stock_service.basics.BasicResponse;
// import com.idirtrack.stock_service.deviceType.DeviceType;
// import com.idirtrack.stock_service.deviceType.DeviceTypeRepository;

// import java.sql.Date;
// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// public class DeviceServiceTest {

//     @Mock
//     private DeviceRepository deviceRepository;

//     @InjectMocks
//     private DeviceService deviceService;

//     @Mock
//     private DeviceTypeRepository deviceTypeRepository;

//     @BeforeEach
//     void setUp() {
//         // This initializes the mocks and injects them into the deviceService instance
//         MockitoAnnotations.openMocks(this);
//     }


    

//     /**
//      * Test Case for counting devices
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testCountDevices() {
//         // Arrange
//         long expectedCount = 50L;
//         when(deviceRepository.count()).thenReturn(expectedCount);

//         BasicResponse expectedResponse = BasicResponse.builder()
//                 .content(expectedCount)
//                 .status(HttpStatus.OK)
//                 .build();

//         // Act
//         BasicResponse actualResponse = deviceService.countDevices();

//         // Assert
//         assertEquals(expectedResponse, actualResponse);
//     }

//     /**
//      * Test Case for counting devices with zero count
//      * 
//      * @throws BasicException
//      */

//     @Test
//     void testCountDevices_ZeroCount() {
//         // Arrange
//         long expectedCount = 0L;
//         when(deviceRepository.count()).thenReturn(expectedCount);

//         BasicResponse expectedResponse = BasicResponse.builder()
//                 .content(expectedCount)
//                 .status(HttpStatus.OK)
//                 .build();

//         // Act
//         BasicResponse actualResponse = deviceService.countDevices();

//         // Assert
//         assertEquals(expectedResponse, actualResponse);
//     }

//     /**
//      * Test Case for counting devices grouped by status
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testCountDevicesGroupByStatus() {
//         // Arrange
//         when(deviceRepository.countByStatus(DeviceStatus.NON_INSTALLED)).thenReturn(10L);
//         when(deviceRepository.countByStatus(DeviceStatus.INSTALLED)).thenReturn(20L);
//         when(deviceRepository.countByStatus(DeviceStatus.LOST)).thenReturn(5L);
//         when(deviceRepository.countByStatus(DeviceStatus.PENDING)).thenReturn(15L);

//         Map<String, Long> expectedCountMap = new HashMap<>();
//         expectedCountMap.put("nonInstalled", 10L);
//         expectedCountMap.put("installed", 20L);
//         expectedCountMap.put("lost", 5L);
//         expectedCountMap.put("pending", 15L);

//         BasicResponse expectedResponse = BasicResponse.builder()
//                 .content(expectedCountMap)
//                 .status(HttpStatus.OK)
//                 .metadata(null)
//                 .build();

//         // Act
//         BasicResponse actualResponse = deviceService.countDevicesGroupByStatus();

//         // Assert
//         assertEquals(expectedResponse, actualResponse);
//     }

//     /**
//      * Test Case for counting devices grouped by status with zero counts
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testCountDevicesGroupByStatus_ZeroCounts() {
//         // Arrange
//         when(deviceRepository.countByStatus(DeviceStatus.NON_INSTALLED)).thenReturn(0L);
//         when(deviceRepository.countByStatus(DeviceStatus.INSTALLED)).thenReturn(0L);
//         when(deviceRepository.countByStatus(DeviceStatus.LOST)).thenReturn(0L);
//         when(deviceRepository.countByStatus(DeviceStatus.PENDING)).thenReturn(0L);

//         Map<String, Long> expectedCountMap = new HashMap<>();
//         expectedCountMap.put("nonInstalled", 0L);
//         expectedCountMap.put("installed", 0L);
//         expectedCountMap.put("lost", 0L);
//         expectedCountMap.put("pending", 0L);

//         BasicResponse expectedResponse = BasicResponse.builder()
//                 .content(expectedCountMap)
//                 .status(HttpStatus.OK)
//                 .metadata(null)
//                 .build();

//         // Act
//         BasicResponse actualResponse = deviceService.countDevicesGroupByStatus();

//         // Assert
//         assertEquals(expectedResponse, actualResponse);
//     }

//     /**
//      * 1. Test Case: All Fields Provided and Devices Found
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testFilterDevices_AllFieldsProvided_DevicesFound() throws BasicException {
//         // Given
//         String status = "INSTALLED";
//         Long deviceTypeId = 1L;
//         String createdFrom = "2023-01-01";
//         String createdTo = "2023-12-31";
//         int page = 1;
//         int size = 10;

//         DeviceType deviceType = new DeviceType();
//         deviceType.setId(deviceTypeId);
//         when(deviceTypeRepository.findById(deviceTypeId)).thenReturn(Optional.of(deviceType));

//         Device device = new Device();
//         device.setId(1L);
//         device.setImei("123456789012345");
//         device.setStatus(DeviceStatus.valueOf(status));
//         device.setDeviceType(deviceType);
//         device.setCreatedAt(Date.valueOf("2023-06-15"));

//         List<Device> devices = Collections.singletonList(device);
//         Page<Device> devicePage = new PageImpl<>(devices, PageRequest.of(0, size), 1);
//         when(deviceRepository.filterDevices(eq(DeviceStatus.valueOf(status)), eq(deviceType), any(Date.class),
//                 any(Date.class), any(Pageable.class))).thenReturn(devicePage);

//         // When
//         BasicResponse response = deviceService.filterDevices(status, deviceTypeId, createdFrom, createdTo, page, size);

//         // Then
//         assertNotNull(response);
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotNull(response.getContent());

//         @SuppressWarnings("unchecked")
//         List<DeviceDTO> deviceDTOs = (List<DeviceDTO>) response.getContent();
//         assertEquals(1, deviceDTOs.size());
//         assertEquals("123456789012345", deviceDTOs.get(0).getIMEI());
//         assertEquals(1, response.getMetadata().getTotalPages());
//     }

//     /**
//      * 2. Test Case: Device Type Not Found
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testFilterDevices_NoDevicesFound() {
//         // Given
//         String status = "INSTALLED";
//         Long deviceTypeId = 1L;
//         String createdFrom = "2023-01-01";
//         String createdTo = "2023-12-31";
//         int page = 1;
//         int size = 10;

//         DeviceType deviceType = new DeviceType();
//         deviceType.setId(deviceTypeId);
//         when(deviceTypeRepository.findById(deviceTypeId)).thenReturn(Optional.of(deviceType));

//         when(deviceRepository.filterDevices(eq(DeviceStatus.valueOf(status)), eq(deviceType), any(Date.class),
//                 any(Date.class), any(Pageable.class))).thenReturn(Page.empty());

//         // When
//         BasicException exception = assertThrows(BasicException.class,
//                 () -> deviceService.filterDevices(status, deviceTypeId, createdFrom, createdTo, page, size));

//         // Then
//         assertEquals(HttpStatus.NOT_FOUND, exception.getResponse().getStatus());
//         assertEquals("No devices found", exception.getResponse().getMessage());
//     }

//     /**
//      * 3. Test Case: Filtering by Status Only
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testFilterDevices_FilterByStatusOnly() throws BasicException {
//         // Given
//         String status = "INSTALLED";
//         Long deviceTypeId = null;
//         String createdFrom = null;
//         String createdTo = null;
//         int page = 1;
//         int size = 10;

//         // Device Type Test Object
//         DeviceType deviceType = new DeviceType();
//         deviceType.setId(deviceTypeId);
//         deviceType.setName("Test Device Type");

//         // Device Test Object
//         Device device = new Device();
//         device.setId(1L);
//         device.setImei("123456789012345");
//         device.setStatus(DeviceStatus.valueOf(status));
//         device.setDeviceType(deviceType);
//         device.setCreatedAt(Date.valueOf("2023-06-15"));

//         List<Device> devices = Collections.singletonList(device);
//         Page<Device> devicePage = new PageImpl<>(devices, PageRequest.of(0, size), 1);
//         when(deviceRepository.filterDevices(eq(DeviceStatus.valueOf(status)), eq(null), eq(null), eq(null),
//                 any(Pageable.class))).thenReturn(devicePage);

//         // When
//         BasicResponse response = deviceService.filterDevices(status, deviceTypeId, createdFrom, createdTo, page, size);

//         // Then
//         assertNotNull(response);
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotNull(response.getContent());

//         @SuppressWarnings("unchecked")
//         List<DeviceDTO> deviceDTOs = (List<DeviceDTO>) response.getContent();
//         assertEquals(1, deviceDTOs.size());
//         assertEquals("123456789012345", deviceDTOs.get(0).getIMEI());
//     }

//     /**
//      * 4. Test Case: Filtering by Device Type Only
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testFilterDevices_FilterByDeviceTypeOnly() throws BasicException {
//         // Given
//         String status = null;
//         Long deviceTypeId = 1L;
//         String createdFrom = null;
//         String createdTo = null;
//         int page = 1;
//         int size = 10;

//         // Device Type Test Object
//         DeviceType deviceType = new DeviceType();
//         deviceType.setId(deviceTypeId);
//         when(deviceTypeRepository.findById(deviceTypeId)).thenReturn(Optional.of(deviceType));

//         // Device Test Object
//         Device device = new Device();
//         device.setId(1L);
//         device.setImei("123456789012345");
//         device.setDeviceType(deviceType);
//         device.setCreatedAt(Date.valueOf("2023-06-15"));

//         List<Device> devices = Collections.singletonList(device);
//         Page<Device> devicePage = new PageImpl<>(devices, PageRequest.of(0, size), 1);
//         when(deviceRepository.filterDevices(eq(null), eq(deviceType), eq(null), eq(null), any(Pageable.class)))
//                 .thenReturn(devicePage);

//         // When
//         BasicResponse response = deviceService.filterDevices(status, deviceTypeId, createdFrom, createdTo, page, size);

//         // Then
//         assertNotNull(response);
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotNull(response.getContent());

//         @SuppressWarnings("unchecked")
//         List<DeviceDTO> deviceDTOs = (List<DeviceDTO>) response.getContent();
//         assertEquals(1, deviceDTOs.size());
//         assertEquals("123456789012345", deviceDTOs.get(0).getIMEI());
//     }

//     /**
//      * 5. Test Case: Filtering by Created Date Range Only
//      * 
//      * @throws BasicException
//      */

//     @Test
//     void testFilterDevices_FilterByCreatedDateRangeOnly() throws BasicException {
//         // Given
//         String status = null;
//         Long deviceTypeId = null;
//         String createdFrom = "2023-01-01";
//         String createdTo = "2023-12-31";
//         int page = 1;
//         int size = 10;

//         // Device Type Test Object
//         DeviceType deviceType = new DeviceType();
//         deviceType.setId(deviceTypeId);
//         deviceType.setName("Test Device Type");

//         // Device Test Object
//         Device device = new Device();
//         device.setId(1L);
//         device.setImei("123456789012345");
//         device.setCreatedAt(Date.valueOf("2023-06-15"));
//         device.setStatus(DeviceStatus.NON_INSTALLED);
//         device.setDeviceType(deviceType);

//         List<Device> devices = Collections.singletonList(device);
//         Page<Device> devicePage = new PageImpl<>(devices, PageRequest.of(0, size), 1);
//         when(deviceRepository.filterDevices(eq(null), eq(null), eq(Date.valueOf(createdFrom)),
//                 eq(Date.valueOf(createdTo)), any(Pageable.class))).thenReturn(devicePage);

//         // When
//         BasicResponse response = deviceService.filterDevices(status, deviceTypeId, createdFrom, createdTo, page, size);

//         // Then
//         assertNotNull(response);
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertNotNull(response.getContent());

//         @SuppressWarnings("unchecked")
//         List<DeviceDTO> deviceDTOs = (List<DeviceDTO>) response.getContent();
//         assertEquals(1, deviceDTOs.size());
//         assertEquals("123456789012345", deviceDTOs.get(0).getIMEI());
//     }

//     /**
//      * Test senario for searching devices successfully
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testSearchDevicesSuccess() throws BasicException {
//         // Arrange
//         Device device = new Device();
//         device.setId(1L);
//         device.setImei("123456789");
//         device.setDeviceType(DeviceType.builder().id(1L).name("Smartphone").build());
//         device.setCreatedAt(Date.valueOf(LocalDateTime.now().toLocalDate()));
//         device.setRemarque("Test device");
//         device.setStatus(DeviceStatus.NON_INSTALLED);

//         List<Device> devices = Collections.singletonList(device);
//         Page<Device> devicePage = new PageImpl<>(devices);

//         when(deviceRepository.search(anyString(), any(Pageable.class))).thenReturn(devicePage);

//         // Act
//         BasicResponse response = deviceService.searchDevices("123", 1, 10);

//         // Assert
//         assertNotNull(response);
//         assertEquals(HttpStatus.OK, response.getStatus());
//         assertEquals(1, response.getMetadata().getCurrentPage());
//         assertEquals(1, response.getMetadata().getTotalPages());
//         assertEquals(1, response.getMetadata().getSize());
//     }

//     /**
//      * Test senario for searching devices with no results
//      * 
//      * @throws BasicException
//      */
//     @Test
//     void testSearchDevicesNotFound() {
//         // Arrange
//         when(deviceRepository.search(anyString(), any(Pageable.class)))
//                 .thenReturn(new PageImpl<>(Collections.emptyList()));

//         // Act & Assert
//         BasicException thrown = assertThrows(BasicException.class, () -> {
//             deviceService.searchDevices("nonexistent", 1, 10);
//         });

//         assertEquals("No devices found", thrown.getResponse().getMessage());
//         assertEquals(HttpStatus.NOT_FOUND, thrown.getResponse().getStatus());
//     }
// }