package com.moneymate.documentationManagement.business.requests;

import com.moneymate.documentationManagement.core.utilities.Messages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Döküman arama isteği")
public class DocumentSearchRequest {
    
    @Schema(
        description = "Aranacak döküman başlığı (kısmi eşleşme desteklenir)",
        example = "faaliyet raporu"
    )
    @NotNull(message = Messages.SearchTitleNotValid)
    @NotEmpty(message = Messages.SearchTitleNotValid)
    @Size(min = 2, max = 200, message = Messages.SearchTitleNotValid)
    private String title;
}
