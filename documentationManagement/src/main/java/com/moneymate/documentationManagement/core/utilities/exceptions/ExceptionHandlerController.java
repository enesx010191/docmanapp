package com.moneymate.documentationManagement.core.utilities.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorDataResult;



@RestControllerAdvice
public class ExceptionHandlerController {
	
    // 🔴 Validation (javax/jakarta.validation) hataları
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDataResult<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErrorDataResult<Object> errorResult = new ErrorDataResult<>(validationErrors, "VALIDATION_ERROR");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }

    // 🔴 Business hataları (senin özel tanımların)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDataResult<Object>> handleBusinessException(BusinessException ex) {
        ErrorDataResult<Object> errorResult = new ErrorDataResult<>(ex.getMessage(), "BUSINESS_ERROR");
        return ResponseEntity.status(ex.getStatus()).body(errorResult);
    }

    // 🔴 Veritabanı unique constraint veya foreign key violation gibi hatalar
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDataResult<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorDataResult<Object> errorResult = new ErrorDataResult<>(ex.getMessage(), "DATA_INTEGRITY_ERROR");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResult);
    }

    // 🔴 Diğer yakalanmamış tüm hatalar
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDataResult<Object>> handleGeneralException(Exception ex) {
        ErrorDataResult<Object> errorResult = new ErrorDataResult<>("Bilinmeyen bir hata oluştu: " + ex.getMessage(), "INTERNAL_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason()));
    }
}

