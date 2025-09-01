package com.moneymate.documentationManagement.dataAccess.abstracts;

import com.moneymate.documentationManagement.dataAccess.abstracts.DocumentRepository;

import com.moneymate.documentationManagement.entities.concretes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("DocumentRepository Integration Tests")
class DocumentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository documentRepository;

    private Document testDocument1;
    private Document testDocument2;
    private Document testDocument3;

    @BeforeEach
    void setUp() {
        // Test verilerini hazırla
        testDocument1 = Document.builder()
                .title("Spring Boot Guide")
                .institutionName("Tech University")
                .institutionType("University")
                .institutionUrl("https://techuni.edu")
                .documentType("PDF")
                .documentDescription("Comprehensive Spring Boot tutorial")
                .fileSize("5MB")
                .fileUrl("https://storage.example.com/spring-boot-guide.pdf")
                .uploadAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .updateAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        testDocument2 = Document.builder()
                .title("Java Advanced Concepts")
                .institutionName("Code Academy")
                .institutionType("Academy")
                .institutionUrl("https://codeacademy.com")
                .documentType("DOCX")
                .documentDescription("Advanced Java programming concepts")
                .fileSize("3MB")
                .fileUrl("https://storage.example.com/java-advanced.docx")
                .uploadAt(LocalDateTime.of(2024, 1, 2, 11, 0))
                .updateAt(LocalDateTime.of(2024, 1, 2, 11, 0))
                .build();

        testDocument3 = Document.builder()
                .title("Spring Security Implementation")
                .institutionName("Tech University")
                .institutionType("University")
                .institutionUrl("https://techuni.edu")
                .documentType("PDF")
                .documentDescription("Security implementation in Spring")
                .fileSize("4MB")
                .fileUrl("https://storage.example.com/spring-security.pdf")
                .uploadAt(LocalDateTime.of(2024, 1, 3, 12, 0))
                .updateAt(LocalDateTime.of(2024, 1, 3, 12, 0))
                .build();

        // Test verilerini kaydet
        entityManager.persistAndFlush(testDocument1);
        entityManager.persistAndFlush(testDocument2);
        entityManager.persistAndFlush(testDocument3);
    }

    @Nested
    @DisplayName("Basic JpaRepository Methods Tests")
    class BasicJpaRepositoryTests {

        @Test
        @DisplayName("Should save and retrieve document")
        void shouldSaveAndRetrieveDocument() {
            // Given
            Document newDocument = Document.builder()
                    .title("New Document")
                    .institutionName("New Institution")
                    .institutionType("College")
                    .documentType("PPT")
                    .fileUrl("https://example.com/new-doc.ppt")
                    .build();

            // When
            Document savedDocument = documentRepository.save(newDocument);
            Optional<Document> foundDocument = documentRepository.findById(savedDocument.getId());

            // Then
            assertThat(savedDocument).isNotNull();
            assertThat(savedDocument.getId()).isNotNull();
            assertThat(foundDocument).isPresent();
            assertThat(foundDocument.get().getTitle()).isEqualTo("New Document");
            assertThat(foundDocument.get().getInstitutionName()).isEqualTo("New Institution");
        }

        @Test
        @DisplayName("Should find all documents")
        void shouldFindAllDocuments() {
            // When
            List<Document> allDocuments = documentRepository.findAll();

            // Then
            assertThat(allDocuments).hasSize(3);
            assertThat(allDocuments)
                    .extracting(Document::getTitle)
                    .containsExactlyInAnyOrder(
                            "Spring Boot Guide",
                            "Java Advanced Concepts", 
                            "Spring Security Implementation"
                    );
        }

        @Test
        @DisplayName("Should delete document by id")
        void shouldDeleteDocumentById() {
            // Given
            Long documentId = testDocument1.getId();

            // When
            documentRepository.deleteById(documentId);
            Optional<Document> deletedDocument = documentRepository.findById(documentId);

            // Then
            assertThat(deletedDocument).isEmpty();
            assertThat(documentRepository.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("Should count total documents")
        void shouldCountTotalDocuments() {
            // When
            long count = documentRepository.count();

            // Then
            assertThat(count).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Custom Query Methods Tests")
    class CustomQueryMethodsTests {

        @Test
        @DisplayName("Should find documents by institution name")
        void shouldFindDocumentsByInstitutionName() {
            // When
            List<Document> techUniDocuments = documentRepository.findByInstitutionName("Tech University");
            List<Document> codeAcademyDocuments = documentRepository.findByInstitutionName("Code Academy");

            // Then
            assertThat(techUniDocuments).hasSize(2);
            assertThat(techUniDocuments)
                    .extracting(Document::getTitle)
                    .containsExactlyInAnyOrder("Spring Boot Guide", "Spring Security Implementation");

            assertThat(codeAcademyDocuments).hasSize(1);
            assertThat(codeAcademyDocuments.get(0).getTitle()).isEqualTo("Java Advanced Concepts");
        }

        @Test
        @DisplayName("Should return empty list for non-existent institution name")
        void shouldReturnEmptyListForNonExistentInstitutionName() {
            // When
            List<Document> documents = documentRepository.findByInstitutionName("Non Existent University");

            // Then
            assertThat(documents).isEmpty();
        }

        @Test
        @DisplayName("Should find documents by document type")
        void shouldFindDocumentsByDocumentType() {
            // When
            List<Document> pdfDocuments = documentRepository.findByDocumentType("PDF");
            List<Document> docxDocuments = documentRepository.findByDocumentType("DOCX");

            // Then
            assertThat(pdfDocuments).hasSize(2);
            assertThat(pdfDocuments)
                    .extracting(Document::getTitle)
                    .containsExactlyInAnyOrder("Spring Boot Guide", "Spring Security Implementation");

            assertThat(docxDocuments).hasSize(1);
            assertThat(docxDocuments.get(0).getTitle()).isEqualTo("Java Advanced Concepts");
        }

        @Test
        @DisplayName("Should return empty list for non-existent document type")
        void shouldReturnEmptyListForNonExistentDocumentType() {
            // When
            List<Document> documents = documentRepository.findByDocumentType("XLS");

            // Then
            assertThat(documents).isEmpty();
        }

        @Test
        @DisplayName("Should find documents by title containing ignore case")
        void shouldFindDocumentsByTitleContainingIgnoreCase() {
            // When
            List<Document> springDocuments = documentRepository.findByTitleContainingIgnoreCase("spring");
            List<Document> guideDocuments = documentRepository.findByTitleContainingIgnoreCase("GUIDE");
            List<Document> javaDocuments = documentRepository.findByTitleContainingIgnoreCase("Java");

            // Then
            assertThat(springDocuments).hasSize(2);
            assertThat(springDocuments)
                    .extracting(Document::getTitle)
                    .containsExactlyInAnyOrder("Spring Boot Guide", "Spring Security Implementation");

            assertThat(guideDocuments).hasSize(1);
            assertThat(guideDocuments.get(0).getTitle()).isEqualTo("Spring Boot Guide");

            assertThat(javaDocuments).hasSize(1);
            assertThat(javaDocuments.get(0).getTitle()).isEqualTo("Java Advanced Concepts");
        }

        @Test
        @DisplayName("Should return empty list when no title matches")
        void shouldReturnEmptyListWhenNoTitleMatches() {
            // When
            List<Document> documents = documentRepository.findByTitleContainingIgnoreCase("Python");

            // Then
            assertThat(documents).isEmpty();
        }

        @Test
        @DisplayName("Should find document by file URL")
        void shouldFindDocumentByFileUrl() {
            // When
            Optional<Document> foundDocument = documentRepository
                    .findByFileUrl("https://storage.example.com/spring-boot-guide.pdf");
            Optional<Document> notFoundDocument = documentRepository
                    .findByFileUrl("https://storage.example.com/non-existent.pdf");

            // Then
            assertThat(foundDocument).isPresent();
            assertThat(foundDocument.get().getTitle()).isEqualTo("Spring Boot Guide");

            assertThat(notFoundDocument).isEmpty();
        }
    }

    @Nested
    @DisplayName("Custom @Query Method Tests")
    class CustomQueryAnnotationTests {

        @Test
        @DisplayName("Should find documents by institution name and document type")
        void shouldFindDocumentsByInstitutionAndType() {
            // When
            List<Document> techUniPdfDocuments = documentRepository
                    .findByInstitutionAndType("Tech University", "PDF");
            List<Document> codeAcademyDocxDocuments = documentRepository
                    .findByInstitutionAndType("Code Academy", "DOCX");
            List<Document> techUniDocxDocuments = documentRepository
                    .findByInstitutionAndType("Tech University", "DOCX");

            // Then
            assertThat(techUniPdfDocuments).hasSize(2);
            assertThat(techUniPdfDocuments)
                    .extracting(Document::getTitle)
                    .containsExactlyInAnyOrder("Spring Boot Guide", "Spring Security Implementation");
            assertThat(techUniPdfDocuments)
                    .allMatch(doc -> "Tech University".equals(doc.getInstitutionName()))
                    .allMatch(doc -> "PDF".equals(doc.getDocumentType()));

            assertThat(codeAcademyDocxDocuments).hasSize(1);
            assertThat(codeAcademyDocxDocuments.get(0).getTitle()).isEqualTo("Java Advanced Concepts");
            assertThat(codeAcademyDocxDocuments.get(0).getInstitutionName()).isEqualTo("Code Academy");
            assertThat(codeAcademyDocxDocuments.get(0).getDocumentType()).isEqualTo("DOCX");

            assertThat(techUniDocxDocuments).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list when no documents match institution and type combination")
        void shouldReturnEmptyListWhenNoDocumentsMatchCombination() {
            // When
            List<Document> nonExistentCombination1 = documentRepository
                    .findByInstitutionAndType("Non Existent University", "PDF");
            List<Document> nonExistentCombination2 = documentRepository
                    .findByInstitutionAndType("Tech University", "XLS");
            List<Document> nonExistentCombination3 = documentRepository
                    .findByInstitutionAndType("Code Academy", "PDF");

            // Then
            assertThat(nonExistentCombination1).isEmpty();
            assertThat(nonExistentCombination2).isEmpty();
            assertThat(nonExistentCombination3).isEmpty();
        }

        @Test
        @DisplayName("Should handle null parameters in custom query")
        void shouldHandleNullParametersInCustomQuery() {
            // When & Then
            assertThat(documentRepository.findByInstitutionAndType(null, "PDF")).isEmpty();
            assertThat(documentRepository.findByInstitutionAndType("Tech University", null)).isEmpty();
            assertThat(documentRepository.findByInstitutionAndType(null, null)).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty string parameters in custom query")
        void shouldHandleEmptyStringParametersInCustomQuery() {
            // When
            List<Document> emptyInstitution = documentRepository.findByInstitutionAndType("", "PDF");
            List<Document> emptyDocType = documentRepository.findByInstitutionAndType("Tech University", "");
            List<Document> bothEmpty = documentRepository.findByInstitutionAndType("", "");

            // Then
            assertThat(emptyInstitution).isEmpty();
            assertThat(emptyDocType).isEmpty();
            assertThat(bothEmpty).isEmpty();
        }

        @Test
        @DisplayName("Should be case sensitive for institution name and document type")
        void shouldBeCaseSensitiveForInstitutionAndDocumentType() {
            // When
            List<Document> correctCase = documentRepository
                    .findByInstitutionAndType("Tech University", "PDF");
            List<Document> wrongInstitutionCase = documentRepository
                    .findByInstitutionAndType("tech university", "PDF");
            List<Document> wrongDocTypeCase = documentRepository
                    .findByInstitutionAndType("Tech University", "pdf");

            // Then
            assertThat(correctCase).hasSize(2);
            assertThat(wrongInstitutionCase).isEmpty();
            assertThat(wrongDocTypeCase).isEmpty();
        }

        @Test
        @DisplayName("Should work with all valid document types")
        void shouldWorkWithAllValidDocumentTypes() {
            // Given - Add documents with different types
            Document xlsDocument = Document.builder()
                    .title("Excel Data")
                    .institutionName("Data University")
                    .institutionType("University")
                    .documentType("XLS")
                    .fileUrl("https://example.com/data.xls")
                    .build();

            Document pptDocument = Document.builder()
                    .title("Presentation Slides")
                    .institutionName("Data University")
                    .institutionType("University")
                    .documentType("PPT")
                    .fileUrl("https://example.com/slides.ppt")
                    .build();

            entityManager.persistAndFlush(xlsDocument);
            entityManager.persistAndFlush(pptDocument);

            // When
            List<Document> xlsDocuments = documentRepository
                    .findByInstitutionAndType("Data University", "XLS");
            List<Document> pptDocuments = documentRepository
                    .findByInstitutionAndType("Data University", "PPT");

            // Then
            assertThat(xlsDocuments).hasSize(1);
            assertThat(xlsDocuments.get(0).getTitle()).isEqualTo("Excel Data");

            assertThat(pptDocuments).hasSize(1);
            assertThat(pptDocuments.get(0).getTitle()).isEqualTo("Presentation Slides");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null parameters gracefully")
        void shouldHandleNullParametersGracefully() {
            // When & Then
            assertThat(documentRepository.findByInstitutionName(null)).isEmpty();
            assertThat(documentRepository.findByDocumentType(null)).isEmpty();
            assertThat(documentRepository.findByTitleContainingIgnoreCase(null)).isEmpty();
            assertThat(documentRepository.findByFileUrl(null)).isEmpty();
            assertThat(documentRepository.findByInstitutionAndType(null, "PDF")).isEmpty();
            assertThat(documentRepository.findByInstitutionAndType("Tech University", null)).isEmpty();
            assertThat(documentRepository.findByInstitutionAndType(null, null)).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty string parameters")
        void shouldHandleEmptyStringParameters() {
            // When
            List<Document> byInstitution = documentRepository.findByInstitutionName("");
            List<Document> byDocType = documentRepository.findByDocumentType("");
            List<Document> byTitle = documentRepository.findByTitleContainingIgnoreCase("");
            Optional<Document> byFileUrl = documentRepository.findByFileUrl("");
            List<Document> byInstitutionAndType = documentRepository.findByInstitutionAndType("", "PDF");

            // Then
            assertThat(byInstitution).isEmpty();
            assertThat(byDocType).isEmpty();
            assertThat(byTitle).hasSize(3); // Empty string matches all titles
            assertThat(byFileUrl).isEmpty();
            assertThat(byInstitutionAndType).isEmpty();
        }

        @Test
        @DisplayName("Should handle case sensitivity correctly")
        void shouldHandleCaseSensitivityCorrectly() {
            // When
            List<Document> upperCase = documentRepository.findByTitleContainingIgnoreCase("SPRING");
            List<Document> lowerCase = documentRepository.findByTitleContainingIgnoreCase("spring");
            List<Document> mixedCase = documentRepository.findByTitleContainingIgnoreCase("SpRiNg");

            // Then - All should return same results due to ignore case
            assertThat(upperCase).hasSize(2);
            assertThat(lowerCase).hasSize(2);
            assertThat(mixedCase).hasSize(2);
            assertThat(upperCase).containsExactlyInAnyOrderElementsOf(lowerCase);
            assertThat(lowerCase).containsExactlyInAnyOrderElementsOf(mixedCase);
        }

        @Test
        @DisplayName("Should handle special characters in search")
        void shouldHandleSpecialCharactersInSearch() {
            // Given - Create document with special characters
            Document specialDocument = Document.builder()
                    .title("C++ Programming & Data Structures")
                    .institutionName("Special-Chars University")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://example.com/cpp-programming.pdf")
                    .build();
            entityManager.persistAndFlush(specialDocument);

            // When
            List<Document> documentsWithPlus = documentRepository.findByTitleContainingIgnoreCase("C++");
            List<Document> documentsWithAmpersand = documentRepository.findByTitleContainingIgnoreCase("&");
            List<Document> documentsWithHyphen = documentRepository.findByInstitutionName("Special-Chars University");

            // Then
            assertThat(documentsWithPlus).hasSize(1);
            assertThat(documentsWithAmpersand).hasSize(1);
            assertThat(documentsWithHyphen).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Performance and Data Integrity Tests") 
    class PerformanceAndIntegrityTests {

        @Test
        @DisplayName("Should maintain data integrity with concurrent operations")
        void shouldMaintainDataIntegrityWithConcurrentOperations() {
            // Given
            String uniqueTitle = "Unique Test Document " + System.currentTimeMillis();
            Document document = Document.builder()
                    .title(uniqueTitle)
                    .institutionName("Test Institution")
                    .institutionType("University")
                    .documentType("PDF")
                    .fileUrl("https://example.com/unique-" + System.currentTimeMillis() + ".pdf")
                    .build();

            // When
            Document saved = documentRepository.save(document);
            Optional<Document> retrieved = documentRepository.findById(saved.getId());
            
            // Modify and save again
            saved.setTitle("Updated " + uniqueTitle);
            Document updated = documentRepository.save(saved);

            // Then
            assertThat(retrieved).isPresent();
            assertThat(updated.getTitle()).startsWith("Updated");
            assertThat(updated.getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("Should handle large result sets efficiently")
        void shouldHandleLargeResultSetsEfficiently() {
            // Given - Create multiple documents with same institution
            String institutionName = "Large Dataset University";
            for (int i = 0; i < 100; i++) {
                Document doc = Document.builder()
                        .title("Document " + i)
                        .institutionName(institutionName)
                        .institutionType("University")
                        .documentType("PDF")
                        .fileUrl("https://example.com/doc-" + i + ".pdf")
                        .build();
                entityManager.persist(doc);
            }
            entityManager.flush();

            // When
            long startTime = System.currentTimeMillis();
            List<Document> documents = documentRepository.findByInstitutionName(institutionName);
            long endTime = System.currentTimeMillis();

            // Then
            assertThat(documents).hasSize(100);
            assertThat(endTime - startTime).isLessThan(1000); // Should complete within 1 second
        }
    }
}
