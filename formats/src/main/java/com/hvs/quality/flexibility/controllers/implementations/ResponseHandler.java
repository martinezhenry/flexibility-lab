package com.hvs.quality.flexibility.controllers.implementations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class ResponseHandler {


    @ExceptionHandler(SchemaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleConflict(RuntimeException ex) {
        var responseBody = "{ \"msg\": \"schema not found\" }";
        return responseBody;
    }

}
