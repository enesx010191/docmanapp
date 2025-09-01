package com.moneymate.documentationManagement.business.requests;

import com.moneymate.documentationManagement.core.utilities.Messages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Döküman yükleme isteği")
public class DocumentUploadRequest {
    
    @Schema(
        description = "Yüklenecek dosya",
        type = "string",
        format = "binary"
    )
    @NotNull(message = Messages.FileNotValid)
    private MultipartFile file;
    
    @Schema(
        description = "Döküman başlığı",
        example = "2024 Yıllık Faaliyet Raporu"
    )
    @NotNull(message = Messages.DocumentTitleNotValid)
    @NotEmpty(message = Messages.DocumentTitleNotValid)
    @Size(min = 2, max = 200, message = Messages.DocumentTitleNotValid)
    private String title;
    
    @Schema(
        description = "Kurum adı",
        example = "Türkiye Cumhuriyet Merkez Bankası"
    )
    @NotNull(message = Messages.InstitutionNameNotValid)
    @NotEmpty(message = Messages.InstitutionNameNotValid)
    @Size(min = 2, max = 100, message = Messages.InstitutionNameNotValid)
    private String institutionName;
    
    @Schema(
        description = "Kurum tipi",
        example = "Merkez Bankası"
    )
    @NotNull(message = Messages.InstitutionTypeNotValid)
    @NotEmpty(message = Messages.InstitutionTypeNotValid)
    @Size(min = 2, max = 50, message = Messages.InstitutionTypeNotValid)
    private String institutionType;
    
    @Schema(
        description = "Kurum web sitesi URL'i",
        example = "https://www.tcmb.gov.tr"
    )
    @NotNull(message = Messages.InstitutionUrlNotValid)
    @NotEmpty(message = Messages.InstitutionUrlNotValid)
    @Pattern(regexp = "^(https?)://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?$", 
             message = Messages.InstitutionUrlNotValid)
    private String institutionUrl;
    
    @Schema(
        description = "Döküman tip ID'si",
        example = "1"
    )
    @NotNull(message = Messages.DocumentTypeNotValid)
    private Long documentType;
    
    @Schema(
        description = "Döküman açıklaması",
        example = "TCMB'nin 2024 yılı faaliyet raporu ve finansal durum özeti"
    )
    @Size(max = 500, message = Messages.DocumentDescriptionNotValid)
    private String documentDescription;
}