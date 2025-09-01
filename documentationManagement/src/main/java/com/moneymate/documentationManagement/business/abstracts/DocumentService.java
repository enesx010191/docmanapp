package com.moneymate.documentationManagement.business.abstracts;

import java.util.List;
import java.util.Optional;

import com.moneymate.documentationManagement.business.requests.DocumentUpdateRequest;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.entities.concretes.Document;

public interface DocumentService {
    DataResult<Document> saveDocument(Document document);
    DataResult<Optional<Document>>  getDocumentById(Long id);
    DataResult<List<Document>> getAllDocuments();
    DataResult<List<Document>>  getDocumentsByInstitution(String institutionName);
    DataResult<List<Document>>  getDocumentsByType(String documentType);
    DataResult<List<Document>>  searchDocumentsByTitle(String title);
    Result deleteDocument(Long id);
    DataResult<Document> updateDocument(Long id, DocumentUpdateRequest document);
}
