package com.idirtrack.vehicle_service.boitier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.basic.MetaData;
import com.idirtrack.vehicle_service.boitier.dto.BoitierDTO;
import com.idirtrack.vehicle_service.boitier.https.BoitierGetByIDResponse;
import com.idirtrack.vehicle_service.boitier.https.BoitierRequest;
import com.idirtrack.vehicle_service.device.Device;
import com.idirtrack.vehicle_service.device.DeviceDTO;
import com.idirtrack.vehicle_service.device.DeviceRepository;
import com.idirtrack.vehicle_service.device.DeviceService;
import com.idirtrack.vehicle_service.sim.Sim;
import com.idirtrack.vehicle_service.sim.SimDTO;
import com.idirtrack.vehicle_service.sim.SimRepository;
import com.idirtrack.vehicle_service.sim.SimService;
import com.idirtrack.vehicle_service.subscription.Subscription;
import com.idirtrack.vehicle_service.subscription.SubscriptionRepository;
import com.idirtrack.vehicle_service.utils.Error;

@Service
public class BoitierService {

        @Autowired
        private BoitierRepository boitierRepository;
        @Autowired
        private DeviceRepository deviceRepository;
        @Autowired
        private SimRepository simRepository;
        @Autowired
        private SubscriptionRepository subscriptionRepository;

        @Autowired
        private WebClient.Builder webClientBuilder;

        @Autowired
        private DeviceService deviceService;

        @Autowired
        private SimService simService;

        /*
         * Create new boitier
         */
        public BasicResponse createNewBoitier(BoitierRequest request) throws BasicException {
                List<Error> errors = new ArrayList<>();

                // Check if the device already exists in the database
                if (deviceRepository.existsByDeviceMicroserviceId(request.getDeviceMicroserviceId())) {
                        errors.add(Error.builder()
                                        .key("device")
                                        .message("Device already used in another boitier")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Check if the sim exists in the database
                if (simRepository.existsBySimMicroserviceId(request.getSimMicroserviceId())) {
                        errors.add(Error.builder()
                                        .key("sim")
                                        .message("Sim already used in another boitier")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Check if the start date is before the end date
                if (request.getStartDate().after(request.getEndDate())) {
                        errors.add(Error.builder()
                                        .key("startDate")
                                        .message("Start date must be before the end date")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Check if the start date is before the current date
                if (request.getStartDate().before(new java.util.Date())) {
                        errors.add(Error.builder()
                                        .key("startDate")
                                        .message("Start date must be after the current date")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                try {
                        // Find the device from the stock microservice
                        DeviceDTO deviceDTO = deviceService
                                        .getDeviceByIdFromMicroservice(request.getDeviceMicroserviceId());

                        // Build the device object
                        Device device = Device.builder()
                                        .deviceMicroserviceId(deviceDTO.getDeviceMicroserviceId())
                                        .imei(deviceDTO.getImei())
                                        .type(deviceDTO.getType())
                                        .build();
                        // Save the device in the database
                        device = deviceRepository.save(device);

                        // Find the sim from the stock microservice
                        SimDTO simDTO = simService.getSimByIdFromMicroservice(request.getSimMicroserviceId());

                        // Build the sim object
                        Sim sim = Sim.builder()
                                        .simMicroserviceId(simDTO.getSimMicroserviceId())
                                        .phone(simDTO.getPhone())
                                        .operatorName(simDTO.getOperatorName())
                                        .ccid(simDTO.getCcid())
                                        .build();
                        // Save the sim in the database
                        sim = simRepository.save(sim);

                        // Save the boitier in the database
                        Boitier boitier = Boitier.builder()
                                        .device(device)
                                        .sim(sim)
                                        .build();
                        boitier = boitierRepository.save(boitier);

                        // Save the subscription in the database
                        Subscription subscription = Subscription.builder()
                                        .startDate(request.getStartDate())
                                        .endDate(request.getEndDate())
                                        .boitier(boitier)
                                        .build();
                        subscription = subscriptionRepository.save(subscription);

                        // Change the status of device and sim to installed in stock microservice
                        Thread thread = new Thread(() -> {
                                deviceService.changeDeviceStatus(deviceDTO.getDeviceMicroserviceId(), "pending");
                                simService.changeSimStatus(simDTO.getSimMicroserviceId(), "pending");
                        });
                        thread.start();

                        // Create DTOs for the response
                        BoitierDTO boitierDTO = BoitierDTO.builder()
                                        .id(boitier.getId())
                                        .device(device.toDTO())
                                        .sim(sim.toDTO())
                                        .subscription(subscription.toDTO())
                                        .build();

                        // Return the response
                        return BasicResponse.builder()
                                        .content(boitierDTO)
                                        .status(HttpStatus.CREATED)
                                        .message("Boitier created successfully")
                                        .build();

                } catch (Exception e) {
                        errors.add(Error.builder()
                                        .key("internal")
                                        .message("An error occurred while creating the boitier: " + e.getMessage())
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }
        }

        /*
         * update boitier process
         * check if the boitier exists
         * check if the new device used in another boitier
         * check if the new sim used in another boitier
         * check if the start date is before the end date
         * check if the start date is before the current date
         * 
         * 
         */
        public BasicResponse updateBoitierById(Long id, BoitierRequest request) throws BasicException {
                List<Error> errors = new ArrayList<>();

                // Find the boitier by id
                Boitier boitier = boitierRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .status(HttpStatus.NOT_FOUND)
                                                .message("Boitier not found")
                                                .build()));

                // Check if the new device already exists in another boitier
                if (!boitier.getDevice().getDeviceMicroserviceId().equals(request.getDeviceMicroserviceId())
                                && deviceRepository.existsByDeviceMicroserviceId(request.getDeviceMicroserviceId())) {
                        errors.add(Error.builder()
                                        .key("device")
                                        .message("Device already used in another boitier")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Check if the new sim already exists in another boitier
                if (!boitier.getSim().getSimMicroserviceId().equals(request.getSimMicroserviceId())
                                && simRepository.existsBySimMicroserviceId(request.getSimMicroserviceId())) {
                        errors.add(Error.builder()
                                        .key("sim")
                                        .message("Sim already used in another boitier")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Check if the start date is before the end date
                if (request.getStartDate().after(request.getEndDate())) {
                        errors.add(Error.builder()
                                        .key("startDate")
                                        .message("Start date must be before the end date")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Check if the start date is before the current date
                if (request.getStartDate().before(new java.util.Date())) {
                        errors.add(Error.builder()
                                        .key("startDate")
                                        .message("Start date must be after the current date")
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }

                // Get the old device and sim
                Device oldDevice = boitier.getDevice();
                Sim oldSim = boitier.getSim();

                try {
                        // Update device if different from current device
                        if (!boitier.getDevice().getDeviceMicroserviceId().equals(request.getDeviceMicroserviceId())) {
                                // Find the device from the stock microservice
                                DeviceDTO deviceDTO = deviceService
                                                .getDeviceByIdFromMicroservice(request.getDeviceMicroserviceId());

                                // Build and save the new device
                                Device newDevice = Device.builder()
                                                .deviceMicroserviceId(deviceDTO.getDeviceMicroserviceId())
                                                .imei(deviceDTO.getImei())
                                                .type(deviceDTO.getType())
                                                .build();
                                newDevice = deviceRepository.save(newDevice);

                                // Update boitier with the new device
                                boitier.setDevice(newDevice);

                                // Save the boitier with the new device before deleting the old one
                                boitierRepository.save(boitier);

                                // Change the status of the new device to pending in stock microservice
                                Thread deviceStatusThread = new Thread(() -> {
                                        deviceService.changeDeviceStatus(request.getDeviceMicroserviceId(), "pending");
                                        // Change the status of the old device
                                        if (oldDevice != null) {
                                                deviceService.changeDeviceStatus(oldDevice.getDeviceMicroserviceId(),
                                                                "non_installed");
                                        }
                                });
                                deviceStatusThread.start();

                        }

                        // Update sim if different from current sim
                        if (!boitier.getSim().getSimMicroserviceId().equals(request.getSimMicroserviceId())) {
                                // Find the sim from the stock microservice
                                SimDTO simDTO = simService.getSimByIdFromMicroservice(request.getSimMicroserviceId());

                                // Build and save the new sim
                                Sim newSim = Sim.builder()
                                                .simMicroserviceId(simDTO.getSimMicroserviceId())
                                                .phone(simDTO.getPhone())
                                                .operatorName(simDTO.getOperatorName())
                                                .ccid(simDTO.getCcid())
                                                .build();
                                newSim = simRepository.save(newSim);

                                // Update boitier with the new sim
                                boitier.setSim(newSim);

                                // Save the boitier with the new sim before deleting the old one
                                boitierRepository.save(boitier);

                                // Change the status of the new sim to pending in stock microservice
                                Thread simStatusThread = new Thread(() -> {
                                        simService.changeSimStatus(request.getSimMicroserviceId(), "pending");
                                        // Change the status of the old sim
                                        if (oldSim != null) {
                                                simService.changeSimStatus(oldSim.getSimMicroserviceId(),
                                                                "non_installed");
                                        }
                                });
                                simStatusThread.start();

                        }

                        // Update the subscription
                        Subscription subscription = subscriptionRepository.findAllByBoitierId(boitier.getId())
                                        .getLast();
                        subscription.setStartDate(request.getStartDate());
                        subscription.setEndDate(request.getEndDate());
                        subscriptionRepository.save(subscription);

                        // Save the updated boitier
                        boitierRepository.save(boitier);

                        // Delete the old device
                        if (oldDevice != null) {
                                deviceRepository.deleteById(oldDevice.getId());
                        }

                        // Delete the old sim
                        if (oldSim != null) {
                                simRepository.deleteById(oldSim.getId());
                        }

                        // Create DTOs for the response
                        BoitierDTO boitierDTO = BoitierDTO.builder()
                                        .id(boitier.getId())
                                        .device(boitier.getDevice().toDTO())
                                        .sim(boitier.getSim().toDTO())
                                        .subscription(subscription.toDTO())
                                        .build();

                        // Return the response
                        return BasicResponse.builder()
                                        .content(boitierDTO)
                                        .status(HttpStatus.OK)
                                        .message("Boitier updated successfully")
                                        .build();

                } catch (Exception e) {
                        errors.add(Error.builder()
                                        .key("internal")
                                        .message("An error occurred while updating the boitier: " + e.getMessage())
                                        .build());
                        BasicResponse response = BasicResponse.builder()
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .errorsList(errors)
                                        .build();
                        throw new BasicException(response);
                }
        }

        // Build the device object

        /*
         * Service to get all boitiers with pagination
         */
        public BasicResponse getAllBoitiers(int page, int size) {
                // Créer la pagination
                Pageable pageRequest = PageRequest.of(page - 1, size);

                // Récupérer tous les boîtiers de la base de données
                Page<Boitier> boitierPage = boitierRepository.findAll(pageRequest);

                // Créer une liste de DTOs pour les boîtiers
                List<BoitierDTO> boitierDTOs = boitierPage.getContent().stream()
                                .map(boitier -> BoitierDTO.builder()
                                                .id(boitier.getId())
                                                .device(boitier.getDevice().toDTO())
                                                .sim(boitier.getSim().toDTO())
                                                .build())
                                .collect(Collectors.toList());

                MetaData metaData = MetaData.builder()
                                .currentPage(boitierPage.getNumber() + 1)
                                .totalPages(boitierPage.getTotalPages())
                                .size(boitierPage.getSize())
                                .build();

                Map<String, Object> data = new HashMap<>();
                data.put("boitiers", boitierDTOs);
                data.put("metadata", metaData);

                return BasicResponse.builder()
                                .content(data)

                                .status(HttpStatus.OK)
                                .message("Boitiers retrieved successfully")
                                .build();
        }

        /**
         * SERVICE TO DELETE A BOITIER BY ID
         * 
         * This service he get the boitier and boolean if the boitier is lost or not,
         * Then delete the boitier form the database, delete the device and the sim from
         * the database
         * If the boitier is lost, change the status of device and sim to lost in stock
         * microservice
         * If not lost, return the device and sim to the stock microservice by set the
         * status to not installed
         * Then delete the subscription from the database,
         * Finally, return a response with the status and message
         * 
         * @param id
         * @return
         * @throws BasicException
         */
        public BasicResponse deleteBoitierById(Long id, boolean isLost) throws BasicException {

                // Find the boitier by id
                Boitier boitier = boitierRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .status(HttpStatus.NOT_FOUND)
                                                .message("Boitier not found")
                                                .build()));
                // Find the device by id
                Device device = deviceRepository.findById(boitier.getDevice().getId())
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .status(HttpStatus.NOT_FOUND)
                                                .message("Device not found")
                                                .build()));

                // Find the sim by id
                Sim sim = simRepository.findById(boitier.getSim().getId())
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .status(HttpStatus.NOT_FOUND)
                                                .message("Sim not found")
                                                .build()));

                // Find all subscriptions by boitier
                List<Subscription> subscriptions = subscriptionRepository.findAllByBoitier(boitier);

                // Stock the sim microservice id
                Long simMicroserviceId = sim.getSimMicroserviceId();

                // Stock the device microservice id
                Long deviceMicroserviceId = device.getDeviceMicroserviceId();

                // Delete Boitier
                boitierRepository.deleteById(id);

                // Delete Device
                deviceRepository.deleteById(boitier.getDevice().getId());

                // Delete Sim
                simRepository.deleteById(boitier.getSim().getId());

                // Delete Subscriptions
                for (Subscription subscription : subscriptions) {
                        subscriptionRepository.deleteById(subscription.getId());
                }

                // Chnage the status of device and sim to lost in stock microservice
                Thread thread = new Thread(() -> {
                        if (isLost == true) {
                                deviceService.changeDeviceStatus(deviceMicroserviceId, "lost");
                                simService.changeSimStatus(simMicroserviceId, "lost");
                        } else {
                                deviceService.changeDeviceStatus(deviceMicroserviceId, "non_installed");
                                simService.changeSimStatus(simMicroserviceId, "non_installed");
                        }
                });
                thread.start();

                // Return the response
                return BasicResponse.builder()
                                .status(HttpStatus.OK)
                                .message("Boitier deleted successfully")
                                .build();

        }

        /**
         * Service to check if list of boitiers exists
         * 
         * @param boitierIds
         * @return Boolean
         */

        public Boolean isExistBoitiers(List<Long> boitierIds) {
                for (Long boitierId : boitierIds) {
                        if (!boitierRepository.existsById(boitierId)) {
                                return false;
                        }
                }

                return true;
        }

        public BasicResponse getBoitierById(Long id) throws BasicException {
                // Try to get the boitier by id
                Boitier boitier = boitierRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .status(HttpStatus.NOT_FOUND)
                                                .message("Boitier not found")
                                                .build()));

                // Create BoitierGetByIDResponse
                BoitierGetByIDResponse boitierGetByIDResponse = BoitierGetByIDResponse.builder()
                                .deviceMicroserviceId(boitier.getDevice().getDeviceMicroserviceId())
                                .simMicroserviceId(boitier.getSim().getSimMicroserviceId())
                                .startDate(boitier.getSubscriptions().getLast().getStartDate())
                                .endDate(boitier.getSubscriptions().getLast().getEndDate()).build();

                // Return the response
                return BasicResponse.builder()
                                .content(boitierGetByIDResponse)
                                .status(HttpStatus.OK)
                                .message("Boitier retrieved successfully")
                                .build();
        }

        /**
         * GET LIST OF BOITIERS NOT ASSOCIATED WITH A VEHICLE
         * 
         * This method returns a list of boitiers that are not associated with a
         * vehicle.
         * First, it creates a pagination object to get a page of boitiers from the
         * database. Then, it creates a list of DTOs for the boitiers. Finally, it
         * creates metadata for the response and returns the response.
         * 
         * @param page
         * @param size
         * @return
         */
        public BasicResponse getUnassignedBoitiers(int page, int size) throws BasicException {
                // Create pagination
                Pageable pageRequest = PageRequest.of(page - 1, size);

                // Get all boitiers not associated with a vehicle
                Page<Boitier> boitierPage = boitierRepository.findAllByVehicleIsNull(pageRequest);

                // Create a list of DTOs for the boitiers
                List<BoitierDTO> boitierDTOs = boitierPage.getContent().stream()
                                .map(boitier -> {
                                        // Build the device DTO
                                        DeviceDTO deviceDTO = DeviceDTO.builder()
                                                        .id(boitier.getDevice().getId())
                                                        .deviceMicroserviceId(
                                                                        boitier.getDevice().getDeviceMicroserviceId())
                                                        .imei(boitier.getDevice().getImei())
                                                        .type(boitier.getDevice().getType())
                                                        .build();
                                        // Build the sim DTO
                                        SimDTO simDTO = SimDTO.builder()
                                                        .id(boitier.getSim().getId())
                                                        .simMicroserviceId(boitier.getSim().getSimMicroserviceId())
                                                        .phone(boitier.getSim().getPhone())
                                                        .operatorName(boitier.getSim().getOperatorName())
                                                        .ccid(boitier.getSim().getCcid())
                                                        .build();
                                        // Build the boitier DTO
                                        return BoitierDTO.builder()
                                                        .id(boitier.getId())
                                                        .device(deviceDTO)
                                                        .sim(simDTO)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // Create metadata
                MetaData metadata = MetaData.builder()
                                .currentPage(boitierPage.getNumber() + 1)
                                .totalPages(boitierPage.getTotalPages())
                                .size(boitierPage.getSize())
                                .build();

                // Build and return the response
                return BasicResponse.builder()
                                .content(boitierDTOs)
                                .metadata(metadata)
                                .status(HttpStatus.OK)
                                .build();
        }

}
