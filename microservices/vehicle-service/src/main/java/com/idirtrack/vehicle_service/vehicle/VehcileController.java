package com.idirtrack.vehicle_service.vehicle;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.vehicle_service.basic.BasicException;
import com.idirtrack.vehicle_service.basic.BasicResponse;
import com.idirtrack.vehicle_service.utils.Error;
import com.idirtrack.vehicle_service.utils.ValidationUtil;
import com.idirtrack.vehicle_service.utils.ValidationUtils;
import com.idirtrack.vehicle_service.vehicle.https.VehicleRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/vehicle-api/vehicles")
public class VehcileController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/")
    public ResponseEntity<BasicResponse> createNewVehicle(@Valid @RequestBody VehicleRequest request,
            BindingResult bindingResult) {
        // Validate the request body
        if (bindingResult.hasErrors()) {
            List<Error> errors = ValidationUtils.extractErrorsFromBindingResult(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BasicResponse.builder()
                    .message("Validation Error")
                    .errorsList(errors)
                    .build());
        }
        // If the request is valid
        else {
            // Try to create the new vehicle
            try {
                BasicResponse response = vehicleService.createNewVehicle(request);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            // Catch and handle BasicException
            catch (BasicException e) {
                return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
            }
            // Catch and handle any other exception
            catch (Exception e) {
                System.out.println(e);
                BasicResponse response = BasicResponse.builder()
                        .message("Internal Server Error")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

    }

    /**
     * Retrieves a paginated list of vehicles.
     * 
     * This endpoint handles GET requests to retrieve vehicles with pagination. It
     * accepts
     * the page number and page size as query parameters, with default values
     * provided. The method
     * invokes the service layer to get the vehicles and returns a ResponseEntity
     * with the
     * appropriate HTTP status and response body.
     * 
     * The endpoint performs the following actions:
     * 1. Calls the {@link VehicleService#getAllVehicles(int, int)} method with the
     * specified
     * page number and page size.
     * 2. Returns a ResponseEntity with the status and body from the service
     * response.
     * 3. Catches and handles {@link BasicException} by returning a ResponseEntity
     * with the error status and message from the exception response.
     * 4. Catches any other exceptions and returns a ResponseEntity with an internal
     * server
     * error status and a generic error message.
     * 
     * @param page the page number to retrieve (default is 1)
     * @param size the number of vehicles per page (default is 5)
     * @return a ResponseEntity containing the BasicResponse with the list of
     *         vehicles,
     *         pagination metadata, and status
     */
    @GetMapping("/")
    public ResponseEntity<BasicResponse> getAllVehicles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            BasicResponse response = vehicleService.getAllVehicles(page, size);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            BasicResponse response = BasicResponse.builder()
                    .message("Internal Server Error")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieves a vehicle by ID.
     * 
     * This endpoint handles GET requests to retrieve a vehicle by ID. It accepts the
     * vehicle ID as a path variable and returns a ResponseEntity with the appropriate
     * HTTP status and response body.
     * 
     * @param vehicleId
     * @return a ResponseEntity containing the BasicResponse with the vehicle details
     */
    @GetMapping("/{vehicleId}/")
    public ResponseEntity<BasicResponse> getVehicleById(@PathVariable Long vehicleId) {
        try {
            BasicResponse response = vehicleService.getVehicleById(vehicleId);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            BasicResponse response = BasicResponse.builder()
                    .message("Internal Server Error")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    /**
     * Retrieves boities of a vehicle.
     * 
     * This endpoint handles GET requests to retrieve boities of a vehicle. It
     * accepts the vehicle ID as a path variable and returns a ResponseEntity with
     * the appropriate HTTP status and response body.
     * 
     * @param vehicleId the ID of the vehicle to retrieve
     * @return a ResponseEntity containing the BasicResponse with the vehicle
     *         details and status
     */

    @GetMapping("/{vehicleId}/boities/")
    public ResponseEntity<BasicResponse> getVehicleBoities(@PathVariable Long vehicleId) {
        // Try to get the vehicle boities
        try {
            BasicResponse response = vehicleService.getVehicleBoities(vehicleId);
            return ResponseEntity.status(response.getStatus()).body(response);
        } 
        // Catch and handle BasicException
        catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } 
        // Catch and handle any other exception
        catch (Exception e) {
            BasicResponse response = BasicResponse.builder()
                    .message("Internal Server Error")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
