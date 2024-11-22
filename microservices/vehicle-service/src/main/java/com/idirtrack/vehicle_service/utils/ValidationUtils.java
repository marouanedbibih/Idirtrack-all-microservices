package com.idirtrack.vehicle_service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationUtils {

    public static Map<String, String> getValidationsErrors(BindingResult bindingResult) {
        Map<String, String> errorsMap = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                if (!errorsMap.containsKey(error.getField())) {
                    errorsMap.put(error.getField(), error.getDefaultMessage());
                }
            }
        }

        return errorsMap;
    }

    /**
     * Extarct the error message from the BindingResult
     * and return List of Error objects
     */
    public static List<Error> extractErrorsFromBindingResult(BindingResult bindingResult) {
        // Declare a list of Error instances
        List<Error> errors = new ArrayList<>();

        // If there are errors in the BindingResult
        if (bindingResult.hasErrors()) {
            // Iterate over the FieldErrors
            for (FieldError error : bindingResult.getFieldErrors()) {
                // Add a new Error instance to the list
                errors.add(Error.builder()
                        .key(error.getField())
                        .message(error.getDefaultMessage())
                        .build());
            }
        }

        // Return the list of Error instances
        return errors;
    }
}
