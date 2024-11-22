package com.idirtrack.vehicle_service.device;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class DeviceService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    /**
     * Service for chnage the status of a device in stock microservice
     * 
     * @param id
     * @param status
     * @return boolean
     */

    public Boolean changeDeviceStatus(Long id, String status) {
        // Construct the URI with query parameters
        String uri = String.format("/?id=%d&status=%s", id, status);

        try {
            // Send request to stock microservice to change the status of the device
            WebClient.ResponseSpec responseSpec = webClientBuilder.build()
                    .put()
                    .uri("http://stock-service/stock-api/devices/status" + uri)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus == HttpStatus.SERVICE_UNAVAILABLE,
                            clientResponse -> Mono.error(new RuntimeException("Service Unavailable")));

            // Retry mechanism
            BasicResponse response = responseSpec
                    .bodyToMono(BasicResponse.class)
                    .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(4))
                            .filter(throwable -> throwable instanceof RuntimeException))
                    .block();

            // If response status is 200 OK, return true
            return responseSpec.toBodilessEntity()
                    .map(entity -> entity.getStatusCode() == HttpStatus.OK)
                    .block();

        } catch (Exception e) {
            logger.error("Error in changeDeviceStatus: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIND DEVICE BY ID IN STOCK MICROSERVICE
     * 
     * This method finds a device by its ID in the stock microservice. It returns a
     * device object if the device is found, otherwise it throws a BasicException.
     * 
     * @param id The ID of the device
     * @return
     */

    public DeviceDTO getDeviceByIdFromMicroservice(Long id) throws BasicException {
        // Call the stock microservice to get the device by its ID
        BasicResponse response = webClientBuilder.build()
                .get()
                .uri("http://stock-service/stock-api/devices/" + id + "/")
                .retrieve()
                .bodyToMono(BasicResponse.class)
                .block();

        if (response == null || response.getContent() == null) {
            BasicResponse errorResponse = BasicResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Device not found")
                    .build();
            throw new BasicException(errorResponse);
        }

        // Cast the content to a Map
        Map<String, Object> content = (Map<String, Object>) response.getContent();

        Long deviceMicroserviceId = content.get("id") instanceof Integer ? Long.valueOf((Integer) content.get("id")) : (Long) content.get("id");

        // Build DeviceDTO from response.content
        DeviceDTO deviceDTO = DeviceDTO.builder()
                .deviceMicroserviceId(deviceMicroserviceId)
                .imei((String) content.get("imei"))
                .type((String) content.get("deviceType"))
                .build();

        System.out.println(response.getContent());

        // Return the device DTO
        return deviceDTO;
    }

}
