package com.moneymate.documentationManagement.entities;

import com.moneymate.documentationManagement.entities.concretes.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create User with no-args constructor")
        void shouldCreateUserWithNoArgsConstructor() {
            // Given & When
            User newUser = new User();

            // Then
            assertNotNull(newUser);
            assertNull(newUser.getId());
            assertNull(newUser.getUserId());
            assertNull(newUser.getEmail());
            assertTrue(newUser.getIsActive()); // default value
            assertEquals(Byte.valueOf((byte) 0), newUser.getLoginAttemptCount()); // default value
            assertFalse(newUser.getIsMultipleSession()); // default value
            assertTrue(newUser.getFirstLoginStatus()); // default value
        }

        @Test
        @DisplayName("Should create User with all-args constructor")
        void shouldCreateUserWithAllArgsConstructor() {
            // Given
            Long id = 1L;
            String userId = "USER001";
            String email = "test@example.com";
            String firstName = "John";
            String lastName = "Doe";
            Boolean isActive = true;
            LocalDateTime createdAt = LocalDateTime.now();
            String createdIp = "192.168.1.1";
            LocalDateTime updateAt = LocalDateTime.now();
            Byte status = 1;
            Byte loginAttemptCount = 0;
            LocalDateTime blockedUntil = null;
            Boolean isMultipleSession = false;
            Boolean firstLoginStatus = true;
            String password = "hashedPassword";
            String passwordCreateAt = "2024-01-01";
            String passwordUpdateAt = "2024-01-01";

            // When
            User newUser = new User(id, userId, email, firstName, lastName, 
                                  isActive, createdAt, createdIp, updateAt, 
                                  status, loginAttemptCount, blockedUntil, 
                                  isMultipleSession, firstLoginStatus, 
                                  password, passwordCreateAt, passwordUpdateAt);

            // Then
            assertNotNull(newUser);
            assertEquals(id, newUser.getId());
            assertEquals(userId, newUser.getUserId());
            assertEquals(email, newUser.getEmail());
            assertEquals(firstName, newUser.getFirstName());
            assertEquals(lastName, newUser.getLastName());
            assertEquals(isActive, newUser.getIsActive());
            assertEquals(createdAt, newUser.getCreatedAt());
            assertEquals(createdIp, newUser.getCreatedIp());
            assertEquals(updateAt, newUser.getUpdateAt());
            assertEquals(status, newUser.getStatus());
            assertEquals(loginAttemptCount, newUser.getLoginAttemptCount());
            assertEquals(blockedUntil, newUser.getBlockedUntil());
            assertEquals(isMultipleSession, newUser.getIsMultipleSession());
            assertEquals(firstLoginStatus, newUser.getFirstLoginStatus());
            assertEquals(password, newUser.getPassword());
            assertEquals(passwordCreateAt, newUser.getPasswordCreateAt());
            assertEquals(passwordUpdateAt, newUser.getPasswordUpdateAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id correctly")
        void shouldSetAndGetId() {
            // Given
            Long expectedId = 123L;

            // When
            user.setId(expectedId);

            // Then
            assertEquals(expectedId, user.getId());
        }

        @Test
        @DisplayName("Should set and get userId correctly")
        void shouldSetAndGetUserId() {
            // Given
            String expectedUserId = "USER001";

            // When
            user.setUserId(expectedUserId);

            // Then
            assertEquals(expectedUserId, user.getUserId());
        }

        @Test
        @DisplayName("Should set and get email correctly")
        void shouldSetAndGetEmail() {
            // Given
            String expectedEmail = "test@example.com";

            // When
            user.setEmail(expectedEmail);

            // Then
            assertEquals(expectedEmail, user.getEmail());
        }

        @Test
        @DisplayName("Should set and get firstName correctly")
        void shouldSetAndGetFirstName() {
            // Given
            String expectedFirstName = "John";

            // When
            user.setFirstName(expectedFirstName);

            // Then
            assertEquals(expectedFirstName, user.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName correctly")
        void shouldSetAndGetLastName() {
            // Given
            String expectedLastName = "Doe";

            // When
            user.setLastName(expectedLastName);

            // Then
            assertEquals(expectedLastName, user.getLastName());
        }

        @Test
        @DisplayName("Should set and get isActive correctly")
        void shouldSetAndGetIsActive() {
            // Given
            Boolean expectedIsActive = false;

            // When
            user.setIsActive(expectedIsActive);

            // Then
            assertEquals(expectedIsActive, user.getIsActive());
        }

        @Test
        @DisplayName("Should set and get createdAt correctly")
        void shouldSetAndGetCreatedAt() {
            // Given
            LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 1, 1, 12, 0);

            // When
            user.setCreatedAt(expectedCreatedAt);

            // Then
            assertEquals(expectedCreatedAt, user.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get createdIp correctly")
        void shouldSetAndGetCreatedIp() {
            // Given
            String expectedCreatedIp = "192.168.1.1";

            // When
            user.setCreatedIp(expectedCreatedIp);

            // Then
            assertEquals(expectedCreatedIp, user.getCreatedIp());
        }

        @Test
        @DisplayName("Should set and get updateAt correctly")
        void shouldSetAndGetUpdateAt() {
            // Given
            LocalDateTime expectedUpdateAt = LocalDateTime.of(2024, 1, 2, 12, 0);

            // When
            user.setUpdateAt(expectedUpdateAt);

            // Then
            assertEquals(expectedUpdateAt, user.getUpdateAt());
        }

        @Test
        @DisplayName("Should set and get status correctly")
        void shouldSetAndGetStatus() {
            // Given
            Byte expectedStatus = 1;

            // When
            user.setStatus(expectedStatus);

            // Then
            assertEquals(expectedStatus, user.getStatus());
        }

        @Test
        @DisplayName("Should set and get loginAttemptCount correctly")
        void shouldSetAndGetLoginAttemptCount() {
            // Given
            Byte expectedCount = 3;

            // When
            user.setLoginAttemptCount(expectedCount);

            // Then
            assertEquals(expectedCount, user.getLoginAttemptCount());
        }

        @Test
        @DisplayName("Should set and get blockedUntil correctly")
        void shouldSetAndGetBlockedUntil() {
            // Given
            LocalDateTime expectedBlockedUntil = LocalDateTime.of(2024, 1, 1, 18, 0);

            // When
            user.setBlockedUntil(expectedBlockedUntil);

            // Then
            assertEquals(expectedBlockedUntil, user.getBlockedUntil());
        }

        @Test
        @DisplayName("Should set and get isMultipleSession correctly")
        void shouldSetAndGetIsMultipleSession() {
            // Given
            Boolean expectedIsMultipleSession = true;

            // When
            user.setIsMultipleSession(expectedIsMultipleSession);

            // Then
            assertEquals(expectedIsMultipleSession, user.getIsMultipleSession());
        }

        @Test
        @DisplayName("Should set and get firstLoginStatus correctly")
        void shouldSetAndGetFirstLoginStatus() {
            // Given
            Boolean expectedFirstLoginStatus = false;

            // When
            user.setFirstLoginStatus(expectedFirstLoginStatus);

            // Then
            assertEquals(expectedFirstLoginStatus, user.getFirstLoginStatus());
        }

        @Test
        @DisplayName("Should set and get password correctly")
        void shouldSetAndGetPassword() {
            // Given
            String expectedPassword = "hashedPassword123";

            // When
            user.setPassword(expectedPassword);

            // Then
            assertEquals(expectedPassword, user.getPassword());
        }

        @Test
        @DisplayName("Should set and get passwordCreateAt correctly")
        void shouldSetAndGetPasswordCreateAt() {
            // Given
            String expectedPasswordCreateAt = "2024-01-01";

            // When
            user.setPasswordCreateAt(expectedPasswordCreateAt);

            // Then
            assertEquals(expectedPasswordCreateAt, user.getPasswordCreateAt());
        }

        @Test
        @DisplayName("Should set and get passwordUpdateAt correctly")
        void shouldSetAndGetPasswordUpdateAt() {
            // Given
            String expectedPasswordUpdateAt = "2024-01-02";

            // When
            user.setPasswordUpdateAt(expectedPasswordUpdateAt);

            // Then
            assertEquals(expectedPasswordUpdateAt, user.getPasswordUpdateAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have correct default values")
        void shouldHaveCorrectDefaultValues() {
            // Given & When
            User newUser = new User();

            // Then
            assertTrue(newUser.getIsActive());
            assertEquals(Byte.valueOf((byte) 0), newUser.getLoginAttemptCount());
            assertFalse(newUser.getIsMultipleSession());
            assertTrue(newUser.getFirstLoginStatus());
        }

        @Test
        @DisplayName("Should override default values when explicitly set")
        void shouldOverrideDefaultValues() {
            // Given & When
            user.setIsActive(false);
            user.setLoginAttemptCount((byte) 5);
            user.setIsMultipleSession(true);
            user.setFirstLoginStatus(false);

            // Then
            assertFalse(user.getIsActive());
            assertEquals(Byte.valueOf((byte) 5), user.getLoginAttemptCount());
            assertTrue(user.getIsMultipleSession());
            assertFalse(user.getFirstLoginStatus());
        }
    }

    @Nested
    @DisplayName("JPA Lifecycle Methods Tests")
    class LifecycleMethodsTests {

        @Test
        @DisplayName("Should set createdAt and updateAt on onCreate when they are null")
        void shouldSetTimestampsOnCreateWhenNull() {
            // Given
            user.setCreatedAt(null);
            user.setUpdateAt(null);
            LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);

            // When
            user.onCreate();

            // Then
            LocalDateTime afterCall = LocalDateTime.now().plusSeconds(1);
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdateAt());
            assertTrue(user.getCreatedAt().isAfter(beforeCall));
            assertTrue(user.getCreatedAt().isBefore(afterCall));
            assertTrue(user.getUpdateAt().isAfter(beforeCall));
            assertTrue(user.getUpdateAt().isBefore(afterCall));
        }

        @Test
        @DisplayName("Should not override existing createdAt on onCreate")
        void shouldNotOverrideExistingCreatedAtOnCreate() {
            // Given
            LocalDateTime existingCreatedAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            user.setCreatedAt(existingCreatedAt);
            user.setUpdateAt(null);

            // When
            user.onCreate();

            // Then
            assertEquals(existingCreatedAt, user.getCreatedAt());
            assertNotNull(user.getUpdateAt());
        }

        @Test
        @DisplayName("Should not override existing updateAt on onCreate")
        void shouldNotOverrideExistingUpdateAtOnCreate() {
            // Given
            LocalDateTime existingUpdateAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            user.setCreatedAt(null);
            user.setUpdateAt(existingUpdateAt);

            // When
            user.onCreate();

            // Then
            assertNotNull(user.getCreatedAt());
            assertEquals(existingUpdateAt, user.getUpdateAt());
        }

        @Test
        @DisplayName("Should update updateAt on onUpdate")
        void shouldUpdateTimestampOnUpdate() {
            // Given
            LocalDateTime oldUpdateAt = LocalDateTime.of(2023, 1, 1, 12, 0);
            user.setUpdateAt(oldUpdateAt);
            LocalDateTime beforeCall = LocalDateTime.now().minusSeconds(1);

            // When
            user.onUpdate();

            // Then
            LocalDateTime afterCall = LocalDateTime.now().plusSeconds(1);
            assertNotNull(user.getUpdateAt());
            assertTrue(user.getUpdateAt().isAfter(beforeCall));
            assertTrue(user.getUpdateAt().isBefore(afterCall));
            assertNotEquals(oldUpdateAt, user.getUpdateAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are same")
        void shouldBeEqualWhenAllFieldsSame() {
            // Given
            User user1 = createTestUser();
            User user2 = createTestUser();

            // When & Then
            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsDifferent() {
            // Given
            User user1 = createTestUser();
            User user2 = createTestUser();
            user2.setId(999L);

            // When & Then
            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            User user1 = createTestUser();

            // When & Then
            assertNotEquals(user1, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            User user1 = createTestUser();
            String differentObject = "different";

            // When & Then
            assertNotEquals(user1, differentObject);
        }

        private User createTestUser() {
            User testUser = new User();
            testUser.setId(1L);
            testUser.setUserId("USER001");
            testUser.setEmail("test@example.com");
            testUser.setFirstName("John");
            testUser.setLastName("Doe");
            testUser.setPassword("hashedPassword");
            return testUser;
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return non-null string representation")
        void shouldReturnNonNullStringRepresentation() {
            // Given
            user.setUserId("USER001");
            user.setEmail("test@example.com");

            // When
            String userString = user.toString();

            // Then
            assertNotNull(userString);
            assertTrue(userString.contains("User"));
        }

        @Test
        @DisplayName("Should contain key field values in string representation")
        void shouldContainKeyFieldsInStringRepresentation() {
            // Given
            user.setUserId("USER001");
            user.setEmail("test@example.com");
            user.setFirstName("John");

            // When
            String userString = user.toString();

            // Then
            assertTrue(userString.contains("USER001"));
            assertTrue(userString.contains("test@example.com"));
            assertTrue(userString.contains("John"));
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should handle login attempt increment")
        void shouldHandleLoginAttemptIncrement() {
            // Given
            user.setLoginAttemptCount((byte) 2);

            // When
            user.setLoginAttemptCount((byte) (user.getLoginAttemptCount() + 1));

            // Then
            assertEquals(Byte.valueOf((byte) 3), user.getLoginAttemptCount());
        }

        @Test
        @DisplayName("Should handle user blocking scenario")
        void shouldHandleUserBlockingScenario() {
            // Given
            LocalDateTime blockTime = LocalDateTime.now().plusHours(1);

            // When
            user.setBlockedUntil(blockTime);
            user.setIsActive(false);

            // Then
            assertEquals(blockTime, user.getBlockedUntil());
            assertFalse(user.getIsActive());
        }

        @Test
        @DisplayName("Should handle first login completion")
        void shouldHandleFirstLoginCompletion() {
            // Given
            user.setFirstLoginStatus(true);

            // When
            user.setFirstLoginStatus(false);

            // Then
            assertFalse(user.getFirstLoginStatus());
        }

        @Test
        @DisplayName("Should handle multiple session enablement")
        void shouldHandleMultipleSessionEnablement() {
            // Given
            user.setIsMultipleSession(false);

            // When
            user.setIsMultipleSession(true);

            // Then
            assertTrue(user.getIsMultipleSession());
        }
    }
}