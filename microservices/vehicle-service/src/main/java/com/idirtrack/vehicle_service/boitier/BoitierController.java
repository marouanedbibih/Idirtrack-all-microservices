package com.idirtrack.vehicle_service.boitier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.boitier.https.BoitierRequest;
import com.idirtrack.vehicle_service.utils.ValidationUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.idirtrack.vehicle_service.utils.Error;

@RestController
@RequestMapping("/vehicle-api/boitier")
@RequiredArgsConstructor
public class BoitierController {

    @Autowired
    private BoitierService boitierService;

    @PostMapping("/")
    public ResponseEntity<BasicResponse> createBoitier(@Valid @RequestBody BoitierRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            List<Error> errors = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                String field = error.getField();

                // Filter errors for device, sim, startDate, and endDate
                if (field.equals("deviceMicroserviceId")) {
                    errors.add(Error.builder().key("device").message(error.getDefaultMessage()).build());
                } else if (field.equals("simMicroserviceId")) {
                    errors.add(Error.builder().key("sim").message(error.getDefaultMessage()).build());
                } else if (field.equals("startDate")) {
                    errors.add(Error.builder().key("dateStart").message(error.getDefaultMessage()).build());
                } else if (field.equals("endDate")) {
                    errors.add(Error.builder().key("dateEnd").message(error.getDefaultMessage()).build());
                }
            }

            BasicResponse response = BasicResponse.builder()
                    .errorsList(errors)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            BasicResponse response = boitierService.createNewBoitier(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BasicException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getResponse());
        }
    }

    @GetMapping("/")
    public ResponseEntity<BasicResponse> getAllBoitiers(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = boitierService.getAllBoitiers(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    /**
     * GET BOITIER BY ID
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/")
    public ResponseEntity<BasicResponse> getBoitierById(@PathVariable Long id) {


        // Try to get the boitier by id
        try {
            BasicResponse response = boitierService.getBoitierById(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        // Catch a BasicException if the boitier is not found
        catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        }
        // Catch any other exceptions
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BasicResponse.builder().message("Internal Server Error").build());
        }
    }

    /**
     * GET LIST OF BOITIERS NOT ASSOCIATED WITH A VEHICLE
     * 
     * @param page
     * @param size
     * @return ResponseEntity<BasicResponse>
     */

    @GetMapping("/unassigned/")
    public ResponseEntity<BasicResponse> getUnassignedBoitiers(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        // Try to get the list of unassigned boitiers
        try {
            BasicResponse response = boitierService.getUnassignedBoitiers(page, size);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        // Catch any BasicExceptions
        catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        }
    }

    /**
     * DELETE BOITIER BY ID
     * 
     * This endpoint deletes a boitier by id
     * @param id
     * @return ResponseEntity<BasicResponse>
     */

    @DeleteMapping("/{id}/")
    public ResponseEntity<BasicResponse> deleteBoitierById(@PathVariable Long id,@RequestParam boolean isLost) {
        // Try to delete the boitier by id
        try {
            BasicResponse response = boitierService.deleteBoitierById(id,isLost);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        // Catch any BasicExceptions
        catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        }
    }

    //update boitier by id
    @PutMapping("/{id}/")
    public ResponseEntity<BasicResponse> updateBoitierById(@PathVariable Long id, @Valid @RequestBody BoitierRequest request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            List<Error> errors = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                String field = error.getField();

                // Filter errors for device, sim, startDate, and endDate
                if (field.equals("deviceMicroserviceId")) {
                    errors.add(Error.builder().key("device").message(error.getDefaultMessage()).build());
                } else if (field.equals("simMicroserviceId")) {
                    errors.add(Error.builder().key("sim").message(error.getDefaultMessage()).build());
                } else if (field.equals("startDate")) {
                    errors.add(Error.builder().key("dateStart").message(error.getDefaultMessage()).build());
                } else if (field.equals("endDate")) {
                    errors.add(Error.builder().key("dateEnd").message(error.getDefaultMessage()).build());
                }
            }

            BasicResponse response = BasicResponse.builder()
                    .errorsList(errors)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            BasicResponse response = boitierService.updateBoitierById(id,request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BasicException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getResponse());
        }
    }
}
