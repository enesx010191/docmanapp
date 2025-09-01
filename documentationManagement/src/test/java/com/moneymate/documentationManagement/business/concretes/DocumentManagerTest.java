package com.moneymate.documentationManagement.business.concretes;

// import com.moneymate.documentationManagement.business.concretes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moneymate.documentationManagement.business.requests.DocumentUpdateRequest;
import com.moneymate.documentationManagement.core.utilities.Messages;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.core.utilities.mappers.DocumentMapperUtil;
import com.moneymate.documentationManagement.dataAccess.abstracts.DocumentRepository;
import com.moneymate.documentationManagement.entities.concretes.Document;

@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentManager Service Tests")
class DocumentManagerTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapperUtil documentMapperUtil;

    @InjectMocks
    private DocumentManager documentManager;

    private Document testDocument;
    private DocumentUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testDocument = Document.builder()
                .id(1L)
                .title("Test Document")
                .institutionName("Test University")
                .institutionType("University")
                .institutionUrl("https://test.edu")
                .documentType("PDF")
                .documentDescription("Test description")
                .fileSize("1MB")
                .fileUrl("https://example.com/test.pdf")
                .uploadAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .updateAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

        updateRequest = new DocumentUpdateRequest();
        // Assuming DocumentUpdateRequest has these fields
        // You may need to adjust based on actual implementation
    }

    @Nested
    @DisplayName("Save Document Tests")
    class SaveDocumentTests {

        @Test
        @DisplayName("Should save document successfully")
        void shouldSaveDocumentSuccessfully() {
            // Given
            Document documentToSave = Document.builder()
                    .title("New Document")
                    .institutionName("New University")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://example.com/new.pdf")
                    .build();

            Document savedDocument = Document.builder()
                    .id(1L)
                    .title("New Document")
                    .institutionName("New University")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://example.com/new.pdf")
                    .uploadAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();

            when(documentRepository.save(any(Document.class))).thenReturn(savedDocument);

            // When
            DataResult<Document> result = documentManager.saveDocument(documentToSave);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(savedDocument, result.getData());
            assertEquals(Messages.DocumentAdded, result.getMessage());

            verify(documentMapperUtil).setCreationTimestamps(documentToSave);
            verify(documentRepository).save(documentToSave);
        }

        @Test
        @DisplayName("Should handle null document when saving")
        void shouldHandleNullDocumentWhenSaving() {
            // Given
            Document nullDocument = null;

            // When
            DataResult<Document> result = documentManager.saveDocument(nullDocument);

            // Then
            assertTrue(result.isSuccess()); // Service doesn't validate null, repository might handle it
            verify(documentMapperUtil).setCreationTimestamps(nullDocument);
            verify(documentRepository).save(nullDocument);
        }
    }

    @Nested
    @DisplayName("Get Document By Id Tests")
    class GetDocumentByIdTests {

        @Test
        @DisplayName("Should get document by id successfully when document exists")
        void shouldGetDocumentByIdSuccessfullyWhenExists() {
            // Given
            Long documentId = 1L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.of(testDocument));

            // When
            DataResult<Optional<Document>> result = documentManager.getDocumentById(documentId);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertTrue(result.getData().isPresent());
            assertEquals(testDocument, result.getData().get());
            assertEquals(Messages.GetByIdDocument, result.getMessage());

            verify(documentRepository).findById(documentId);
        }

        @Test
        @DisplayName("Should return error when document does not exist")
        void shouldReturnErrorWhenDocumentDoesNotExist() {
            // Given
            Long documentId = 999L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

            // When
            DataResult<Optional<Document>> result = documentManager.getDocumentById(documentId);

            // Then
            assertFalse(result.isSuccess());
            assertEquals(Messages.DocumentsFindFailed, result.getMessage());

            verify(documentRepository).findById(documentId);
        }

        @Test
        @DisplayName("Should handle null id parameter")
        void shouldHandleNullIdParameter() {
            // Given
            Long nullId = null;
            when(documentRepository.findById(nullId)).thenReturn(Optional.empty());

            // When
            DataResult<Optional<Document>> result = documentManager.getDocumentById(nullId);

            // Then
            assertFalse(result.isSuccess());
            assertEquals(Messages.DocumentsFindFailed, result.getMessage());

            verify(documentRepository).findById(nullId);
        }
    }

    @Nested
    @DisplayName("Get All Documents Tests")
    class GetAllDocumentsTests {

        @Test
        @DisplayName("Should get all documents successfully when documents exist")
        void shouldGetAllDocumentsSuccessfullyWhenExist() {
            // Given
            Document document2 = Document.builder()
                    .id(2L)
                    .title("Another Document")
                    .institutionName("Another University")
                    .institutionType("College")
                    .documentType("DOCX")
                    .fileUrl("https://example.com/another.docs")
                    .build();

            List<Document> documents = Arrays.asList(testDocument, document2);
            when(documentRepository.findAll()).thenReturn(documents);

            // When
            DataResult<List<Document>> result = documentManager.getAllDocuments();

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());
            assertEquals(documents, result.getData());
            assertEquals(Messages.GetByAllDocument, result.getMessage());

            verify(documentRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no documents exist")
        void shouldReturnEmptyListWhenNoDocumentsExist() {
            // Given
            List<Document> emptyList = Collections.emptyList();
            when(documentRepository.findAll()).thenReturn(emptyList);

            // When
            DataResult<List<Document>> result = documentManager.getAllDocuments();

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
            assertEquals(Messages.GetByAllDocument, result.getMessage());

            verify(documentRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Get Documents By Institution Tests")
    class GetDocumentsByInstitutionTests {

        @Test
        @DisplayName("Should get documents by institution name successfully")
        void shouldGetDocumentsByInstitutionNameSuccessfully() {
            // Given
            String institutionName = "Test University";
            List<Document> documents = Arrays.asList(testDocument);
            when(documentRepository.findByInstitutionName(institutionName)).thenReturn(documents);

            // When
            DataResult<List<Document>> result = documentManager.getDocumentsByInstitution(institutionName);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals(testDocument, result.getData().get(0));
            assertEquals(Messages.DocumentsGetSuccess, result.getMessage());

            verify(documentRepository).findByInstitutionName(institutionName);
        }

        @Test
        @DisplayName("Should return empty list when no documents found for institution")
        void shouldReturnEmptyListWhenNoDocumentsForInstitution() {
            // Given
            String institutionName = "Nonexistent University";
            List<Document> emptyList = Collections.emptyList();
            when(documentRepository.findByInstitutionName(institutionName)).thenReturn(emptyList);

            // When
            DataResult<List<Document>> result = documentManager.getDocumentsByInstitution(institutionName);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
            assertEquals(Messages.DocumentsGetSuccess, result.getMessage());

            verify(documentRepository).findByInstitutionName(institutionName);
        }

        @Test
        @DisplayName("Should handle null institution name")
        void shouldHandleNullInstitutionName() {
            // Given
            String nullInstitutionName = null;
            List<Document> emptyList = Collections.emptyList();
            when(documentRepository.findByInstitutionName(nullInstitutionName)).thenReturn(emptyList);

            // When
            DataResult<List<Document>> result = documentManager.getDocumentsByInstitution(nullInstitutionName);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());

            verify(documentRepository).findByInstitutionName(nullInstitutionName);
        }
    }

    @Nested
    @DisplayName("Get Documents By Type Tests")
    class GetDocumentsByTypeTests {

        @Test
        @DisplayName("Should get documents by document type successfully")
        void shouldGetDocumentsByDocumentTypeSuccessfully() {
            // Given
            String documentType = "PDF";
            List<Document> documents = Arrays.asList(testDocument);
            when(documentRepository.findByDocumentType(documentType)).thenReturn(documents);

            // When
            DataResult<List<Document>> result = documentManager.getDocumentsByType(documentType);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals(testDocument, result.getData().get(0));
            assertEquals(Messages.DocumentsGetSuccess, result.getMessage());

            verify(documentRepository).findByDocumentType(documentType);
        }

        @Test
        @DisplayName("Should return empty list when no documents found for type")
        void shouldReturnEmptyListWhenNoDocumentsForType() {
            // Given
            String documentType = "XLS";
            List<Document> emptyList = Collections.emptyList();
            when(documentRepository.findByDocumentType(documentType)).thenReturn(emptyList);

            // When
            DataResult<List<Document>> result = documentManager.getDocumentsByType(documentType);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
            assertEquals(Messages.DocumentsGetSuccess, result.getMessage());

            verify(documentRepository).findByDocumentType(documentType);
        }
    }

    @Nested
    @DisplayName("Search Documents By Title Tests")
    class SearchDocumentsByTitleTests {

        @Test
        @DisplayName("Should search documents by title successfully")
        void shouldSearchDocumentsByTitleSuccessfully() {
            // Given
            String searchTitle = "test";
            List<Document> documents = Arrays.asList(testDocument);
            when(documentRepository.findByTitleContainingIgnoreCase(searchTitle)).thenReturn(documents);

            // When
            DataResult<List<Document>> result = documentManager.searchDocumentsByTitle(searchTitle);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());
            assertEquals(testDocument, result.getData().get(0));
            assertEquals(Messages.DocumentsGetSuccess, result.getMessage());

            verify(documentRepository).findByTitleContainingIgnoreCase(searchTitle);
        }

        @Test
        @DisplayName("Should return empty list when no matching titles found")
        void shouldReturnEmptyListWhenNoMatchingTitlesFound() {
            // Given
            String searchTitle = "nonexistent";
            List<Document> emptyList = Collections.emptyList();
            when(documentRepository.findByTitleContainingIgnoreCase(searchTitle)).thenReturn(emptyList);

            // When
            DataResult<List<Document>> result = documentManager.searchDocumentsByTitle(searchTitle);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
            assertEquals(Messages.DocumentsGetSuccess, result.getMessage());

            verify(documentRepository).findByTitleContainingIgnoreCase(searchTitle);
        }

        @Test
        @DisplayName("Should handle empty search title")
        void shouldHandleEmptySearchTitle() {
            // Given
            String emptyTitle = "";
            List<Document> allDocuments = Arrays.asList(testDocument);
            when(documentRepository.findByTitleContainingIgnoreCase(emptyTitle)).thenReturn(allDocuments);

            // When
            DataResult<List<Document>> result = documentManager.searchDocumentsByTitle(emptyTitle);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(1, result.getData().size());

            verify(documentRepository).findByTitleContainingIgnoreCase(emptyTitle);
        }
    }

    @Nested
    @DisplayName("Delete Document Tests")
    class DeleteDocumentTests {

        @Test
        @DisplayName("Should delete document successfully when document exists")
        void shouldDeleteDocumentSuccessfullyWhenExists() {
            // Given
            Long documentId = 1L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.of(testDocument));
            doNothing().when(documentRepository).deleteById(documentId);

            // When
            Result result = documentManager.deleteDocument(documentId);

            // Then
            assertTrue(result.isSuccess());
            assertEquals(Messages.DocumentsDeletedSuccess, result.getMessage());

            verify(documentRepository).findById(documentId);
            verify(documentRepository).deleteById(documentId);
        }

        @Test
        @DisplayName("Should return error when trying to delete non-existent document")
        void shouldReturnErrorWhenTryingToDeleteNonExistentDocument() {
            // Given
            Long documentId = 999L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

            // When
            Result result = documentManager.deleteDocument(documentId);

            // Then
            assertFalse(result.isSuccess());
            assertEquals(Messages.DocumentsDeletedFailed, result.getMessage());

            verify(documentRepository).findById(documentId);
            verify(documentRepository, never()).deleteById(documentId);
        }

        @Test
        @DisplayName("Should handle null id when deleting")
        void shouldHandleNullIdWhenDeleting() {
            // Given
            Long nullId = null;
            when(documentRepository.findById(nullId)).thenReturn(Optional.empty());

            // When
            Result result = documentManager.deleteDocument(nullId);

            // Then
            assertFalse(result.isSuccess());
            assertEquals(Messages.DocumentsDeletedFailed, result.getMessage());

            verify(documentRepository).findById(nullId);
            verify(documentRepository, never()).deleteById(nullId);
        }
    }

    @Nested
    @DisplayName("Update Document Tests")
    class UpdateDocumentTests {

        @Test
        @DisplayName("Should update document successfully when document exists")
        void shouldUpdateDocumentSuccessfullyWhenExists() {
            // Given
            Long documentId = 1L;
            Document updatedDocument = Document.builder()
                    .id(1L)
                    .title("Updated Document")
                    .institutionName("Updated University")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://example.com/updated.pdf")
                    .updateAt(LocalDateTime.now())
                    .build();

            when(documentRepository.findById(documentId)).thenReturn(Optional.of(testDocument));
            doNothing().when(documentMapperUtil).updateDocumentFields(any(Document.class), any(DocumentUpdateRequest.class));
            when(documentRepository.save(any(Document.class))).thenReturn(updatedDocument);

            // When
            DataResult<Document> result = documentManager.updateDocument(documentId, updateRequest);

            // Then
            assertTrue(result.isSuccess());
            assertNotNull(result.getData());
            assertEquals(updatedDocument, result.getData());
            assertEquals(Messages.DocumentUpdateSuccess, result.getMessage());

            verify(documentRepository).findById(documentId);
            verify(documentMapperUtil).updateDocumentFields(testDocument, updateRequest);
            verify(documentRepository).save(testDocument);
        }

        @Test
        @DisplayName("Should return error when trying to update non-existent document")
        void shouldReturnErrorWhenTryingToUpdateNonExistentDocument() {
            // Given
            Long documentId = 999L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

            // When
            DataResult<Document> result = documentManager.updateDocument(documentId, updateRequest);

            // Then
            assertFalse(result.isSuccess());
            assertEquals(Messages.DocumentsFindFailed, result.getMessage());

            verify(documentRepository).findById(documentId);
            verify(documentMapperUtil, never()).updateDocumentFields(any(Document.class), any(DocumentUpdateRequest.class));
            verify(documentRepository, never()).save(any(Document.class));
        }

        @Test
        @DisplayName("Should handle null id when updating")
        void shouldHandleNullIdWhenUpdating() {
            // Given
            Long nullId = null;
            when(documentRepository.findById(nullId)).thenReturn(Optional.empty());

            // When
            DataResult<Document> result = documentManager.updateDocument(nullId, updateRequest);

            // Then
            assertFalse(result.isSuccess());
            assertEquals(Messages.DocumentsFindFailed, result.getMessage());

            verify(documentRepository).findById(nullId);
        }

        @Test
        @DisplayName("Should handle null update request")
        void shouldHandleNullUpdateRequest() {
            // Given
            Long documentId = 1L;
            DocumentUpdateRequest nullRequest = null;
            when(documentRepository.findById(documentId)).thenReturn(Optional.of(testDocument));
            when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

            // When
            DataResult<Document> result = documentManager.updateDocument(documentId, nullRequest);

            // Then
            assertTrue(result.isSuccess()); // Service doesn't validate null request
            verify(documentMapperUtil).updateDocumentFields(testDocument, nullRequest);
        }
    }

    @Nested
    @DisplayName("Integration and Error Handling Tests")
    class IntegrationAndErrorHandlingTests {

        @Test
        @DisplayName("Should handle repository exceptions gracefully")
        void shouldHandleRepositoryExceptionsGracefully() {
            // Given
            Long documentId = 1L;
            when(documentRepository.findById(documentId)).thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                documentManager.getDocumentById(documentId);
            });

            verify(documentRepository).findById(documentId);
        }

        @Test
        @DisplayName("Should handle mapper utility exceptions gracefully")
        void shouldHandleMapperUtilityExceptionsGracefully() {
            // Given
            Document document = Document.builder()
                    .title("Test")
                    .institutionName("Test Uni")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://test.com/test.pdf")
                    .build();

            doThrow(new RuntimeException("Mapping error")).when(documentMapperUtil).setCreationTimestamps(document);

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                documentManager.saveDocument(document);
            });

            verify(documentMapperUtil).setCreationTimestamps(document);
        }

        @Test
        @DisplayName("Should verify method interactions in correct order")
        void shouldVerifyMethodInteractionsInCorrectOrder() {
            // Given
            Long documentId = 1L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.of(testDocument));
            when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

            // When
            documentManager.updateDocument(documentId, updateRequest);

            // Then - Verify order of operations
            var inOrder = inOrder(documentRepository, documentMapperUtil);
            inOrder.verify(documentRepository).findById(documentId);
            inOrder.verify(documentMapperUtil).updateDocumentFields(testDocument, updateRequest);
            inOrder.verify(documentRepository).save(testDocument);
        }

        @Test
        @DisplayName("Should not call unnecessary methods when operation fails")
        void shouldNotCallUnnecessaryMethodsWhenOperationFails() {
            // Given
            Long documentId = 999L;
            when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

            // When
            documentManager.deleteDocument(documentId);

            // Then
            verify(documentRepository).findById(documentId);
            verify(documentRepository, never()).deleteById(any(Long.class));
        }
    }
}
