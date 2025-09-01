package com.moneymate.documentationManagement.business.requests;

import com.moneymate.documentationManagement.core.utilities.Messages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Döküman güncelleme isteği")
public class DocumentUpdateRequest {
    
    @Schema(
        description = "Döküman başlığı",
        example = "2024 Güncellenmiş Faaliyet Raporu"
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
        description = "Döküman tipi",
        example = "Faaliyet Raporu"
    )
    @NotNull(message = Messages.DocumentTypeNotValid)
    @NotEmpty(message = Messages.DocumentTypeNotValid)
    private String documentType;
    
    @Schema(
        description = "Döküman açıklaması",
        example = "TCMB'nin güncellenmiş 2024 yılı faaliyet raporu ve revize edilmiş finansal durum özeti"
    )
    @Size(max = 500, message = Messages.DocumentDescriptionNotValid)
    private String documentDescription;
}