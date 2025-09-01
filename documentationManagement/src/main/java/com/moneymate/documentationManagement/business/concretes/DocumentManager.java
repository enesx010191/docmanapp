package com.moneymate.documentationManagement.business.concretes;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moneymate.documentationManagement.business.abstracts.DocumentService;
import com.moneymate.documentationManagement.business.requests.DocumentUpdateRequest;
import com.moneymate.documentationManagement.core.utilities.Messages;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessResult;
import com.moneymate.documentationManagement.core.utilities.mappers.DocumentMapperUtil;
import com.moneymate.documentationManagement.dataAccess.abstracts.DocumentRepository;
import com.moneymate.documentationManagement.entities.concretes.Document;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentManager implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapperUtil documentMapperUtil;

    @Override
    public DataResult<Document> saveDocument(Document document) {
    	documentMapperUtil.setCreationTimestamps(document);
        Document savedDocument = documentRepository.save(document);
        return new SuccessDataResult<>(savedDocument, Messages.DocumentAdded);
    }

    @Override
    public DataResult<Optional<Document>> getDocumentById(Long id) {
        Optional<Document> result = documentRepository.findById(id);
        if (result.isPresent()) {
            return new SuccessDataResult<>(result, Messages.GetByIdDocument);
        }
        return new ErrorDataResult<>(Messages.DocumentsFindFailed);
    }

    @Override
    public DataResult<List<Document>> getAllDocuments() {
        List<Document> documents = documentRepository.findAll();
        return new SuccessDataResult<>(documents, Messages.GetByAllDocument);
    }

    @Override
    public DataResult<List<Document>> getDocumentsByInstitution(String institutionName) {
        List<Document> documents = documentRepository.findByInstitutionName(institutionName);
        return new SuccessDataResult<>(documents, Messages.DocumentsGetSuccess);
    }

    @Override
    public DataResult<List<Document>> getDocumentsByType(String documentType) {
        List<Document> documents = documentRepository.findByDocumentType(documentType);
        return new SuccessDataResult<>(documents, Messages.DocumentsGetSuccess);
    }

    @Override
    public DataResult<List<Document>> searchDocumentsByTitle(String title) {
        List<Document> documents = documentRepository.findByTitleContainingIgnoreCase(title);
        return new SuccessDataResult<>(documents, Messages.DocumentsGetSuccess);
    }

    @Override
    public Result deleteDocument(Long id) {
        Optional<Document> existingDoc = documentRepository.findById(id);
        if (existingDoc.isPresent()) {
            documentRepository.deleteById(id);
            return new SuccessResult(Messages.DocumentsDeletedSuccess);
        }
        return new ErrorResult(Messages.DocumentsDeletedFailed);
    }

    @Override
    public DataResult<Document> updateDocument(Long id, DocumentUpdateRequest document) {
    	
        Optional<Document> existingDoc = documentRepository.findById(id);
        if (existingDoc.isPresent()) {
            Document doc = existingDoc.get();
            documentMapperUtil.updateDocumentFields(doc, document);
            Document updatedDocument = documentRepository.save(doc);
            return new SuccessDataResult<>(updatedDocument, Messages.DocumentUpdateSuccess);
        }
        return new ErrorDataResult<>(Messages.DocumentsFindFailed);
    }
}