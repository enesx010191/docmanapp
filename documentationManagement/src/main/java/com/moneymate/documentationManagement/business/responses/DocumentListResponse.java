package com.moneymate.documentationManagement.business.responses;

import com.moneymate.documentationManagement.entities.concretes.Document;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentListResponse {
    private List<Document> documents;
    private int totalCount;
    
    public DocumentListResponse(List<Document> documents) {
        this.documents = documents;
        this.totalCount = documents != null ? documents.size() : 0;
    }
    
    // Static factory methods
    public static DocumentListResponse of(List<Document> documents) {
        return new DocumentListResponse(documents);
    }
    
    public static DocumentListResponse empty() {
        return new DocumentListResponse(List.of(), 0);
    }
}