package com.moneymate.documentationManagement.entities;

import com.moneymate.documentationManagement.entities.concretes.Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Document Entity Tests")
class DocumentTest {

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create Document with no-args constructor")
        void shouldCreateDocumentWithNoArgsConstructor() {
            // Given & When
            Document newDocument = new Document();

            // Then
            assertNotNull(newDocument);
            assertNull(newDocument.getId());
            assertNull(newDocument.getTitle());
            assertNull(newDocument.getInstitutionName());
            assertNull(newDocument.getInstitutionType());
            assertNull(newDocument.getInstitutionUrl());
            assertNull(newDocument.getDocumentType());
            assertNull(newDocument.getUploadAt());
            assertNull(newDocument.getUpdateAt());
            assertNull(newDocument.getDocumentDescription());
            assertNull(newDocument.getFileSize());
            assertNull(newDocument.getFileUrl());
        }

        @Test
        @DisplayName("Should create Document with all-args constructor")
        void shouldCreateDocumentWithAllArgsConstructor() {
            // Given
            Long id = 1L;
            String title = "Test Document";
            String minioFileName = "minioFileName";
            String institutionName = "Test Institution";
            String institutionType = "University";
            String institutionUrl = "https://test.edu";
            String documentType = "PDF";
            LocalDateTime uploadAt = LocalDateTime.now();
            LocalDateTime updateAt = LocalDateTime.now();
            String documentDescription = "Test description";
            String fileSize = "1024KB";
            String fileUrl = "https://example.com/document.pdf";

            // When
            Document newDocument = new Document(id, title, minioFileName, institutionName, institutionType,
                    institutionUrl, documentType, uploadAt, updateAt,
                    documentDescription, fileSize, fileUrl);

            // Then
            assertNotNull(newDocument);
            assertEquals(id, newDocument.getId());
            assertEquals(title, newDocument.getTitle());
            assertEquals(institutionName, newDocument.getInstitutionName());
            assertEquals(institutionType, newDocument.getInstitutionType());
            assertEquals(institutionUrl, newDocument.getInstitutionUrl());
            assertEquals(documentType, newDocument.getDocumentType());
            assertEquals(uploadAt, newDocument.getUploadAt());
            assertEquals(updateAt, newDocument.getUpdateAt());
            assertEquals(documentDescription, newDocument.getDocumentDescription());
            assertEquals(fileSize, newDocument.getFileSize());
            assertEquals(fileUrl, newDocument.getFileUrl());
        }

        @Test
        @DisplayName("Should create Document with custom constructor and set timestamps")
        void shouldCreateDocumentWithCustomConstructorAndSetTimestamps() {
            // Given
            String title = "Test Document";
            String minioFileName = "minioFileName";
            String institutionName = "Test Institution";
            String institutionType = "University";
            String institutionUrl = "https://test.edu";
            String documentType = "PDF";
            String documentDescription = "Test description";
            String fileSize = "1024KB";
            String fileUrl = "https://example.com/document.pdf";
            LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

            // When
            Document newDocument = new Document(title, minioFileName, institutionName, institutionType,
                    institutionUrl, documentType, documentDescription, fileSize, fileUrl);

            // Then
            LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
            assertNotNull(newDocument);
            assertEquals(title, newDocument.getTitle());
            assertEquals(institutionName, newDocument.getInstitutionName());
            assertEquals(institutionType, newDocument.getInstitutionType());
            assertEquals(institutionUrl, newDocument.getInstitutionUrl());
            assertEquals(documentType, newDocument.getDocumentType());
            assertEquals(documentDescription, newDocument.getDocumentDescription());
            assertEquals(fileSize, newDocument.getFileSize());
            assertEquals(fileUrl, newDocument.getFileUrl());
            
            // Verify timestamps are set
            assertNotNull(newDocument.getUploadAt());
            assertNotNull(newDocument.getUpdateAt());
            assertTrue(newDocument.getUploadAt().isAfter(beforeCreation));
            assertTrue(newDocument.getUploadAt().isBefore(afterCreation));
            assertTrue(newDocument.getUpdateAt().isAfter(beforeCreation));
            assertTrue(newDocument.getUpdateAt().isBefore(afterCreation));
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTests {

        @Test
        @DisplayName("Should create Document using builder")
        void shouldCreateDocumentUsingBuilder() {
            // Given & When
            Document document = Document.builder()
                    .id(1L)
                    .title("Test Document")
                    .institutionName("Test Institution")
                    .institutionType("University")
                    .institutionUrl("https://test.edu")
                    .documentType("PDF")
                    .documentDescription("Test description")
                    .fileSize("1024KB")
                    .fileUrl("https://example.com/document.pdf")
                    .uploadAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                    .updateAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                    .build();

            // Then
            assertNotNull(document);
            assertEquals(1L, document.getId());
            assertEquals("Test Document", document.getTitle());
            assertEquals("Test Institution", document.getInstitutionName());
            assertEquals("University", document.getInstitutionType());
            assertEquals("https://test.edu", document.getInstitutionUrl());
            assertEquals("PDF", document.getDocumentType());
            assertEquals("Test description", document.getDocumentDescription());
            assertEquals("1024KB", document.getFileSize());
            assertEquals("https://example.com/document.pdf", document.getFileUrl());
            assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), document.getUploadAt());
            assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), document.getUpdateAt());
        }

        @Test
        @DisplayName("Should create Document with minimal required fields using builder")
        void shouldCreateDocumentWithMinimalFieldsUsingBuilder() {
            // Given & When
            Document document = Document.builder()
                    .title("Minimal Document")
                    .institutionName("Minimal Institution")
                    .institutionType("College")
                    .documentType("DOC")
                    .fileUrl("https://example.com/minimal.doc")
                    .build();

            // Then
            assertNotNull(document);
            assertEquals("Minimal Document", document.getTitle());
            assertEquals("Minimal Institution", document.getInstitutionName());
            assertEquals("College", document.getInstitutionType());
            assertEquals("DOC", document.getDocumentType());
            assertEquals("https://example.com/minimal.doc", document.getFileUrl());
            assertNull(document.getInstitutionUrl());
            assertNull(document.getDocumentDescription());
            assertNull(document.getFileSize());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id correctly")
        void shouldSetAndGetId() {
            // Given
            Long expectedId = 123L;

            // When
            document.setId(expectedId);

            // Then
            assertEquals(expectedId, document.getId());
        }

        @Test
        @DisplayName("Should set and get title correctly")
        void shouldSetAndGetTitle() {
            // Given
            String expectedTitle = "Test Document Title";

            // When
            document.setTitle(expectedTitle);

            // Then
            assertEquals(expectedTitle, document.getTitle());
        }

        @Test
        @DisplayName("Should set and get institutionName correctly")
        void shouldSetAndGetInstitutionName() {
            // Given
            String expectedInstitutionName = "Harvard University";

            // When
            document.setInstitutionName(expectedInstitutionName);

            // Then
            assertEquals(expectedInstitutionName, document.getInstitutionName());
        }

        @Test
        @DisplayName("Should set and get institutionType correctly")
        void shouldSetAndGetInstitutionType() {
            // Given
            String expectedInstitutionType = "University";

            // When
            document.setInstitutionType(expectedInstitutionType);

            // Then
            assertEquals(expectedInstitutionType, document.getInstitutionType());
        }

        @Test
        @DisplayName("Should set and get institutionUrl correctly")
        void shouldSetAndGetInstitutionUrl() {
            // Given
            String expectedUrl = "https://harvard.edu";

            // When
            document.setInstitutionUrl(expectedUrl);

            // Then
            assertEquals(expectedUrl, document.getInstitutionUrl());
        }

        @Test
        @DisplayName("Should set and get documentType correctly")
        void shouldSetAndGetDocumentType() {
            // Given
            String expectedDocumentType = "PDF";

            // When
            document.setDocumentType(expectedDocumentType);

            // Then
            assertEquals(expectedDocumentType, document.getDocumentType());
        }

        @Test
        @DisplayName("Should set and get uploadAt correctly")
        void shouldSetAndGetUploadAt() {
            // Given
            LocalDateTime expectedUploadAt = LocalDateTime.of(2024, 1, 1, 12, 0);

            // When
            document.setUploadAt(expectedUploadAt);

            // Then
            assertEquals(expectedUploadAt, document.getUploadAt());
        }

        @Test
        @DisplayName("Should set and get updateAt correctly")
        void shouldSetAndGetUpdateAt() {
            // Given
            LocalDateTime expectedUpdateAt = LocalDateTime.of(2024, 1, 2, 12, 0);

            // When
            document.setUpdateAt(expectedUpdateAt);

            // Then
            assertEquals(expectedUpdateAt, document.getUpdateAt());
        }

        @Test
        @DisplayName("Should set and get documentDescription correctly")
        void shouldSetAndGetDocumentDescription() {
            // Given
            String expectedDescription = "This is a detailed document description with multiple lines.";

            // When
            document.setDocumentDescription(expectedDescription);

            // Then
            assertEquals(expectedDescription, document.getDocumentDescription());
        }

        @Test
        @DisplayName("Should set and get fileSize correctly")
        void shouldSetAndGetFileSize() {
            // Given
            String expectedFileSize = "2048KB";

            // When
            document.setFileSize(expectedFileSize);

            // Then
            assertEquals(expectedFileSize, document.getFileSize());
        }

        @Test
        @DisplayName("Should set and get fileUrl correctly")
        void shouldSetAndGetFileUrl() {
            // Given
            String expectedFileUrl = "https://example.com/documents/sample.pdf";

            // When
            document.setFileUrl(expectedFileUrl);

            // Then
            assertEquals(expectedFileUrl, document.getFileUrl());
        }
    }

    @Nested
    @DisplayName("JPA Lifecycle Methods Tests")
    class LifecycleMethodsTests {

        @Test
        @DisplayName("Should set uploadAt and updateAt on onCreate when they are null")
        void shouldSetTimestampsOnCreateWhenNull() {
            // Given
            document.setUploadAt(null);
            document.setUpdateAt(null);
            LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);

            // When
            document.onCreate();

            // Then
            LocalDateTime afterCall = LocalDateTime.now().plusSeconds(1);
            assertNotNull(document.getUploadAt());
            assertNotNull(document.getUpdateAt());
            assertTrue(document.getUploadAt().isAfter(beforeCall));
            assertTrue(document.getUploadAt().isBefore(afterCall));
            assertTrue(document.getUpdateAt().isAfter(beforeCall));
            assertTrue(document.getUpdateAt().isBefore(afterCall));
        }

        @Test
        @DisplayName("Should not override existing uploadAt on onCreate")
        void shouldNotOverrideExistingUploadAtOnCreate() {
            // Given
            LocalDateTime existingUploadAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            document.setUploadAt(existingUploadAt);
            document.setUpdateAt(null);

            // When
            document.onCreate();

            // Then
            assertEquals(existingUploadAt, document.getUploadAt());
            assertNotNull(document.getUpdateAt());
        }

        @Test
        @DisplayName("Should not override existing updateAt on onCreate")
        void shouldNotOverrideExistingUpdateAtOnCreate() {
            // Given
            LocalDateTime existingUpdateAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            document.setUploadAt(null);
            document.setUpdateAt(existingUpdateAt);

            // When
            document.onCreate();

            // Then
            assertNotNull(document.getUploadAt());
            assertEquals(existingUpdateAt, document.getUpdateAt());
        }

        @Test
        @DisplayName("Should update updateAt on onUpdate")
        void shouldUpdateTimestampOnUpdate() {
            // Given
            LocalDateTime oldUpdateAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            document.setUpdateAt(oldUpdateAt);
            LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);

            // When
            document.onUpdate();

            // Then
            LocalDateTime afterCall = LocalDateTime.now().plusSeconds(1);
            assertNotNull(document.getUpdateAt());
            assertTrue(document.getUpdateAt().isAfter(beforeCall));
            assertTrue(document.getUpdateAt().isBefore(afterCall));
            assertNotEquals(oldUpdateAt, document.getUpdateAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are same")
        void shouldBeEqualWhenAllFieldsSame() {
            // Given
            Document document1 = createTestDocument();
            Document document2 = createTestDocument();

            // When & Then
            assertEquals(document1, document2);
            assertEquals(document1.hashCode(), document2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsDifferent() {
            // Given
            Document document1 = createTestDocument();
            Document document2 = createTestDocument();
            document2.setId(999L);

            // When & Then
            assertNotEquals(document1, document2);
        }

        @Test
        @DisplayName("Should not be equal when titles are different")
        void shouldNotBeEqualWhenTitlesDifferent() {
            // Given
            Document document1 = createTestDocument();
            Document document2 = createTestDocument();
            document2.setTitle("Different Title");

            // When & Then
            assertNotEquals(document1, document2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Document document1 = createTestDocument();

            // When & Then
            assertNotEquals(document1, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            Document document1 = createTestDocument();
            String differentObject = "different";

            // When & Then
            assertNotEquals(document1, differentObject);
        }

        private Document createTestDocument() {
            return Document.builder()
                    .id(1L)
                    .title("Test Document")
                    .institutionName("Test Institution")
                    .institutionType("University")
                    .institutionUrl("https://test.edu")
                    .documentType("PDF")
                    .documentDescription("Test description")
                    .fileSize("1024KB")
                    .fileUrl("https://example.com/document.pdf")
                    .uploadAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                    .updateAt(LocalDateTime.of(2024, 1, 1, 12, 0))
                    .build();
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return non-null string representation")
        void shouldReturnNonNullStringRepresentation() {
            // Given
            document.setTitle("Test Document");
            document.setInstitutionName("Test Institution");

            // When
            String documentString = document.toString();

            // Then
            assertNotNull(documentString);
            assertTrue(documentString.contains("Document"));
        }

        @Test
        @DisplayName("Should contain key field values in string representation")
        void shouldContainKeyFieldsInStringRepresentation() {
            // Given
            document.setTitle("Test Document");
            document.setInstitutionName("Test Institution");
            document.setDocumentType("PDF");

            // When
            String documentString = document.toString();

            // Then
            assertTrue(documentString.contains("Test Document"));
            assertTrue(documentString.contains("Test Institution"));
            assertTrue(documentString.contains("PDF"));
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should handle long document descriptions")
        void shouldHandleLongDocumentDescriptions() {
            // Given
            String longDescription = "This is a very long document description that spans multiple lines and contains detailed information about the document content, its purpose, and its significance in the context of the institution.";

            // When
            document.setDocumentDescription(longDescription);

            // Then
            assertEquals(longDescription, document.getDocumentDescription());
        }

        @Test
        @DisplayName("Should handle different file sizes")
        void shouldHandleDifferentFileSizes() {
            // Given
            String[] fileSizes = {"1KB", "1024KB", "5MB", "100MB", "1GB"};

            for (String fileSize : fileSizes) {
                // When
                document.setFileSize(fileSize);

                // Then
                assertEquals(fileSize, document.getFileSize());
            }
        }

        @Test
        @DisplayName("Should handle different document types")
        void shouldHandleDifferentDocumentTypes() {
            // Given
            String[] documentTypes = {"PDF", "DOC", "DOCX", "PPT", "PPTX", "XLS", "XLSX", "TXT"};

            for (String docType : documentTypes) {
                // When
                document.setDocumentType(docType);

                // Then
                assertEquals(docType, document.getDocumentType());
            }
        }

        @Test
        @DisplayName("Should handle different institution types")
        void shouldHandleDifferentInstitutionTypes() {
            // Given
            String[] institutionTypes = {"University", "College", "School", "Institute", "Academy", "Center"};

            for (String instType : institutionTypes) {
                // When
                document.setInstitutionType(instType);

                // Then
                assertEquals(instType, document.getInstitutionType());
            }
        }

        @Test
        @DisplayName("Should handle very long file URLs")
        void shouldHandleVeryLongFileUrls() {
            // Given
            StringBuilder longUrl = new StringBuilder("https://example.com/documents/");
            // Create a URL close to the 1500 character limit
            for (int i = 0; i < 140; i++) {
                longUrl.append("directory/");
            }
            longUrl.append("document.pdf");
            String fileUrl = longUrl.toString();

            // When
            document.setFileUrl(fileUrl);

            // Then
            assertEquals(fileUrl, document.getFileUrl());
            assertTrue(document.getFileUrl().length() <= 1500); // Verify it's within database limit
        }

        @Test
        @DisplayName("Should handle null optional fields gracefully")
        void shouldHandleNullOptionalFieldsGracefully() {
            // Given & When
            document.setInstitutionUrl(null);
            document.setDocumentDescription(null);
            document.setFileSize(null);

            // Then
            assertNull(document.getInstitutionUrl());
            assertNull(document.getDocumentDescription());
            assertNull(document.getFileSize());
        }
    }
}