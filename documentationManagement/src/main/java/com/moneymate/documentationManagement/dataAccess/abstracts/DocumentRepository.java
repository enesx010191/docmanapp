package com.moneymate.documentationManagement.dataAccess.abstracts;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moneymate.documentationManagement.entities.concretes.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByInstitutionName(String institutionName);

    List<Document> findByDocumentType(String documentType);

    List<Document> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT d FROM Document d WHERE d.institutionName = :institutionName AND d.documentType = :documentType")
    List<Document> findByInstitutionAndType(@Param("institutionName") String institutionName,
                                            @Param("documentType") String documentType);

    Optional<Document> findByFileUrl(String fileUrl);
}
