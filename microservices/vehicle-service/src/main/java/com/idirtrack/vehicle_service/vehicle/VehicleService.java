package com.idirtrack.vehicle_service.vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.basic.MessageType;
import com.idirtrack.vehicle_service.basic.MetaData;
import com.idirtrack.vehicle_service.boitier.Boitier;
import com.idirtrack.vehicle_service.boitier.BoitierRepository;
import com.idirtrack.vehicle_service.boitier.BoitierService;
import com.idirtrack.vehicle_service.boitier.dto.BoitierDTO;
import com.idirtrack.vehicle_service.client.Client;
import com.idirtrack.vehicle_service.client.ClientDTO;
import com.idirtrack.vehicle_service.client.ClientRepository;
import com.idirtrack.vehicle_service.client.ClientService;
import com.idirtrack.vehicle_service.device.DeviceDTO;
import com.idirtrack.vehicle_service.device.DeviceService;
import com.idirtrack.vehicle_service.sim.SimDTO;
import com.idirtrack.vehicle_service.sim.SimService;
import com.idirtrack.vehicle_service.subscription.SubscriptionDTO;
import com.idirtrack.vehicle_service.traccar.TracCarService;
import com.idirtrack.vehicle_service.vehicle.https.VehicleRequest;
import com.idirtrack.vehicle_service.vehicle.https.VehicleResponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class VehicleService {
        @Autowired
        private VehicleRepository vehicleRepository;
        @Autowired
        private ClientRepository clientRepository;

        @Autowired
        private WebClient.Builder webClientBuilder;

        @Autowired
        private BoitierRepository boitierRepository;

        @Autowired
        private ClientService clientService;

        @Autowired
        private BoitierService boitierService;

        @Autowired
        private TracCarService tracCarService;

        @Autowired
        private DeviceService deviceService;

        @Autowired
        private SimService simService;

        private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

        public BasicResponse createNewVehicle(VehicleRequest request) throws BasicException {
                // Verify if the vehicle does not already exist by matricule
                if (vehicleRepository.existsByMatricule(request.getMatricule())) {
                        throw new BasicException(BasicResponse.builder()
                                        .message("Vehicle already exists")
                                        .messageType(MessageType.ERROR)
                                        .status(HttpStatus.CONFLICT)
                                        .build());

                }

                // Declare the client entity object
                Client client = null;
                // Verify if the client exists in the database, if not, check if it exists in
                // the user microservice
                if (!clientRepository.existsByClientMicroserviceId(request.getClientMicroserviceId())) {
                        ClientDTO clientDTO = clientService
                                        .getClientFormUserMicroservice(request.getClientMicroserviceId());
                        client = clientService.saveClient(clientDTO);
                }
                // If exists, get the client from the database
                else {
                        client = clientRepository.findByClientMicroserviceId(request.getClientMicroserviceId());
                }

                /*
                 * Check if the boitiers exist in the database and are not already attached to
                 * another vehicle
                 *
                 * If one boitier not exist in the database, throw an exception
                 * If one boitier is already attached to another vehicle, throw an exception
                 */
                List<Boitier> boitiers = new ArrayList<>();
                for (Long boitierId : request.getBoitiersIds()) {

                        // Check if the boitier exists in the database
                        Boitier boitier = boitierRepository.findById(boitierId)
                                        .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                        .message("Boitier not found")
                                                        .messageType(MessageType.ERROR)
                                                        .status(HttpStatus.NOT_FOUND)
                                                        .build()));
                        // Check if the boitier is already attached to a vehicle
                        if (boitier.getVehicle() != null) {
                                String message = "Boitier with the phone " + boitier.getSim().getPhone()
                                                + "and device IMEI "
                                                + boitier.getDevice().getImei() + " already attached to a vehicle";
                                throw new BasicException(BasicResponse.builder()
                                                .message(message)
                                                .messageType(MessageType.WARNING)
                                                .status(HttpStatus.CONFLICT)
                                                .build());
                        }

                        // Add the boitier to the list of boitiers
                        boitiers.add(boitier);
                }

                // Save the Boities in TracCar Microservice
                for (Boitier boitier : boitiers) {
                        boolean isSaved = tracCarService.createDevice(
                                        client.getName(),
                                        boitier.getDevice().getImei(),
                                        client.getCompany(),
                                        request.getMatricule());
                        if (!isSaved) {
                                throw new BasicException(BasicResponse.builder()
                                                .message("Error while saving the boitier in TracCar Microservice")
                                                .messageType(MessageType.WARNING)
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .build());
                        }
                }

                // Attach Boitiers to the vehicle and save the vehicle in the database
                Vehicle vehicle = Vehicle.builder()
                                .matricule(request.getMatricule())
                                .client(clientRepository.findByClientMicroserviceId(request.getClientMicroserviceId()))
                                .type(request.getType())
                                .boitiers(boitiers)
                                .build();

                vehicle = vehicleRepository.save(vehicle);

                // Attach vehicle to boitiers
                for (Boitier boitier : boitiers) {
                        boitier.setVehicle(vehicle);
                        boitierRepository.save(boitier);
                }

                // Chnage the status of the boitiers in the stock microservice

                for (Boitier boitier : boitiers) {
                        boolean isDeviceStatusChnaged = deviceService
                                        .changeDeviceStatus(boitier.getDevice().getDeviceMicroserviceId(),"installed");
                        if (!isDeviceStatusChnaged) {
                                throw new BasicException(BasicResponse.builder()
                                                .message("Error while changing the status of the device in the stock microservice")
                                                .messageType(MessageType.WARNING)
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .build());
                        }
                        boolean isSimStatusChanged = simService.changeSimStatus(boitier.getSim().getSimMicroserviceId(),"installed");

                        if (!isSimStatusChanged) {
                                throw new BasicException(BasicResponse.builder()
                                                .message("Error while changing the status of the sim in the stock microservice")
                                                .messageType(MessageType.WARNING)
                                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .build());
                        }
                }

                return BasicResponse.builder()
                                .message("Vehicle created successfully")
                                .messageType(MessageType.INFO)
                                .status(HttpStatus.CREATED)
                                .build();
        }

        /**
         * Retrieves a paginated list of vehicles.
         * 
         * This method performs the following steps:
         * 1. Creates a pagination request using the provided page number and page size.
         * 2. Fetches a page of vehicles from the repository.
         * 3. Throws a {@link BasicException} if no vehicles are found for the specified
         * page.
         * 4. Transforms the vehicles into DTOs for the response.
         * 5. Constructs metadata for pagination, including current page, total pages,
         * and page size.
         * 6. Builds and returns a {@link BasicResponse} object containing the vehicle
         * list, metadata, and status.
         * 
         * @param page the page number (1-based index) to retrieve
         * @param size the number of vehicles per page
         * @return a {@link BasicResponse} with the list of vehicles, pagination
         *         metadata, and status
         * @throws BasicException if no vehicles are found for the specified page
         */
        public BasicResponse getAllVehicles(int page, int size) throws BasicException {

                // Create a pagination request
                Pageable pageable = PageRequest.of(page - 1, size,Sort.by(Sort.Direction.DESC, "id"));

                // Get Page of vehicles
                Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);

                // Throw exception if the vehicles list is empty
                if (vehicles.getContent().isEmpty()) {
                        throw new BasicException(BasicResponse.builder()
                                        .message("No vehicles found")
                                        .messageType(MessageType.INFO)
                                        .status(HttpStatus.NOT_FOUND)
                                        .build());
                }

                // Build the DTO of vehicle resppnse
                List<VehicleResponse> vehiclesResponse = vehicles.getContent().stream()
                                .map(vehicle -> {
                                        ClientDTO clientDTO = ClientDTO.builder()
                                                        .id(vehicle.getClient().getId())
                                                        .name(vehicle.getClient().getName())
                                                        .company(vehicle.getClient().getCompany())
                                                        .clientMicroserviceId(vehicle.getClient().getClientMicroserviceId())
                                                        .build();
                                        return VehicleResponse.builder()
                                                        .vehicle(vehicle.toDTO())
                                                        .client(clientDTO)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // Build the metadata object
                MetaData metaData = MetaData.builder()
                                .currentPage(vehicles.getNumber() + 1)
                                .totalPages(vehicles.getTotalPages())
                                .size(vehicles.getSize())
                                .build();

                // Build the response object
                return BasicResponse.builder()
                                .content(vehiclesResponse)
                                .metadata(metaData)
                                .status(HttpStatus.OK)
                                .build();
        }

        // Service: Get the vehicle by id and her boitiers
        public List<Boitier> attachBoitierToVehicle(Vehicle vehicle, List<Long> boitierIds) throws BasicException {
                // Boucle in boitiers for get the boitier and attach to vehicle
                List<Boitier> boitiersList = new ArrayList<>();
                for (Long boitierId : boitierIds) {
                        Boitier boitier = boitierRepository.findById(boitierId)
                                        .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                        .message("Boitier not found")
                                                        .messageType(MessageType.ERROR)
                                                        .status(HttpStatus.NOT_FOUND)
                                                        .build()));
                        boitier.setVehicle(vehicle);
                        boitier = boitierRepository.save(boitier);
                        boitiersList.add(boitier);
                }
                vehicle.setBoitiers(boitiersList);

                return boitiersList;
        }

 
        /**
         * Retrieves detailed information about a specific vehicle by its ID.
         *
         * @param vehicleId the ID of the vehicle to retrieve
         * @return a BasicResponse with detailed vehicle information
         * @throws BasicException if the vehicle is not found
         */
        public BasicResponse getVehicleById(Long vehicleId) throws BasicException {
                Vehicle vehicle = vehicleRepository.findById(vehicleId)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .message("Vehicle not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

                // Create a detailed response
                ClientDTO clientDTO = ClientDTO.builder()
                                .id(vehicle.getClient().getId())
                                .name(vehicle.getClient().getName())
                                .company(vehicle.getClient().getCompany())
                                .clientMicroserviceId(vehicle.getClient().getClientMicroserviceId())
                                .build();


                // Build Vehicle DTO
                VehicleDTO vehicleDTO = VehicleDTO.builder()
                                .id(vehicle.getId())
                                .matricule(vehicle.getMatricule())
                                .type(vehicle.getType())
                                .build();
                
                // Build the VehicleResponse
                VehicleResponse vehicleResponse = VehicleResponse.builder()
                                .vehicle(vehicleDTO)
                                .client(clientDTO)
                                .build();

                return BasicResponse.builder()
                                .content(vehicleResponse)
                                .status(HttpStatus.OK)
                                .build();
        }

        /**
         * Retrieves the boitiers of a vehicle by its ID.
         * 
         * This service retrieves the boitiers of a vehicle by its ID. It performs the
         * following steps:
         * 1. Finds the vehicle by ID.
         * 2. Retrieves the boitiers of the vehicle
         * 3. Transforms the boitiers into DTOs
         * 4. Builds and returns a {@link BasicResponse} object containing the list of
         * boitiers and status.
         * 
         * @param vehicleId the ID of the vehicle to retrieve
         */
        public BasicResponse getVehicleBoities(Long vehicleId) throws BasicException {
                // Find the vehicle by id
                Vehicle vehicle = vehicleRepository.findById(vehicleId)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .message("Vehicle not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

                // Get the boitiers of the vehicle
                List<Boitier> boitiers = vehicle.getBoitiers();

                // Transform the boitiers into DTOs
                List<BoitierDTO> boitiersDTO = boitiers.stream()
                                .map(boitier -> {
                                        // Get the device of the boitier
                                        DeviceDTO deviceDTO = DeviceDTO.builder()
                                                        .id(boitier.getDevice().getId())
                                                        .imei(boitier.getDevice().getImei())
                                                        .deviceMicroserviceId(boitier.getDevice().getDeviceMicroserviceId())
                                                        .type(boitier.getDevice().getType())
                                                        .build();
                                        
                                        // Get the sim of the boitier
                                        SimDTO simDTO = SimDTO.builder()
                                                        .id(boitier.getSim().getId())
                                                        .phone(boitier.getSim().getPhone())
                                                        .simMicroserviceId(boitier.getSim().getSimMicroserviceId())
                                                        .operatorName(boitier.getSim().getOperatorName())
                                                        .build();
                                        
                                        // Get the last subscription of the boitier
                                        SubscriptionDTO subscriptionDTO = SubscriptionDTO.builder()
                                                        .id(boitier.getSubscriptions().get(boitier.getSubscriptions().size() - 1).getId())
                                                        .startDate(boitier.getSubscriptions().get(boitier.getSubscriptions().size() - 1).getStartDate())
                                                        .endDate(boitier.getSubscriptions().get(boitier.getSubscriptions().size() - 1).getEndDate())
                                                        .build();

                                        // Build the boitier DTO
                                        return BoitierDTO.builder()
                                                        .id(boitier.getId())
                                                        .device(deviceDTO)
                                                        .sim(simDTO)
                                                        .subscription(subscriptionDTO)
                                                        .build();
                                        
                                })
                                .collect(Collectors.toList());
                
                // Return the response
                return BasicResponse.builder()
                                .content(boitiersDTO)
                                .status(HttpStatus.OK)
                                .build();
        }
}
