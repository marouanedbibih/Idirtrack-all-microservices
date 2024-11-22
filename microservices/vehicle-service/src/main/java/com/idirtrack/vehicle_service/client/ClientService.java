package com.idirtrack.vehicle_service.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.vehicle.https.VehicleRequest;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    /**
     * Check if a client exists in the user microservice
     * 
     * @param id
     * @param clientName
     * @param companyName
     * @return Boolean
     */
    public Boolean isExistInUserMicroservice(Long id, String clientName, String companyName) {
        // Construct the URI with query parameters
        String uri = String.format("/exist-for-create-vehicle/?clientId=%d&clientName=%s&companyName=%s",
                id, clientName, companyName);

        // Send request to user microservice to check if the client exists
        BasicResponse response = webClientBuilder.build()
                .get()
                .uri("http://user-service/user-api/clients" + uri)
                .retrieve()
                .bodyToMono(BasicResponse.class)
                .block();

        Boolean exist = (Boolean) response.getContent();

        return exist;
    }

    /**
     * Save client in database from Vehicle Request
     * 
     * @param request
     * @return Client
     */
    // public Client saveClient(VehicleRequest request) throws Exception {
    // try {
    // Client client = Client.builder()
    // .clientMicroserviceId(request.getClientMicroserviceId())
    // .name(StringUtils.capitalize(request.getClientName()))
    // .company(request.getClientCompany())
    // .build();
    // return clientRepository.save(client);
    // } catch (Exception e) {
    // throw new Exception("Error while saving client");
    // }

    // }

    /**
     * GET CLIENT BY ID FROM USER MICROSERVICE
     * 
     */

    public ClientDTO getClientFormUserMicroservice(Long id) {
        try {
            // Construct the URI with query parameters
            String uri = String.format("/%d/", id);

            // Send request to user microservice to check if the client exists
            BasicResponse response = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/user-api/clients" + uri)
                    .retrieve()
                    .bodyToMono(BasicResponse.class)
                    .block();

            if (response == null || response.getContent() == null) {
                throw new NullPointerException("Response or response content is null");
            }

            // Cast the content to a Map
            Map<String, Object> content = (Map<String, Object>) response.getContent();

            // Log the response content for debugging
            System.out.println("Response content: " + content);

            // Check if required fields are present in the content map
            Long clientMicroserviceId = content.get("id") instanceof Integer
                    ? ((Integer) content.get("id")).longValue()
                    : (Long) content.get("id");

            // Build the client DTO object
            ClientDTO client = ClientDTO.builder()
                    .clientMicroserviceId(clientMicroserviceId)
                    .name((String) content.get("name"))
                    .company((String) content.get("company"))
                    .build();

            System.out.println(response.getContent());

            // Return the client DTO object
            return client;
        } catch (WebClientResponseException e) {
            // Handle the exception and return a fallback response
            // Log the error details
            System.err.println("Error occurred while calling user-service: " + e.getMessage());
            // return fallbackClientDTO(id);
            return null;
        } catch (NullPointerException e) {
            // Handle the null pointer exception and log the error
            System.err.println("Error processing response: " + e.getMessage());
            // return fallbackClientDTO(id);
            return null;
        }
    }

    public Client saveClient(ClientDTO clientDTO) {
        Client client = Client.builder()
                .clientMicroserviceId(clientDTO.getClientMicroserviceId())
                .name(clientDTO.getName())
                .company(clientDTO.getCompany())
                .build();
        return clientRepository.save(client);
    }

}
