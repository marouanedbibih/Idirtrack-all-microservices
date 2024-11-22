package com.idirtrack.vehicle_service.vehicle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.boitier.Boitier;
import com.idirtrack.vehicle_service.boitier.dto.BoitierDTO;
import com.idirtrack.vehicle_service.device.Device;
import com.idirtrack.vehicle_service.sim.Sim;
import com.idirtrack.vehicle_service.subscription.Subscription;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService; // Replace with the actual name of your service class

    private Vehicle mockVehicle;
    private Boitier mockBoitier;
    private Device mockDevice;
    private Sim mockSim;
    private Subscription mockSubscription;

    @BeforeAll
    static void setupEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @BeforeEach
    void setUp() {
        // Create Device Mock Object
        mockDevice = Device.builder().id(1L).imei("123456789012345").deviceMicroserviceId(1L).build();

        // Create Sim Mock Object
        mockSim = Sim.builder().id(1L).phone("1234567890").simMicroserviceId(1L).build();

        // Create Subscription Mock Object
        mockSubscription = Subscription.builder()
                .id(1L)
                .startDate(java.sql.Date.valueOf("2023-01-01"))
                .endDate(java.sql.Date.valueOf("2024-01-01"))
                .build();

        // Create Boitier Mock Object
        mockBoitier = Boitier.builder().id(1L).device(mockDevice).sim(mockSim)
                .subscriptions(Arrays.asList(mockSubscription)).build();

        // Create Vehicle Mock Object
        mockVehicle = Vehicle.builder().id(1L).boitiers(Arrays.asList(mockBoitier)).build();
    }

    @Test
    void testGetVehicleBoities_Success() throws BasicException {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(mockVehicle));

        // Act
        BasicResponse response = vehicleService.getVehicleBoities(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContent());

        BoitierDTO boitierDTO = ((List<BoitierDTO>) response.getContent()).get(0);
        assertEquals(mockBoitier.getId(), boitierDTO.getId());
        assertEquals(mockDevice.getId(), boitierDTO.getDevice().getId());
        assertEquals(mockSim.getId(), boitierDTO.getSim().getId());
        assertEquals(mockSubscription.getId(), boitierDTO.getSubscription().getId());
    }
}