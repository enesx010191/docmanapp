package com.moneymate.documentationManagement.webApi.controllers;

import static com.moneymate.documentationManagement.core.utilities.exceptions.ResponseEntityBuilder.fromDataResult;
import static com.moneymate.documentationManagement.core.utilities.exceptions.ResponseEntityBuilder.fromResult;

import java.net.URL;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moneymate.documentationManagement.business.abstracts.DocumentService;
import com.moneymate.documentationManagement.business.abstracts.FileUploadService;
import com.moneymate.documentationManagement.business.requests.DocumentUpdateRequest;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorDataResult;
import com.moneymate.documentationManagement.entities.concretes.Document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin(origins = "*")
@AllArgsConstructor
@Tag(name = "Document Management", description = "Döküman yönetimi API'leri")
public class DocumentApi {
    
    private final DocumentService documentService;
    private final FileUploadService fileUploadService;

    @GetMapping("/")
    @Operation(summary = "Tüm dökümanları listele")
    public ResponseEntity<?> getAllDocuments() {
        var documents = documentService.getAllDocuments();
        return fromDataResult(documents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID'ye göre döküman getir")
    public ResponseEntity<?> getDocumentById(@PathVariable Long id) {
        var document = documentService.getDocumentById(id);
        return fromDataResult(document);
    }

    @PostMapping("/")
    @Operation(summary = "Yeni döküman yükle")
    public ResponseEntity<?> uploadFileWithMetadata(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("institutionName") String institutionName,
            @RequestParam("institutionType") String institutionType,
            @RequestParam("institutionUrl") String institutionUrl,
            @RequestParam("documentType") String documentType,
            @RequestParam("documentDescription") String documentDescription) {

        if (file.isEmpty()) {
            DataResult<Document> errorResult = new ErrorDataResult<>("Dosya boş olamaz");
            return fromDataResult(errorResult);
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            DataResult<Document> errorResult = new ErrorDataResult<>("Dosya boyutu 10MB'dan büyük olamaz");
            return fromDataResult(errorResult);
        }

        DataResult<Document> result = fileUploadService.uploadFileWithMetadata(
                file, title, institutionName, institutionType, 
                institutionUrl, documentType, documentDescription
        );

        return fromDataResult(result);
    }

    @GetMapping("/institution/{institutionName}")
    @Operation(summary = "Kuruma göre dökümanları getir")
    public ResponseEntity<?> getDocumentsByInstitution(@PathVariable String institutionName) {
        var documents = documentService.getDocumentsByInstitution(institutionName);
        return fromDataResult(documents);
    }

    @GetMapping("/type/{documentType}")
    @Operation(summary = "Döküman tipine göre getir")
    public ResponseEntity<?> getDocumentsByType(@PathVariable String documentType) {
        var documents = documentService.getDocumentsByType(documentType);
        return fromDataResult(documents);
    }

    @GetMapping("/search")
    @Operation(summary = "Döküman ara")
    public ResponseEntity<?> searchDocuments(@RequestParam String title) {
        var documents = documentService.searchDocumentsByTitle(title);
        return fromDataResult(documents);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Döküman güncelle")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody DocumentUpdateRequest request) {
        var result = documentService.updateDocument(id, request);
        return fromDataResult(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Döküman sil")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        DataResult<Optional<Document>> documentResult = documentService.getDocumentById(id);
        if (!documentResult.isSuccess()) {
            return fromDataResult(documentResult);
        }
        
        Optional<Document> document = documentResult.getData();
        String fileName = document.get().getMinioFileName();
        
        fileUploadService.deleteFile(fileName);
        var result = documentService.deleteDocument(id);
        return fromResult(result);
    }
}