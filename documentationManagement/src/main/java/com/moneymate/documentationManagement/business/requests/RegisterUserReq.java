package com.moneymate.documentationManagement.business.requests;

import com.moneymate.documentationManagement.core.utilities.Messages;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Kullanıcı kayıt isteği")
public class RegisterUserReq {
    
    @Schema(
        description = "Kullanıcının adı",
        example = "Ahmet",
        minLength = 2,
        maxLength = 50
    )
    @NotNull(message = Messages.FirstNameNotValid)
    @NotEmpty(message = Messages.FirstNameNotValid)
    @Size(min = 2, max = 50, message = Messages.FirstNameNotValid)
    @Pattern(regexp = "^[A-Za-zÇçĞğİıÖöŞşÜü\\s]+$", message = Messages.FirstNameNotValid)
    private String firstName;
    
    @Schema(
        description = "Kullanıcının soyadı",
        example = "Yılmaz",
        minLength = 2,
        maxLength = 50
    )
    @NotNull(message = Messages.LastNameNotValid)
    @NotEmpty(message = Messages.LastNameNotValid)
    @Size(min = 2, max = 50, message = Messages.LastNameNotValid)
    @Pattern(regexp = "^[A-Za-zÇçĞğİıÖöŞşÜü\\s]+$", message = Messages.LastNameNotValid)
    private String lastName;
    
    @Schema(
        description = "Kullanıcı email adresi",
        example = "ahmet.yilmaz@gmail.com",
        maxLength = 100
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