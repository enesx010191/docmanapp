package com.moneymate.documentationManagement.business.requests;

import com.moneymate.documentationManagement.core.utilities.Messages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Kullanıcı giriş isteği")
public class LoginUserReq {

    @Schema(
        description = "Kullanıcı email adresi",
        example = "ahmet.yilmaz@gmail.com"
    )
    @NotNull(message = Messages.EmailNotValid)
    @NotEmpty(message = Messages.EmailNotValid)
    @Size(max = 100, message = Messages.EmailNotValid)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = Messages.EmailNotValid)
    private String email;

    @Schema(
        description = "Kullanıcı şifresi",
        example = "MySecurePassword123!"
    )
    @NotNull(message = Messages.PasswordNotValid)
    @NotEmpty(message = Messages.PasswordNotValid)
    private String password;
}