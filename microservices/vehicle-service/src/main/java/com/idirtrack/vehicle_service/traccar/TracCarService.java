package com.idirtrack.vehicle_service.traccar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.idirtrack.vehicle_service.traccar.request.TracCarDeviceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TracCarService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TracCarService.class);

    public boolean createDevice(String clientName, String imei, String clientCompany, String vehicleMatricule) {
        String url = "http://152.228.219.146:8082/api/devices";

        String name = clientName + " - " + clientCompany + " - " + vehicleMatricule;

        // Create the request body
        TracCarDeviceRequest request = TracCarDeviceRequest.builder()
                .name(name)
                .uniqueId(imei)
                .build();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // add basic auth with username and password idirtech idirtech
        headers.setBasicAuth("idirtech ", "idirtech1");

        // Create the HttpEntity
        HttpEntity<TracCarDeviceRequest> entity = new HttpEntity<>(request, headers);

        // Send the POST request
        try {
            restTemplate.postForObject(url, entity, String.class);
            return true;
        } catch (Exception e) {
            logger.error("Error creating device: " + e.getMessage());
            return false;
        }
    }
}
