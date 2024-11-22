package com.idirtrack.vehicle_service.boitier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.boitier.https.BoitierRequest;
import com.idirtrack.vehicle_service.utils.ValidationUtil;

@WebMvcTest(BoitierController.class)
public class BoitierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoitierService boitierService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoitierRequest validRequest;

    // @BeforeEach
    // public void setup() {
    //     validRequest = new BoitierRequest();
    //     validRequest.setDeviceMicroserviceId((long) 123);
    //     validRequest.setImei((int) 123456789L);
    //     validRequest.setDeviceType("F9G");
    //     validRequest.setSimMicroserviceId((long) 5678);
    //     validRequest.setPhoneNumber("0615554445");
    //     validRequest.setDateStart("20/06/2024");
    //     validRequest.setDateFin("20/06/2025");
    // }

    // @Test
    // public void testCreateBoitierWithValidRequest() throws Exception {
    //     BasicResponse response = BasicResponse.builder().message("Boitier created successfully").status(HttpStatus.OK).build();
    //     when(boitierService.createBoitier(any(BoitierRequest.class))).thenReturn(response);

    //     mockMvc.perform(post("/api/boitier/")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(validRequest)))
    //             .andExpect(status().isOk());
    // }

    // @Test
    // public void testCreateBoitierWithInvalidRequest() throws Exception {
    //     BoitierRequest invalidRequest = new BoitierRequest();
    //     // Set invalid fields, e.g., missing deviceMicroserviceId
    //     invalidRequest.setImei((int) 123456789L);
    //     invalidRequest.setDeviceType("F9G");
    //     invalidRequest.setSimMicroserviceId((long) 5678);
    //     invalidRequest.setPhoneNumber("0615554445");
    //     invalidRequest.setDateStart("20/06/2024");
    //     invalidRequest.setDateFin("20/06/2025");

    //     mockMvc.perform(post("/api/boitier/")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // public void testCreateBoitierWithValidationErrors() throws Exception {
    //     BoitierRequest invalidRequest = new BoitierRequest();
    //     // Set invalid fields, e.g., invalid phone number
    //     invalidRequest.setDeviceMicroserviceId((long) 1234);
    //     invalidRequest.setImei((int) 123456789L);
    //     invalidRequest.setDeviceType("F9G");
    //     invalidRequest.setSimMicroserviceId((long)5678);
    //     invalidRequest.setPhoneNumber("12345");
    //     invalidRequest.setDateStart("20/06/2024");
    //     invalidRequest.setDateFin("20/06/2025");

    //     mockMvc.perform(post("/api/boitier/")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(invalidRequest)))
    //             .andExpect(status().isBadRequest());
    // }
}
