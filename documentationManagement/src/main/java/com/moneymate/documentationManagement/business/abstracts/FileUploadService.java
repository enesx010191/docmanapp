package com.moneymate.documentationManagement.business.abstracts;

import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.entities.concretes.Document;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface FileUploadService {
    DataResult<Document> uploadFileWithMetadata(MultipartFile file, String title, String institutionName,
                                    String institutionType, String institutionUrl,
                                    String documentType, String documentDescription);
    
    Result uploadFile(MultipartFile file);
    Result deleteFile(String fileName);
    Result getFileUrl(String fileName);
}
