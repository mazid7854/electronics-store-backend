package com.mazid.electronic.store.exceptions;

import com.mazid.electronic.store.utility.ApiResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //resource not found exception handler
    private Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        logger.info("resource not found exception handler called");
        ApiResponseMessage message= ApiResponseMessage.builder().message(ex.getMessage()).status(HttpStatus.NOT_FOUND).success(false).build();
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }

    //MethodArgumentNotValidException handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        Map<String,Object> response= new HashMap<>();
        allErrors.forEach(ObjectError -> {
            String fieldName = ((org.springframework.validation.FieldError) ObjectError).getField();
            String errorMessage=ObjectError.getDefaultMessage();
            response.put(fieldName,errorMessage);
        });
        return new  ResponseEntity<>(response,HttpStatus.BAD_REQUEST);



    }



    //BadApiRequestException exception handler

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<ApiResponseMessage> badApiRequestExceptionHandler(BadApiRequestException ex) {

        ApiResponseMessage message= ApiResponseMessage.builder().message(ex.getMessage()).status(HttpStatus.BAD_REQUEST).success(false).build();
        return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
    }

}
