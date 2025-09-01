package com.moneymate.documentationManagement.core.utilities.mappers;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.moneymate.documentationManagement.entities.concretes.Document;
import com.moneymate.documentationManagement.business.requests.DocumentUpdateRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentMapperUtil {

    private final ModelMapper modelMapper;

    /**
     * Mevcut dökümanın field'larını günceller
     * @param existingDoc Güncellenecek döküman
     * @param newDoc Yeni değerler içeren döküman
     */
    public void updateDocumentFields(Document existingDoc, DocumentUpdateRequest newDoc) {
        if (existingDoc == null || newDoc == null) {
            return;
        }
        
        // ModelMapper ile field'ları kopyala (ID ve timestamp'ler hariç)
        
        System.out.println("New Doc:  "+newDoc);
        System.out.println("Eski Doc:  "+existingDoc);
        modelMapper.map(newDoc, existingDoc);
        existingDoc.setUpdateAt(LocalDateTime.now());
    }


    /**
     * Döküman için timestamp'leri set eder
     * @param document Döküman entity
     */
    public void setCreationTimestamps(Document document) {
        if (document == null) {
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        document.setUploadAt(now);
        document.setUpdateAt(now);
    }

    /**
     * Döküman için sadece update timestamp'ini set eder
     * @param document Döküman entity
     */
    public void setUpdateTimestamp(Document document) {
        if (document == null) {
            return;
        }
        
        document.setUpdateAt(LocalDateTime.now());
    }
}