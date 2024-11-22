package com.idirtrack.vehicle_service.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationUtil {
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

    
}
