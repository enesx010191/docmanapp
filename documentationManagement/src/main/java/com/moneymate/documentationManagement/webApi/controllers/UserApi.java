package com.moneymate.documentationManagement.webApi.controllers;

import static com.moneymate.documentationManagement.core.utilities.exceptions.ResponseEntityBuilder.fromResult;
import static com.moneymate.documentationManagement.core.utilities.exceptions.ResponseEntityBuilder.fromDataResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneymate.documentationManagement.business.abstracts.UserService;
import com.moneymate.documentationManagement.business.requests.LoginUserReq;
import com.moneymate.documentationManagement.business.requests.RegisterUserReq;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Kullanıcı kimlik doğrulama API'leri")
public class UserApi {
    
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Kullanıcı kaydı")
    public ResponseEntity<?> register(@RequestBody RegisterUserReq registerUserReq) {
        Result result = userService.register(registerUserReq);
        return fromResult(result);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Kullanıcı girişi")
    public ResponseEntity<?> login(@RequestBody LoginUserReq loginUserReq) {
        var result = userService.login(loginUserReq);
        return fromDataResult(result);
    }
}