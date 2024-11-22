package com.idirtrack.vehicle_service.sim;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;

import reactor.core.publisher.Mono;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class SimService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(SimService.class);

    /**
     * Service for chnage the status of a sim in stock microservice
     * 
     * @param id
     * @param status
     * @return boolean
     */

    public Boolean changeSimStatus(Long id, String status) {
        // Construct the URI with query parameters
        String uri = String.format("/?id=%d&status=%s", id, status);

        try {
            // Send request to stock microservice to change the status of the SIM
            WebClient.ResponseSpec responseSpec = webClientBuilder.build()
                    .put()
                    .uri("http://stock-service/stock-api/sim/status" + uri)
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
            logger.error("Error in changeSimStatus: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIND SIM BY ID FROM STOCK MICROSERVICE
     * 
     * @param id of the sim
     * @return SimDTO
     * @throws BasicException
     */

    public SimDTO getSimByIdFromMicroservice(Long id) throws BasicException {
        // Call the stock microservice to get the sim by its ID
        BasicResponse response = webClientBuilder.build()
                .get()
                .uri("http://stock-service/stock-api/sim/" + id + "/")
                .retrieve()
                .bodyToMono(BasicResponse.class)
                .block();

        if (response == null || response.getContent() == null) {
            BasicResponse errorResponse = BasicResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Sim not found")
                    .build();
            throw new BasicException(errorResponse);
        }

        // Cast the content to a Map
        Map<String, Object> content = (Map<String, Object>) response.getContent();

        Long simMicroserviceId = content.get("id") instanceof Integer ? Long.valueOf((Integer) content.get("id")) : (Long) content.get("id");


        // Build SimDTO from response.content
        SimDTO simDTO = SimDTO.builder()
                .simMicroserviceId(simMicroserviceId)
                .phone((String) content.get("phone"))
                .operatorName((String) content.get("operatorName"))
                .ccid((String) content.get("ccid"))
                .build();

        System.out.println(response.getContent());

        // Return the sim DTO
        return simDTO;
    }

}
