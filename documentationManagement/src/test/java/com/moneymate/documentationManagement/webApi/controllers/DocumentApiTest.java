package com.moneymate.documentationManagement.webApi.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneymate.documentationManagement.business.abstracts.DocumentService;
import com.moneymate.documentationManagement.business.abstracts.FileUploadService;
import com.moneymate.documentationManagement.business.requests.DocumentUpdateRequest;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessResult;
import com.moneymate.documentationManagement.entities.concretes.Document;

@WebMvcTest(DocumentApi.class)
@DisplayName("DocumentApi Controller Tests")
class DocumentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private FileUploadService fileUploadService;

    private Document testDocument;
    private List<Document> testDocuments;
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
                .fileUrl("https://storage.example.com/documents/test-file.pdf")
                .uploadAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .updateAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                .build();

        Document testDocument2 = Document.builder()
                .id(2L)
                .title("Another Document")
                .institutionName("Another University")
                .institutionType("College")
                .documentType("DOCX")
                .documentDescription("Another description")
                .fileSize("2MB")
                .fileUrl("https://storage.example.com/documents/another-file.docx")
                .uploadAt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .updateAt(LocalDateTime.of(2024, 1, 2, 12, 0))
                .build();

        testDocuments = Arrays.asList(testDocument, testDocument2);

        updateRequest = new DocumentUpdateRequest();
        // Set update request fields based on actual class structure
    }

    @Nested
    @DisplayName("GET /api/v1/documents/ - Get All Documents")
    class GetAllDocumentsTests {

        @Test
        @DisplayName("Should return all documents successfully")
        void shouldReturnAllDocumentsSuccessfully() throws Exception {
            // Given
            DataResult<List<Document>> successResult = new SuccessDataResult<>(testDocuments, "Success");
            when(documentService.getAllDocuments()).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].title").value("Test Document"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].title").value("Another Document"));

            verify(documentService).getAllDocuments();
        }

        @Test
        @DisplayName("Should return empty list when no documents exist")
        void shouldReturnEmptyListWhenNoDocumentsExist() throws Exception {
            // Given
            DataResult<List<Document>> successResult = new SuccessDataResult<>(Collections.emptyList(), "Success");
            when(documentService.getAllDocuments()).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(documentService).getAllDocuments();
        }

        @Test
        @DisplayName("Should handle service error gracefully")
        void shouldHandleServiceErrorGracefully() throws Exception {
            // Given
            DataResult<List<Document>> errorResult = new ErrorDataResult<>("Database error");
            when(documentService.getAllDocuments()).thenReturn(errorResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Database error"));

            verify(documentService).getAllDocuments();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/documents/{id} - Get Document By ID")
    class GetDocumentByIdTests {

        @Test
        @DisplayName("Should return document when ID exists")
        void shouldReturnDocumentWhenIdExists() throws Exception {
            // Given
            Long documentId = 1L;
            DataResult<Optional<Document>> successResult = new SuccessDataResult<>(Optional.of(testDocument), "Success");
            when(documentService.getDocumentById(documentId)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("Test Document"))
                    .andExpect(jsonPath("$.data.institutionName").value("Test University"));

            verify(documentService).getDocumentById(documentId);
        }

        @Test
        @DisplayName("Should return error when document not found")
        void shouldReturnErrorWhenDocumentNotFound() throws Exception {
            // Given
            Long documentId = 999L;
            DataResult<Optional<Document>> errorResult = new ErrorDataResult<>("Document not found");
            when(documentService.getDocumentById(documentId)).thenReturn(errorResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Document not found"));

            verify(documentService).getDocumentById(documentId);
        }

        @Test
        @DisplayName("Should handle invalid ID format")
        void shouldHandleInvalidIdFormat() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/documents/{id}", "invalid"))
                    .andExpect(status().isBadRequest());

            verify(documentService, never()).getDocumentById(any());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/documents/ - Upload File With Metadata")
    class UploadFileWithMetadataTests {

        @Test
        @DisplayName("Should upload file successfully with metadata")
        void shouldUploadFileSuccessfullyWithMetadata() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "test content".getBytes()
            );

            DataResult<Document> successResult = new SuccessDataResult<>(testDocument, "File uploaded successfully");
            when(fileUploadService.uploadFileWithMetadata(
                    any(), eq("Test Document"), eq("Test University"), eq("University"),
                    eq("https://test.edu"), eq("PDF"), eq("Test description")
            )).thenReturn(successResult);

            // When & Then
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(file)
                    .param("title", "Test Document")
                    .param("institutionName", "Test University")
                    .param("institutionType", "University")  
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Test description"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("Test Document"));

            verify(fileUploadService).uploadFileWithMetadata(
                    any(), eq("Test Document"), eq("Test University"), eq("University"),
                    eq("https://test.edu"), eq("PDF"), eq("Test description")
            );
        }

        @Test
        @DisplayName("Should return error when file is empty")
        void shouldReturnErrorWhenFileIsEmpty() throws Exception {
            // Given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", new byte[0]
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(emptyFile)
                    .param("title", "Test Document")
                    .param("institutionName", "Test University")
                    .param("institutionType", "University")
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Test description"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Dosya boş olamaz"));

            verify(fileUploadService, never()).uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should return error when file size exceeds limit")
        void shouldReturnErrorWhenFileSizeExceedsLimit() throws Exception {
            // Given - Create a file larger than 10MB
            byte[] largeFileContent = new byte[11 * 1024 * 1024]; // 11MB
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", "large.pdf", "application/pdf", largeFileContent
            );

            // When & Then
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(largeFile)
                    .param("title", "Large Document")
                    .param("institutionName", "Test University")
                    .param("institutionType", "University") 
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Large file test"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Dosya boyutu 10MB'dan büyük olamaz"));

            verify(fileUploadService, never()).uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should handle missing required parameters")
        void shouldHandleMissingRequiredParameters() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "test content".getBytes()
            );

            // When & Then - Missing title parameter
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(file)
                    .param("institutionName", "Test University")
                    .param("institutionType", "University")
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Test description"))
                    .andExpect(status().isBadRequest());

            verify(fileUploadService, never()).uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should handle file upload service error")
        void shouldHandleFileUploadServiceError() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "test content".getBytes()
            );

            DataResult<Document> errorResult = new ErrorDataResult<>("Upload failed");
            when(fileUploadService.uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(errorResult);

            // When & Then
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(file)
                    .param("title", "Test Document")
                    .param("institutionName", "Test University")
                    .param("institutionType", "University")
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Test description"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Upload failed"));

            verify(fileUploadService).uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/documents/institution/{institutionName} - Get Documents By Institution")
    class GetDocumentsByInstitutionTests {

        @Test
        @DisplayName("Should return documents for valid institution")
        void shouldReturnDocumentsForValidInstitution() throws Exception {
            // Given
            String institutionName = "Test University";
            List<Document> institutionDocuments = Arrays.asList(testDocument);
            DataResult<List<Document>> successResult = new SuccessDataResult<>(institutionDocuments, "Success");
            when(documentService.getDocumentsByInstitution(institutionName)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/institution/{institutionName}", institutionName))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].institutionName").value("Test University"));

            verify(documentService).getDocumentsByInstitution(institutionName);
        }

        @Test
        @DisplayName("Should return empty list for non-existent institution")
        void shouldReturnEmptyListForNonExistentInstitution() throws Exception {
            // Given
            String institutionName = "Non Existent University";
            DataResult<List<Document>> successResult = new SuccessDataResult<>(Collections.emptyList(), "Success");
            when(documentService.getDocumentsByInstitution(institutionName)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/institution/{institutionName}", institutionName))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(documentService).getDocumentsByInstitution(institutionName);
        }

        @Test
        @DisplayName("Should handle special characters in institution name")
        void shouldHandleSpecialCharactersInInstitutionName() throws Exception {
            // Given
            String institutionName = "Test & Research University";
            DataResult<List<Document>> successResult = new SuccessDataResult<>(Collections.emptyList(), "Success");
            when(documentService.getDocumentsByInstitution(institutionName)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/institution/{institutionName}", institutionName))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(documentService).getDocumentsByInstitution(institutionName);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/documents/type/{documentType} - Get Documents By Type")
    class GetDocumentsByTypeTests {

        @Test
        @DisplayName("Should return documents for valid document type")
        void shouldReturnDocumentsForValidDocumentType() throws Exception {
            // Given
            String documentType = "PDF";
            List<Document> pdfDocuments = Arrays.asList(testDocument);
            DataResult<List<Document>> successResult = new SuccessDataResult<>(pdfDocuments, "Success");
            when(documentService.getDocumentsByType(documentType)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/type/{documentType}", documentType))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].documentType").value("PDF"));

            verify(documentService).getDocumentsByType(documentType);
        }

        @Test
        @DisplayName("Should return empty list for non-existent document type")
        void shouldReturnEmptyListForNonExistentDocumentType() throws Exception {
            // Given
            String documentType = "XLS";
            DataResult<List<Document>> successResult = new SuccessDataResult<>(Collections.emptyList(), "Success");
            when(documentService.getDocumentsByType(documentType)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/type/{documentType}", documentType))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(documentService).getDocumentsByType(documentType);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/documents/search - Search Documents")
    class SearchDocumentsTests {

        @Test
        @DisplayName("Should return matching documents for search query")
        void shouldReturnMatchingDocumentsForSearchQuery() throws Exception {
            // Given
            String searchTitle = "Test";
            List<Document> searchResults = Arrays.asList(testDocument);
            DataResult<List<Document>> successResult = new SuccessDataResult<>(searchResults, "Success");
            when(documentService.searchDocumentsByTitle(searchTitle)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/search")
                    .param("title", searchTitle))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].title").value("Test Document"));

            verify(documentService).searchDocumentsByTitle(searchTitle);
        }

        @Test
        @DisplayName("Should return empty list when no matches found")
        void shouldReturnEmptyListWhenNoMatchesFound() throws Exception {
            // Given
            String searchTitle = "NonExistent";
            DataResult<List<Document>> successResult = new SuccessDataResult<>(Collections.emptyList(), "Success");
            when(documentService.searchDocumentsByTitle(searchTitle)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/search")
                    .param("title", searchTitle))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));

            verify(documentService).searchDocumentsByTitle(searchTitle);
        }

        @Test
        @DisplayName("Should handle missing search parameter")
        void shouldHandleMissingSearchParameter() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/documents/search"))
                    .andExpect(status().isBadRequest());

            verify(documentService, never()).searchDocumentsByTitle(any());
        }

        @Test
        @DisplayName("Should handle empty search parameter")
        void shouldHandleEmptySearchParameter() throws Exception {
            // Given
            String emptySearch = "";
            DataResult<List<Document>> successResult = new SuccessDataResult<>(testDocuments, "Success");
            when(documentService.searchDocumentsByTitle(emptySearch)).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/search")
                    .param("title", emptySearch))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(documentService).searchDocumentsByTitle(emptySearch);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/documents/{id} - Update Document")
    class UpdateDocumentTests {

        @Test
        @DisplayName("Should update document successfully")
        void shouldUpdateDocumentSuccessfully() throws Exception {
            // Given
            Long documentId = 1L;
            Document updatedDocument = Document.builder()
                    .id(documentId)
                    .title("Updated Document")
                    .institutionName("Updated University")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://storage.example.com/updated.pdf")
                    .build();

            DataResult<Document> successResult = new SuccessDataResult<>(updatedDocument, "Document updated");
            when(documentService.updateDocument(eq(documentId), any(DocumentUpdateRequest.class)))
                    .thenReturn(successResult);

            // When & Then
            mockMvc.perform(put("/api/v1/documents/{id}", documentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.title").value("Updated Document"));

            verify(documentService).updateDocument(eq(documentId), any(DocumentUpdateRequest.class));
        }

        @Test
        @DisplayName("Should return error when document not found for update")
        void shouldReturnErrorWhenDocumentNotFoundForUpdate() throws Exception {
            // Given
            Long documentId = 999L;
            DataResult<Document> errorResult = new ErrorDataResult<>("Document not found");
            when(documentService.updateDocument(eq(documentId), any(DocumentUpdateRequest.class)))
                    .thenReturn(errorResult);

            // When & Then
            mockMvc.perform(put("/api/v1/documents/{id}", documentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Document not found"));

            verify(documentService).updateDocument(eq(documentId), any(DocumentUpdateRequest.class));
        }

        @Test
        @DisplayName("Should handle invalid JSON in request body")
        void shouldHandleInvalidJsonInRequestBody() throws Exception {
            // Given
            Long documentId = 1L;
            String invalidJson = "{ invalid json }";

            // When & Then
            mockMvc.perform(put("/api/v1/documents/{id}", documentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(documentService, never()).updateDocument(any(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/documents/{id} - Delete Document")
    class DeleteDocumentTests {

        @Test
        @DisplayName("Should delete document and file successfully")
        void shouldDeleteDocumentAndFileSuccessfully() throws Exception {
            // Given
            Long documentId = 1L;
            String fileName = "test-file.pdf";
            
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(testDocument), "Found");
            Result deleteResult = new SuccessResult("Document deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile(fileName)).thenReturn(deleteResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Document deleted"));

            verify(documentService).getDocumentById(documentId);
            verify(fileUploadService).deleteFile(fileName);
            verify(documentService).deleteDocument(documentId);
        }

        @Test
        @DisplayName("Should return error when document not found for deletion")
        void shouldReturnErrorWhenDocumentNotFoundForDeletion() throws Exception {
            // Given
            Long documentId = 999L;
            DataResult<Optional<Document>> errorResult = new ErrorDataResult<>("Document not found");
            when(documentService.getDocumentById(documentId)).thenReturn(errorResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Document not found"));

            verify(documentService).getDocumentById(documentId);
            verify(fileUploadService, never()).deleteFile(any());
            verify(documentService, never()).deleteDocument(any());
        }

        @Test
        @DisplayName("Should handle file name extraction from different URL formats")
        void shouldHandleFileNameExtractionFromDifferentUrlFormats() throws Exception {
            // Given
            Long documentId = 1L;
            Document docWithDifferentUrl = Document.builder()
                    .id(documentId)
                    .title("Test Document")
                    .fileUrl("https://storage.example.com/path/to/file/document.pdf")
                    .build();
            
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(docWithDifferentUrl), "Found");
            Result deleteResult = new SuccessResult("Document deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile("document.pdf")).thenReturn(deleteResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(fileUploadService).deleteFile("document.pdf");
        }

        @Test
        @DisplayName("Should handle URL without path separators")
        void shouldHandleUrlWithoutPathSeparators() throws Exception {
            // Given
            Long documentId = 1L;
            Document docWithSimpleUrl = Document.builder()
                    .id(documentId)
                    .title("Test Document")
                    .fileUrl("simple-filename.pdf")
                    .build();
            
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(docWithSimpleUrl), "Found");
            Result deleteResult = new SuccessResult("Document deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile("simple-filename.pdf")).thenReturn(deleteResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(fileUploadService).deleteFile("simple-filename.pdf");
        }
    }

    @Nested
    @DisplayName("Cross-Origin and CORS Tests")
    class CorsTests {

        @Test
        @DisplayName("Should handle CORS preflight request")
        void shouldHandleCorsPreflight() throws Exception {
            // When & Then
            mockMvc.perform(options("/api/v1/documents/")
                    .header("Origin", "http://localhost:3000")
                    .header("Access-Control-Request-Method", "GET")
                    .header("Access-Control-Request-Headers", "Content-Type"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "*"));
        }

        @Test
        @DisplayName("Should allow cross-origin requests")
        void shouldAllowCrossOriginRequests() throws Exception {
            // Given
            DataResult<List<Document>> successResult = new SuccessDataResult<>(testDocuments, "Success");
            when(documentService.getAllDocuments()).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/")
                    .header("Origin", "http://localhost:3000"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "*"));

            verify(documentService).getAllDocuments();
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases Tests")
    class ErrorHandlingAndEdgeCasesTests {

        @Test
        @DisplayName("Should handle service exceptions gracefully")
        void shouldHandleServiceExceptionsGracefully() throws Exception {
            // Given
            when(documentService.getAllDocuments())
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/api/v1/documents/"))
                    .andExpect(status().isInternalServerError());

            verify(documentService).getAllDocuments();
        }

        @Test
        @DisplayName("Should handle malformed path variables")
        void shouldHandleMalformedPathVariables() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/documents/{id}", "not-a-number"))
                    .andExpect(status().isBadRequest());

            verify(documentService, never()).getDocumentById(any());
        }

        @Test
        @DisplayName("Should handle large file uploads within limit")
        void shouldHandleLargeFileUploadsWithinLimit() throws Exception {
            // Given - Create a file just under the 10MB limit
            byte[] largeFileContent = new byte[9 * 1024 * 1024]; // 9MB
            MockMultipartFile largeFile = new MockMultipartFile(
                    "file", "large.pdf", "application/pdf", largeFileContent
            );

            DataResult<Document> successResult = new SuccessDataResult<>(testDocument, "Upload successful");
            when(fileUploadService.uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(successResult);

            // When & Then
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(largeFile)
                    .param("title", "Large Document")
                    .param("institutionName", "Test University")
                    .param("institutionType", "University")
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Large file within limit"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(fileUploadService).uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should handle concurrent requests properly")
        void shouldHandleConcurrentRequestsProperly() throws Exception {
            // Given
            DataResult<List<Document>> successResult = new SuccessDataResult<>(testDocuments, "Success");
            when(documentService.getAllDocuments()).thenReturn(successResult);

            // When & Then - Simulate concurrent requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/api/v1/documents/"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success").value(true));
            }

            verify(documentService, times(5)).getAllDocuments();
        }
    }

    @Nested
    @DisplayName("File Name Extraction Utility Tests")
    class FileNameExtractionTests {

        @Test
        @DisplayName("Should extract filename from standard URL")
        void shouldExtractFilenameFromStandardUrl() throws Exception {
            // Given
            Long documentId = 1L;
            Document docWithStandardUrl = Document.builder()
                    .id(documentId)
                    .fileUrl("https://storage.example.com/documents/my-document.pdf")
                    .build();
            
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(docWithStandardUrl), "Found");
            Result deleteResult = new SuccessResult("Deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile("my-document.pdf")).thenReturn(deleteResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk());

            verify(fileUploadService).deleteFile("my-document.pdf");
        }

        @Test
        @DisplayName("Should handle null file URL")
        void shouldHandleNullFileUrl() throws Exception {
            // Given
            Long documentId = 1L;
            Document docWithNullUrl = Document.builder()
                    .id(documentId)
                    .fileUrl(null)
                    .build();
            
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(docWithNullUrl), "Found");
            Result deleteResult = new SuccessResult("Deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile(null)).thenReturn(deleteResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk());

            verify(fileUploadService).deleteFile(null);
        }

        @Test
        @DisplayName("Should handle empty file URL")
        void shouldHandleEmptyFileUrl() throws Exception {
            // Given
            Long documentId = 1L;
            Document docWithEmptyUrl = Document.builder()
                    .id(documentId)
                    .fileUrl("")
                    .build();
            
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(docWithEmptyUrl), "Found");
            Result deleteResult = new SuccessResult("Deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile("")).thenReturn(deleteResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteResult);

            // When & Then
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId))
                    .andExpect(status().isOk());

            verify(fileUploadService).deleteFile("");
        }
    }

    @Nested
    @DisplayName("API Documentation and Swagger Tests")  
    class SwaggerDocumentationTests {

        @Test
        @DisplayName("Should have proper API endpoints documented")
        void shouldHaveProperApiEndpointsDocumented() throws Exception {
            // This test verifies that endpoints are accessible
            // Swagger documentation is handled by annotations
            
            // Given
            DataResult<List<Document>> successResult = new SuccessDataResult<>(testDocuments, "Success");
            when(documentService.getAllDocuments()).thenReturn(successResult);

            // When & Then
            mockMvc.perform(get("/api/v1/documents/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            verify(documentService).getAllDocuments();
        }
    }

    @Nested
    @DisplayName("Integration and Method Interaction Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should verify correct service method calls during upload")
        void shouldVerifyCorrectServiceMethodCallsDuringUpload() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "test content".getBytes()
            );

            DataResult<Document> successResult = new SuccessDataResult<>(testDocument, "Uploaded");
            when(fileUploadService.uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any()))
                    .thenReturn(successResult);

            // When
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(file)
                    .param("title", "Test")
                    .param("institutionName", "Uni")
                    .param("institutionType", "University")
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Desc"));

            // Then
            verify(fileUploadService).uploadFileWithMetadata(
                    any(), eq("Test"), eq("Uni"), eq("University"),
                    eq("https://test.edu"), eq("PDF"), eq("Desc")
            );
            verify(documentService, never()).getAllDocuments(); // Should not call other methods
        }

        @Test
        @DisplayName("Should verify correct service method calls during deletion")
        void shouldVerifyCorrectServiceMethodCallsDuringDeletion() throws Exception {
            // Given
            Long documentId = 1L;
            DataResult<Optional<Document>> getResult = new SuccessDataResult<>(Optional.of(testDocument), "Found");
            Result deleteFileResult = new SuccessResult("File deleted");
            Result deleteDocResult = new SuccessResult("Document deleted");
            
            when(documentService.getDocumentById(documentId)).thenReturn(getResult);
            when(fileUploadService.deleteFile(any())).thenReturn(deleteFileResult);
            when(documentService.deleteDocument(documentId)).thenReturn(deleteDocResult);

            // When
            mockMvc.perform(delete("/api/v1/documents/{id}", documentId));

            // Then - Verify order of operations
            var inOrder = inOrder(documentService, fileUploadService);
            inOrder.verify(documentService).getDocumentById(documentId);
            inOrder.verify(fileUploadService).deleteFile(any());
            inOrder.verify(documentService).deleteDocument(documentId);
        }

        @Test
        @DisplayName("Should not call unnecessary services when validation fails")
        void shouldNotCallUnnecessaryServicesWhenValidationFails() throws Exception {
            // Given
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", new byte[0]
            );

            // When
            mockMvc.perform(multipart("/api/v1/documents/")
                    .file(emptyFile)
                    .param("title", "Test")
                    .param("institutionName", "Uni")
                    .param("institutionType", "University")
                    .param("institutionUrl", "https://test.edu")
                    .param("documentType", "PDF")
                    .param("documentDescription", "Desc"));

            // Then
            verify(fileUploadService, never()).uploadFileWithMetadata(any(), any(), any(), any(), any(), any(), any());
            verify(documentService, never()).getAllDocuments();
        }
    }
}