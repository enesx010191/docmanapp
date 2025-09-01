package com.moneymate.documentationManagement.business.concretes;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moneymate.documentationManagement.business.abstracts.DocumentService;
import com.moneymate.documentationManagement.business.abstracts.FileUploadService;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorDataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.SuccessResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.ErrorResult;
import com.moneymate.documentationManagement.entities.concretes.Document;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;

@Service
public class FileUploadManager implements FileUploadService {

    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private DocumentService documentService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public DataResult<Document> uploadFileWithMetadata(MultipartFile file, String title, String institutionName,
                                           String institutionType, String institutionUrl,
                                           String documentType, String documentDescription) {
        try {
            // 1. Dosyayı MinIO'ya yükle
            DataResult<String> uploadResult = uploadFile(file);
            if (!uploadResult.isSuccess()) {
                return new ErrorDataResult<>("Dosya yüklenemedi: " + uploadResult.getMessage());
            }
            
            String fileName = uploadResult.getData();
            
            // 2. Dosya URL'ini al
            DataResult<String> urlResult = getFileUrl(fileName);
            if (!urlResult.isSuccess()) {
                return new ErrorDataResult<>("Dosya URL'i alınamadı: " + urlResult.getMessage());
            }
            
            String fileUrl = urlResult.getData();
            String fileSize = formatFileSize(file.getSize());

            // 3. Document entity'sini oluştur
            Document document = new Document(
                    title,
                    fileName,
                    institutionName,
                    institutionType,
                    institutionUrl,
                    documentType,
                    documentDescription,
                    fileSize,
                    fileUrl
            );

            // 4. Veritabanına kaydet
            return documentService.saveDocument(document);
            
        } catch (Exception e) {
            return new ErrorDataResult<>("Dosya yükleme işlemi başarısız: " + e.getMessage());
        }
    }

    @Override
    public DataResult<String> uploadFile(MultipartFile file) {
        try {
            createBucketIfNotExists();
            String fileName = generateFileName(file.getOriginalFilename());

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                return new SuccessDataResult<>(fileName, "Dosya başarıyla yüklendi");
            }
        } catch (Exception e) {
            return new ErrorDataResult<>("Dosya yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    public Result deleteFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return new ErrorResult("Dosya adı geçersiz");
        }
        
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            return new SuccessResult("Dosya başarıyla silindi");
        } catch (Exception e) {
            return new ErrorResult("Dosya silinemedi: " + e.getMessage());
        }
    }

    @Override
    public DataResult<String> getFileUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return new ErrorDataResult<>("Dosya adı boş olamaz");
        }
        
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(60 * 60 * 24)
                            .build()
            );
            return new SuccessDataResult<>(url, "URL başarıyla oluşturuldu");
        } catch (Exception e) {
            return new ErrorDataResult<>("URL oluşturulamadı: " + e.getMessage());
        }
    }
    
    private void createBucketIfNotExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
}
