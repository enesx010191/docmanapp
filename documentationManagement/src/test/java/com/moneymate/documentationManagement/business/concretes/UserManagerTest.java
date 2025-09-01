/*
 * package com.moneymate.documentationManagement.business.concretes;
 * 
 * import static org.junit.jupiter.api.Assertions.*; import static
 * org.mockito.ArgumentMatchers.*; import static org.mockito.Mockito.*;
 * 
 * import java.time.LocalDateTime; import java.util.Optional;
 * 
 * import org.junit.jupiter.api.BeforeEach; import
 * org.junit.jupiter.api.DisplayName; import org.junit.jupiter.api.Nested;
 * import org.junit.jupiter.api.Test; import
 * org.junit.jupiter.api.extension.ExtendWith; import org.mockito.InjectMocks;
 * import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension;
 * import org.modelmapper.ModelMapper; import
 * org.springframework.security.crypto.password.PasswordEncoder;
 * 
 * import com.moneymate.documentationManagement.business.requests.LoginUserReq;
 * import
 * com.moneymate.documentationManagement.business.requests.RegisterUserReq;
 * import com.moneymate.documentationManagement.business.responses.LoginUserRes;
 * import com.moneymate.documentationManagement.core.utilities.Messages; import
 * com.moneymate.documentationManagement.core.utilities.SecurityConfig.
 * JwtService; import
 * com.moneymate.documentationManagement.core.utilities.exceptions.Results.
 * DataResult; import
 * com.moneymate.documentationManagement.core.utilities.exceptions.Results.
 * Result; import com.moneymate.documentationManagement.core.utilities.mappers.
 * ModelMapperConfig; import
 * com.moneymate.documentationManagement.dataAccess.abstracts.UserRepository;
 * import com.moneymate.documentationManagement.entities.concretes.User;
 * 
 * import jakarta.servlet.http.HttpServletRequest;
 * 
 * @ExtendWith(MockitoExtension.class)
 * 
 * @DisplayName("UserManager Service Tests") class UserManagerTest {
 * 
 * @Mock private UserRepository userRepository;
 * 
 * @Mock private ModelMapperConfig modelMapperService;
 * 
 * @Mock private ModelMapper modelMapper;
 * 
 * @Mock private HttpServletRequest request;
 * 
 * @Mock private PasswordEncoder passwordEncoder;
 * 
 * @Mock private JwtService jwtService;
 * 
 * @InjectMocks private UserManager userManager;
 * 
 * private RegisterUserReq registerUserReq; private LoginUserReq loginUserReq;
 * private User testUser;
 * 
 * @BeforeEach void setUp() { // Setup test data registerUserReq = new
 * RegisterUserReq(); registerUserReq.setEmail("test@example.com");
 * registerUserReq.setPassword("plainPassword123");
 * registerUserReq.setFirstName("John"); registerUserReq.setLastName("Doe");
 * 
 * loginUserReq = new LoginUserReq(); loginUserReq.setEmail("test@example.com");
 * loginUserReq.setPassword("plainPassword123");
 * 
 * testUser = new User(); testUser.setId(1L);
 * testUser.setUserId("user-uuid-123"); testUser.setEmail("test@example.com");
 * testUser.setPassword("hashedPassword123"); testUser.setFirstName("John");
 * testUser.setLastName("Doe"); testUser.setIsActive(true);
 * testUser.setStatus(Byte.valueOf((byte) 1));
 * testUser.setLoginAttemptCount(Byte.valueOf((byte) 0));
 * testUser.setCreatedAt(LocalDateTime.now());
 * testUser.setUpdateAt(LocalDateTime.now());
 * 
 * // Setup common mocks
 * when(modelMapperService.modelMapper()).thenReturn(modelMapper); }
 * 
 * @Nested
 * 
 * @DisplayName("User Registration Tests") class UserRegistrationTests {
 * 
 * @Test
 * 
 * @DisplayName("Should register user successfully when email does not exist")
 * void shouldRegisterUserSuccessfullyWhenEmailDoesNotExist() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getHeader("X-Forwarded-For")).thenReturn(null);
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When Result result = userManager.register(registerUserReq);
 * 
 * // Then assertTrue(result.isSuccess()); assertEquals(Messages.UserCreated,
 * result.getMessage());
 * 
 * verify(userRepository).findByEmail(registerUserReq.getEmail());
 * verify(modelMapper).map(registerUserReq, User.class);
 * verify(passwordEncoder).encode(registerUserReq.getPassword());
 * verify(userRepository).save(any(User.class));
 * 
 * // Verify user properties are set correctly
 * verify(userRepository).save(argThat(user -> { return
 * user.getPassword().equals("hashedPassword123") && user.getUserId() != null &&
 * user.getCreatedAt() != null && user.getUpdateAt() != null &&
 * user.getCreatedIp().equals("192.168.1.100") && user.getStatus().equals((byte)
 * 1) && user.getLoginAttemptCount().equals((byte) 0) && user.getIsActive() ==
 * true && user.getIsMultipleSession() == false && user.getFirstLoginStatus() ==
 * true && user.getPasswordCreateAt() != null && user.getPasswordUpdateAt() !=
 * null; })); }
 * 
 * @Test
 * 
 * @DisplayName("Should return error when email already exists") void
 * shouldReturnErrorWhenEmailAlreadyExists() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.of(testUser));
 * 
 * // When Result result = userManager.register(registerUserReq);
 * 
 * // Then assertFalse(result.isSuccess());
 * assertEquals(Messages.AlreadyExistUser, result.getMessage());
 * 
 * verify(userRepository).findByEmail(registerUserReq.getEmail());
 * verify(modelMapper, never()).map(any(), any()); verify(passwordEncoder,
 * never()).encode(any()); verify(userRepository, never()).save(any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle X-Forwarded-For header for IP address") void
 * shouldHandleXForwardedForHeaderForIpAddress() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123"); when(request.getHeader("X-Forwarded-For")).
 * thenReturn("203.0.113.1, 198.51.100.1");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When Result result = userManager.register(registerUserReq);
 * 
 * // Then assertTrue(result.isSuccess());
 * 
 * verify(userRepository).save(argThat(user ->
 * user.getCreatedIp().equals("203.0.113.1") )); }
 * 
 * @Test
 * 
 * @DisplayName("Should set unique userId for each registration") void
 * shouldSetUniqueUserIdForEachRegistration() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(userRepository).save(argThat(user -> { String userId =
 * user.getUserId(); return userId != null && userId.length() == 36 && // UUID
 * format length userId.contains("-"); // UUID format contains hyphens })); }
 * 
 * @Test
 * 
 * @DisplayName("Should hash password correctly") void
 * shouldHashPasswordCorrectly() { // Given String plainPassword =
 * "mySecretPassword123"; String hashedPassword = "hashedPassword456";
 * 
 * registerUserReq.setPassword(plainPassword);
 * 
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(plainPassword)).thenReturn(hashedPassword);
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When Result result = userManager.register(registerUserReq);
 * 
 * // Then assertTrue(result.isSuccess());
 * verify(passwordEncoder).encode(plainPassword);
 * verify(userRepository).save(argThat(user ->
 * user.getPassword().equals(hashedPassword) )); }
 * 
 * @Test
 * 
 * @DisplayName("Should set default user properties correctly") void
 * shouldSetDefaultUserPropertiesCorrectly() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(userRepository).save(argThat(user -> { return
 * user.getStatus().equals((byte) 1) &&
 * user.getLoginAttemptCount().equals((byte) 0) && user.getIsActive() == true &&
 * user.getIsMultipleSession() == false && user.getFirstLoginStatus() == true;
 * })); } }
 * 
 * @Nested
 * 
 * @DisplayName("User Login Tests") class UserLoginTests {
 * 
 * @Test
 * 
 * @DisplayName("Should login successfully with correct credentials") void
 * shouldLoginSuccessfullyWithCorrectCredentials() { // Given String jwtToken =
 * "jwt.token.here"; LoginUserRes expectedResponse = new LoginUserRes(jwtToken);
 * 
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(loginUserReq.getPassword(),
 * testUser.getPassword())).thenReturn(true);
 * when(jwtService.generateToken(testUser.getEmail(), testUser.getFirstName(),
 * testUser.getLastName())) .thenReturn(jwtToken);
 * 
 * // When DataResult<LoginUserRes> result = userManager.login(loginUserReq);
 * 
 * // Then assertTrue(result.isSuccess());
 * assertEquals(Messages.LoginSuccessful, result.getMessage());
 * assertNotNull(result.getData()); assertEquals(jwtToken,
 * result.getData().getToken());
 * 
 * verify(userRepository).findByEmail(loginUserReq.getEmail());
 * verify(passwordEncoder).matches(loginUserReq.getPassword(),
 * testUser.getPassword());
 * verify(jwtService).generateToken(testUser.getEmail(),
 * testUser.getFirstName(), testUser.getLastName()); }
 * 
 * @Test
 * 
 * @DisplayName("Should return error when user does not exist") void
 * shouldReturnErrorWhenUserDoesNotExist() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .empty());
 * 
 * // When DataResult<LoginUserRes> result = userManager.login(loginUserReq);
 * 
 * // Then assertFalse(result.isSuccess());
 * assertEquals(Messages.LoginInformationIsIncorrect, result.getMessage());
 * assertNotNull(result.getData()); // Note: LoginUserRes(401) constructor sets
 * status code internally // We can't access it directly, but we verify the
 * error response exists
 * 
 * verify(userRepository).findByEmail(loginUserReq.getEmail());
 * verify(passwordEncoder, never()).matches(any(), any()); verify(jwtService,
 * never()).generateToken(any(), any(), any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should return error when password is incorrect") void
 * shouldReturnErrorWhenPasswordIsIncorrect() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(loginUserReq.getPassword(),
 * testUser.getPassword())).thenReturn(false);
 * 
 * // When DataResult<LoginUserRes> result = userManager.login(loginUserReq);
 * 
 * // Then assertFalse(result.isSuccess());
 * assertEquals(Messages.LoginInformationIsIncorrect, result.getMessage());
 * assertNotNull(result.getData()); // Note: LoginUserRes(401) constructor sets
 * status code internally
 * 
 * verify(userRepository).findByEmail(loginUserReq.getEmail());
 * verify(passwordEncoder).matches(loginUserReq.getPassword(),
 * testUser.getPassword()); verify(jwtService, never()).generateToken(any(),
 * any(), any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle null email in login request") void
 * shouldHandleNullEmailInLoginRequest() { // Given loginUserReq.setEmail(null);
 * when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
 * 
 * // When DataResult<LoginUserRes> result = userManager.login(loginUserReq);
 * 
 * // Then assertFalse(result.isSuccess());
 * assertEquals(Messages.LoginInformationIsIncorrect, result.getMessage());
 * assertNotNull(result.getData()); // Note: Error response with 401 status code
 * is created internally }
 * 
 * @Test
 * 
 * @DisplayName("Should handle null password in login request") void
 * shouldHandleNullPasswordInLoginRequest() { // Given
 * loginUserReq.setPassword(null);
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(null,
 * testUser.getPassword())).thenReturn(false);
 * 
 * // When DataResult<LoginUserRes> result = userManager.login(loginUserReq);
 * 
 * // Then assertFalse(result.isSuccess());
 * assertEquals(Messages.LoginInformationIsIncorrect, result.getMessage());
 * assertNotNull(result.getData()); // Note: Error response with 401 status code
 * is created internally }
 * 
 * @Test
 * 
 * @DisplayName("Should generate JWT token with correct user information") void
 * shouldGenerateJwtTokenWithCorrectUserInformation() { // Given String
 * expectedToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.test.token";
 * 
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(loginUserReq.getPassword(),
 * testUser.getPassword())).thenReturn(true);
 * when(jwtService.generateToken(testUser.getEmail(), testUser.getFirstName(),
 * testUser.getLastName())) .thenReturn(expectedToken);
 * 
 * // When DataResult<LoginUserRes> result = userManager.login(loginUserReq);
 * 
 * // Then assertTrue(result.isSuccess()); assertEquals(expectedToken,
 * result.getData().getToken());
 * 
 * verify(jwtService).generateToken( eq(testUser.getEmail()),
 * eq(testUser.getFirstName()), eq(testUser.getLastName()) ); } }
 * 
 * @Nested
 * 
 * @DisplayName("IP Address Handling Tests") class IpAddressHandlingTests {
 * 
 * @Test
 * 
 * @DisplayName("Should get IP from X-Forwarded-For header when present") void
 * shouldGetIpFromXForwardedForHeaderWhenPresent() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123"); when(request.getHeader("X-Forwarded-For")).
 * thenReturn("203.0.113.1, 198.51.100.1, 192.168.1.1");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(request).getHeader("X-Forwarded-For"); verify(request,
 * never()).getRemoteAddr(); verify(userRepository).save(argThat(user ->
 * user.getCreatedIp().equals("203.0.113.1") // First IP in the chain )); }
 * 
 * @Test
 * 
 * @DisplayName("Should get IP from remote address when X-Forwarded-For is null"
 * ) void shouldGetIpFromRemoteAddressWhenXForwardedForIsNull() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getHeader("X-Forwarded-For")).thenReturn(null);
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(request).getHeader("X-Forwarded-For");
 * verify(request).getRemoteAddr(); verify(userRepository).save(argThat(user ->
 * user.getCreatedIp().equals("192.168.1.100") )); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle empty X-Forwarded-For header") void
 * shouldHandleEmptyXForwardedForHeader() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getHeader("X-Forwarded-For")).thenReturn("");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(userRepository).save(argThat(user ->
 * user.getCreatedIp().equals("") // Uses the empty header value )); } }
 * 
 * @Nested
 * 
 * @DisplayName("Security and Validation Tests") class
 * SecurityAndValidationTests {
 * 
 * @Test
 * 
 * @DisplayName("Should not store plain text password") void
 * shouldNotStorePlainTextPassword() { // Given String plainPassword =
 * "myPlainPassword123"; String hashedPassword = "$2a$10$hashedPasswordValue";
 * 
 * registerUserReq.setPassword(plainPassword);
 * 
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(plainPassword)).thenReturn(hashedPassword);
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(userRepository).save(argThat(user -> { // Ensure password is
 * hashed and not plain text return !user.getPassword().equals(plainPassword) &&
 * user.getPassword().equals(hashedPassword) &&
 * user.getPassword().startsWith("$2a$"); // BCrypt hash format })); }
 * 
 * @Test
 * 
 * @DisplayName("Should use secure password comparison for login") void
 * shouldUseSecurePasswordComparisonForLogin() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(loginUserReq.getPassword(),
 * testUser.getPassword())).thenReturn(true);
 * when(jwtService.generateToken(any(), any(), any())).thenReturn("token");
 * 
 * // When userManager.login(loginUserReq);
 * 
 * // Then // Verify that passwordEncoder.matches is used instead of direct
 * string comparison verify(passwordEncoder).matches(loginUserReq.getPassword(),
 * testUser.getPassword()); }
 * 
 * @Test
 * 
 * @DisplayName("Should set timestamps for password tracking") void
 * shouldSetTimestampsForPasswordTracking() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(userRepository).save(argThat(user -> { return
 * user.getPasswordCreateAt() != null && user.getPasswordUpdateAt() != null &&
 * !user.getPasswordCreateAt().isEmpty() &&
 * !user.getPasswordUpdateAt().isEmpty(); })); } }
 * 
 * @Nested
 * 
 * @DisplayName("Error Handling and Edge Cases Tests") class
 * ErrorHandlingAndEdgeCasesTests {
 * 
 * @Test
 * 
 * @DisplayName("Should handle repository exceptions during registration") void
 * shouldHandleRepositoryExceptionsDuringRegistration() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())) .thenThrow(new
 * RuntimeException("Database connection failed"));
 * 
 * // When & Then assertThrows(RuntimeException.class, () -> {
 * userManager.register(registerUserReq); });
 * 
 * verify(userRepository).findByEmail(registerUserReq.getEmail());
 * verify(userRepository, never()).save(any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle repository exceptions during login") void
 * shouldHandleRepositoryExceptionsDuringLogin() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())) .thenThrow(new
 * RuntimeException("Database connection failed"));
 * 
 * // When & Then assertThrows(RuntimeException.class, () -> {
 * userManager.login(loginUserReq); });
 * 
 * verify(userRepository).findByEmail(loginUserReq.getEmail());
 * verify(jwtService, never()).generateToken(any(), any(), any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle password encoder exceptions") void
 * shouldHandlePasswordEncoderExceptions() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())) .thenThrow(new
 * RuntimeException("Password encoding failed"));
 * 
 * // When & Then assertThrows(RuntimeException.class, () -> {
 * userManager.register(registerUserReq); });
 * 
 * verify(passwordEncoder).encode(registerUserReq.getPassword());
 * verify(userRepository, never()).save(any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle JWT service exceptions") void
 * shouldHandleJwtServiceExceptions() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(loginUserReq.getPassword(),
 * testUser.getPassword())).thenReturn(true);
 * when(jwtService.generateToken(testUser.getEmail(), testUser.getFirstName(),
 * testUser.getLastName())) .thenThrow(new
 * RuntimeException("JWT generation failed"));
 * 
 * // When & Then assertThrows(RuntimeException.class, () -> {
 * userManager.login(loginUserReq); });
 * 
 * verify(jwtService).generateToken(testUser.getEmail(),
 * testUser.getFirstName(), testUser.getLastName()); }
 * 
 * @Test
 * 
 * @DisplayName("Should handle model mapper exceptions") void
 * shouldHandleModelMapperExceptions() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq, User.class))
 * .thenThrow(new RuntimeException("Mapping failed"));
 * 
 * // When & Then assertThrows(RuntimeException.class, () -> {
 * userManager.register(registerUserReq); });
 * 
 * verify(modelMapper).map(registerUserReq, User.class); verify(userRepository,
 * never()).save(any()); } }
 * 
 * @Nested
 * 
 * @DisplayName("Integration and Method Interaction Tests") class
 * IntegrationAndMethodInteractionTests {
 * 
 * @Test
 * 
 * @DisplayName("Should verify correct method call order during registration")
 * void shouldVerifyCorrectMethodCallOrderDuringRegistration() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.empty()); when(modelMapper.map(registerUserReq,
 * User.class)).thenReturn(testUser);
 * when(passwordEncoder.encode(registerUserReq.getPassword())).thenReturn(
 * "hashedPassword123");
 * when(request.getRemoteAddr()).thenReturn("192.168.1.100");
 * when(userRepository.save(any(User.class))).thenReturn(testUser);
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then - Verify order of operations var inOrder = inOrder(userRepository,
 * modelMapper, passwordEncoder);
 * inOrder.verify(userRepository).findByEmail(registerUserReq.getEmail());
 * inOrder.verify(modelMapper).map(registerUserReq, User.class);
 * inOrder.verify(passwordEncoder).encode(registerUserReq.getPassword());
 * inOrder.verify(userRepository).save(any(User.class)); }
 * 
 * @Test
 * 
 * @DisplayName("Should verify correct method call order during login") void
 * shouldVerifyCorrectMethodCallOrderDuringLogin() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .of(testUser)); when(passwordEncoder.matches(loginUserReq.getPassword(),
 * testUser.getPassword())).thenReturn(true);
 * when(jwtService.generateToken(any(), any(), any())).thenReturn("token");
 * 
 * // When userManager.login(loginUserReq);
 * 
 * // Then - Verify order of operations var inOrder = inOrder(userRepository,
 * passwordEncoder, jwtService);
 * inOrder.verify(userRepository).findByEmail(loginUserReq.getEmail());
 * inOrder.verify(passwordEncoder).matches(loginUserReq.getPassword(),
 * testUser.getPassword());
 * inOrder.verify(jwtService).generateToken(testUser.getEmail(),
 * testUser.getFirstName(), testUser.getLastName()); }
 * 
 * @Test
 * 
 * @DisplayName("Should not call unnecessary methods when registration fails")
 * void shouldNotCallUnnecessaryMethodsWhenRegistrationFails() { // Given
 * when(userRepository.findByEmail(registerUserReq.getEmail())).thenReturn(
 * Optional.of(testUser));
 * 
 * // When userManager.register(registerUserReq);
 * 
 * // Then verify(userRepository).findByEmail(registerUserReq.getEmail());
 * verify(modelMapper, never()).map(any(), any()); verify(passwordEncoder,
 * never()).encode(any()); verify(userRepository, never()).save(any()); }
 * 
 * @Test
 * 
 * @DisplayName("Should not call unnecessary methods when login fails") void
 * shouldNotCallUnnecessaryMethodsWhenLoginFails() { // Given
 * when(userRepository.findByEmail(loginUserReq.getEmail())).thenReturn(Optional
 * .empty());
 * 
 * // When userManager.login(loginUserReq);
 * 
 * // Then verify(userRepository).findByEmail(loginUserReq.getEmail());
 * verify(passwordEncoder, never()).matches(any(), any()); verify(jwtService,
 * never()).generateToken(any(), any(), any()); } } }
 */