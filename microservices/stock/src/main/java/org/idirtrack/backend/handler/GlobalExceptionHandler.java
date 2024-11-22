package org.idirtrack.backend.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<MyErrorRES> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<MyFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        MyErrorRES errorResponse = MyErrorRES.builder()
                .errors(errors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Helper method to map field errors
    private MyFieldError mapFieldError(FieldError fieldError) {
        return MyFieldError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }

}