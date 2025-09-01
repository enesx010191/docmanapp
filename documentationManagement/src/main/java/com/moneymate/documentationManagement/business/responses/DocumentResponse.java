package com.moneymate.documentationManagement.business.responses;

import com.moneymate.documentationManagement.entities.concretes.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentResponse extends BaseResponse {
    private Document document;
    
    public DocumentResponse(boolean success, String message, Document document) {
        super(success, message);
        this.document = document;
    }
    
    // Başarılı işlem için static factory method
    public static DocumentResponse success(String message, Document document) {
        return new DocumentResponse(true, message, document);
    }
    
    // Hatalı işlem için static factory method
    public static DocumentResponse error(String message) {
        return new DocumentResponse(false, message, null);
    }
}