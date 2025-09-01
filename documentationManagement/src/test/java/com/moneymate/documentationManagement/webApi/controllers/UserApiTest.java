package com.moneymate.documentationManagement.webApi.controllers;

import com.moneymate.documentationManagement.business.abstracts.UserService;
import com.moneymate.documentationManagement.business.requests.LoginUserReq;
import com.moneymate.documentationManagement.business.requests.RegisterUserReq;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;
import com.moneymate.documentationManagement.business.responses.LoginUserRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserApiTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserApi userApi;

    private RegisterUserReq registerUserReq;
    private LoginUserReq loginUserReq;
    private LoginUserRes mockLoginUserRes;  
    private DataResult<LoginUserRes> mockDataResult;

    @BeforeEach
    public void setUp() {
        // Test öncesi gerekli nesnelerin oluşturulması
        registerUserReq = new RegisterUserReq();
        loginUserReq = new LoginUserReq();
        mockLoginUserRes = mock(LoginUserRes.class);  // LoginUserRes türünde mock nesnesi
        mockDataResult = mock(DataResult.class);  // DataResult<LoginUserRes> türünde mock nesnesi
    }

    // Register Testleri

    @Test
    public void testRegister_Success() {
        // Arrange
        when(userService.register(any(RegisterUserReq.class))).thenReturn(new DataResult<>(mockLoginUserRes, true));
        when(mockDataResult.isSuccess()).thenReturn(true);  // DataResult üzerinden başarı durumu kontrolü

        // Act
        ResponseEntity<?> response = userApi.register(registerUserReq);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).register(registerUserReq);  // userService'in register metodunun bir kez çağrıldığını doğrula
    }

    @Test
    public void testRegister_Failure() {
        // Arrange
        when(userService.register(any(RegisterUserReq.class))).thenReturn(new DataResult<>(mockLoginUserRes, false));
        when(mockDataResult.isSuccess()).thenReturn(false);  // DataResult üzerinden başarısız durum kontrolü

        // Act
        ResponseEntity<?> response = userApi.register(registerUserReq);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, times(1)).register(registerUserReq);  // userService'in register metodunun bir kez çağrıldığını doğrula
    }

    // Login Testleri

    @Test
    public void testLogin_Success() {
        // Arrange
        when(userService.login(any(LoginUserReq.class))).thenReturn(new DataResult<>(mockLoginUserRes, true));
        when(mockDataResult.isSuccess()).thenReturn(true);  // DataResult üzerinden başarı durumu kontrolü

        // Act
        ResponseEntity<?> response = userApi.login(loginUserReq);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).login(loginUserReq);  // userService'in login metodunun bir kez çağrıldığını doğrula
    }

    @Test
    public void testLogin_Failure() {
        // Arrange
        when(userService.login(any(LoginUserReq.class))).thenReturn(new DataResult<>(mockLoginUserRes, false));
        when(mockDataResult.isSuccess()).thenReturn(false);  // DataResult üzerinden başarısız durum kontrolü

        // Act
        ResponseEntity<?> response = userApi.login(loginUserReq);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, times(1)).login(loginUserReq);  // userService'in login metodunun bir kez çağrıldığını doğrula
    }
}
