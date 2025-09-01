package com.moneymate.documentationManagement.entities.concretes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    
    @Column(name = "minio_file_name", nullable = false)
    private String minioFileName;

    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    @Column(name = "institution_type", nullable = false)
    private String institutionType;

    @Column(name = "institution_url")
    private String institutionUrl;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "upload_at")
    private LocalDateTime uploadAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "document_description", columnDefinition = "TEXT")
    private String documentDescription;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "file_url", nullable = false, length = 1500)
    private String fileUrl;

    public Document(String title, String minioFileName,String institutionName, String institutionType,
                    String institutionUrl, String documentType, String documentDescription,
                    String fileSize, String fileUrl) {
        this.title = title;
        this.minioFileName = minioFileName;
        this.institutionName = institutionName;
        this.institutionType = institutionType;
        this.institutionUrl = institutionUrl;
        this.documentType = documentType;
        this.documentDescription = documentDescription;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.uploadAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (uploadAt == null) {
            uploadAt = LocalDateTime.now();
        }
        if (updateAt == null) {
            updateAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}