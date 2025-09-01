package com.moneymate.documentationManagement.business.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterUserRes {
    private String message;
    private boolean status;
    private int httpCode;
    
    // Başarılı kayıt için constructor
    public RegisterUserRes(String message) {
        this.message = message;
        this.status = true;
        this.httpCode = 201; // Created
    }
    
    // Hatalı kayıt için constructor
    public RegisterUserRes(String message, int httpCode) {
        this.message = message;
        this.status = false;
        this.httpCode = httpCode;
    }
}