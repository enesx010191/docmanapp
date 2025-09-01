package com.moneymate.documentationManagement.business.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    private boolean success;
    private String message;
    
    public static BaseResponse success(String message) {
        return new BaseResponse(true, message);
    }
    
    public static BaseResponse error(String message) {
        return new BaseResponse(false, message);
    }
}