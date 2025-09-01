package com.moneymate.documentationManagement.business.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Login yanıt modeli")
public class LoginUserRes {
    
    private String firstName;
    private String lastName;   
    private Integer httpCode;   
    private Boolean status;  
    private String token;
    
    
    public LoginUserRes(String token) {
        this.token = token;
        this.httpCode = 200;
        this.status = true;
    }
    
    public LoginUserRes(int httpCode) {
        this.httpCode = httpCode;
        this.status = false;
    }
}