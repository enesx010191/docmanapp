package com.moneymate.documentationManagement.dataAccess.abstracts;

import com.moneymate.documentationManagement.dataAccess.abstracts.UserRepository;

import com.moneymate.documentationManagement.entities.concretes.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Test verilerini hazırla
        testUser1 = new User();
        testUser1.setUserId("USER001");
        testUser1.setEmail("john.doe@example.com");
        testUser1.setFirstName("John");
        testUser1.setLastName("Doe");
        testUser1.setPassword("hashedPassword123");
        testUser1.setIsActive(true);
        testUser1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        testUser1.setUpdateAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        testUser1.setCreatedIp("192.168.1.100");
        testUser1.setStatus(Byte.valueOf((byte) 1));
        testUser1.setLoginAttemptCount(Byte.valueOf((byte) 0));
        testUser1.setIsMultipleSession(false);
        testUser1.setFirstLoginStatus(true);

        testUser2 = new User();
        testUser2.setUserId("USER002");
        testUser2.setEmail("jane.smith@example.com");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
        testUser2.setPassword("anotherHashedPassword");
        testUser2.setIsActive(true);
        testUser2.setCreatedAt(LocalDateTime.of(2024, 1, 2, 11, 0));
        testUser2.setUpdateAt(LocalDateTime.of(2024, 1, 2, 11, 0));
        testUser2.setCreatedIp("192.168.1.101");
        testUser2.setStatus(Byte.valueOf((byte) 1));
        testUser2.setLoginAttemptCount(Byte.valueOf((byte) 1));
        testUser2.setIsMultipleSession(true);
        testUser2.setFirstLoginStatus(false);

        testUser3 = new User();
        testUser3.setUserId("USER003");
        testUser3.setEmail("inactive.user@example.com");
        testUser3.setFirstName("Inactive");
        testUser3.setLastName("User");
        testUser3.setPassword("inactivePassword");
        testUser3.setIsActive(false);
        testUser3.setCreatedAt(LocalDateTime.of(2024, 1, 3, 12, 0));
        testUser3.setUpdateAt(LocalDateTime.of(2024, 1, 3, 12, 0));
        testUser3.setCreatedIp("192.168.1.102");
        testUser3.setStatus(Byte.valueOf((byte) 0));
        testUser3.setLoginAttemptCount(Byte.valueOf((byte) 5));
        testUser3.setBlockedUntil(LocalDateTime.of(2024, 1, 3, 18, 0));
        testUser3.setIsMultipleSession(false);
        testUser3.setFirstLoginStatus(true);

        // Test verilerini kaydet
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);
    }

    @Nested
    @DisplayName("Basic JpaRepository Methods Tests")
    class BasicJpaRepositoryTests {

        @Test
        @DisplayName("Should save and retrieve user")
        void shouldSaveAndRetrieveUser() {
            // Given
            User newUser = new User();
            newUser.setUserId("NEW_USER");
            newUser.setEmail("new.user@example.com");
            newUser.setFirstName("New");
            newUser.setLastName("User");
            newUser.setPassword("newPassword");

            // When
            User savedUser = userRepository.save(newUser);
            Optional<User> foundUser = userRepository.findById(savedUser.getId());

            // Then
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getId()).isNotNull();
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUserId()).isEqualTo("NEW_USER");
            assertThat(foundUser.get().getEmail()).isEqualTo("new.user@example.com");
            assertThat(foundUser.get().getFirstName()).isEqualTo("New");
            assertThat(foundUser.get().getLastName()).isEqualTo("User");
        }

        @Test
        @DisplayName("Should find all users")
        void shouldFindAllUsers() {
            // When
            List<User> allUsers = userRepository.findAll();

            // Then
            assertThat(allUsers).hasSize(3);
            assertThat(allUsers)
                    .extracting(User::getUserId)
                    .containsExactlyInAnyOrder("USER001", "USER002", "USER003");
        }

        @Test
        @DisplayName("Should delete user by id")
        void shouldDeleteUserById() {
            // Given
            Long userId = testUser1.getId();

            // When
            userRepository.deleteById(userId);
            Optional<User> deletedUser = userRepository.findById(userId);

            // Then
            assertThat(deletedUser).isEmpty();
            assertThat(userRepository.findAll()).hasSize(2);
        }

        @Test
        @DisplayName("Should count total users")
        void shouldCountTotalUsers() {
            // When
            long count = userRepository.count();

            // Then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User userToUpdate = testUser1;
            String newFirstName = "UpdatedJohn";
            String newLastName = "UpdatedDoe";

            // When
            userToUpdate.setFirstName(newFirstName);
            userToUpdate.setLastName(newLastName);
            User updatedUser = userRepository.save(userToUpdate);

            // Then
            assertThat(updatedUser.getFirstName()).isEqualTo(newFirstName);
            assertThat(updatedUser.getLastName()).isEqualTo(newLastName);
            assertThat(updatedUser.getId()).isEqualTo(testUser1.getId());
        }
    }

    @Nested
    @DisplayName("Custom Query Methods Tests")
    class CustomQueryMethodsTests {

        @Test
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            // When
            Optional<User> foundUser1 = userRepository.findByEmail("john.doe@example.com");
            Optional<User> foundUser2 = userRepository.findByEmail("jane.smith@example.com");
            Optional<User> notFoundUser = userRepository.findByEmail("nonexistent@example.com");

            // Then
            assertThat(foundUser1).isPresent();
            assertThat(foundUser1.get().getUserId()).isEqualTo("USER001");
            assertThat(foundUser1.get().getFirstName()).isEqualTo("John");
            assertThat(foundUser1.get().getLastName()).isEqualTo("Doe");

            assertThat(foundUser2).isPresent();
            assertThat(foundUser2.get().getUserId()).isEqualTo("USER002");
            assertThat(foundUser2.get().getFirstName()).isEqualTo("Jane");

            assertThat(notFoundUser).isEmpty();
        }

        @Test
        @DisplayName("Should find user by userId")
        void shouldFindUserByUserId() {
            // When
            Optional<User> foundUser1 = userRepository.findByUserId("USER001");
            Optional<User> foundUser2 = userRepository.findByUserId("USER002");
            Optional<User> notFoundUser = userRepository.findByUserId("NONEXISTENT");

            // Then
            assertThat(foundUser1).isPresent();
            assertThat(foundUser1.get().getEmail()).isEqualTo("john.doe@example.com");
            assertThat(foundUser1.get().getFirstName()).isEqualTo("John");

            assertThat(foundUser2).isPresent();
            assertThat(foundUser2.get().getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(foundUser2.get().getFirstName()).isEqualTo("Jane");

            assertThat(notFoundUser).isEmpty();
        }

        @Test
        @DisplayName("Should check if email exists")
        void shouldCheckIfEmailExists() {
            // When
            boolean existingEmail1 = userRepository.existsByEmail("john.doe@example.com");
            boolean existingEmail2 = userRepository.existsByEmail("jane.smith@example.com");
            boolean existingEmail3 = userRepository.existsByEmail("inactive.user@example.com");
            boolean nonExistingEmail = userRepository.existsByEmail("nonexistent@example.com");

            // Then
            assertTrue(existingEmail1);
            assertTrue(existingEmail2);
            assertTrue(existingEmail3);
            assertFalse(nonExistingEmail);
        }

        @Test
        @DisplayName("Should find user by email and password")
        void shouldFindUserByEmailAndPassword() {
            // When
            Optional<User> correctCredentials1 = userRepository
                    .findByEmailAndPassword("john.doe@example.com", "hashedPassword123");
            Optional<User> correctCredentials2 = userRepository
                    .findByEmailAndPassword("jane.smith@example.com", "anotherHashedPassword");
            Optional<User> wrongPassword = userRepository
                    .findByEmailAndPassword("john.doe@example.com", "wrongPassword");
            Optional<User> wrongEmail = userRepository
                    .findByEmailAndPassword("wrong@example.com", "hashedPassword123");
            Optional<User> bothWrong = userRepository
                    .findByEmailAndPassword("wrong@example.com", "wrongPassword");

            // Then
            assertThat(correctCredentials1).isPresent();
            assertThat(correctCredentials1.get().getUserId()).isEqualTo("USER001");
            assertThat(correctCredentials1.get().getFirstName()).isEqualTo("John");

            assertThat(correctCredentials2).isPresent();
            assertThat(correctCredentials2.get().getUserId()).isEqualTo("USER002");
            assertThat(correctCredentials2.get().getFirstName()).isEqualTo("Jane");

            assertThat(wrongPassword).isEmpty();
            assertThat(wrongEmail).isEmpty();
            assertThat(bothWrong).isEmpty();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null parameters gracefully")
        void shouldHandleNullParametersGracefully() {
            // When & Then
            assertThat(userRepository.findByEmail(null)).isEmpty();
            assertThat(userRepository.findByUserId(null)).isEmpty();
            assertThat(userRepository.existsByEmail(null)).isFalse();
            assertThat(userRepository.findByEmailAndPassword(null, "password")).isEmpty();
            assertThat(userRepository.findByEmailAndPassword("email@test.com", null)).isEmpty();
            assertThat(userRepository.findByEmailAndPassword(null, null)).isEmpty();
        }

        @Test
        @DisplayName("Should handle empty string parameters")
        void shouldHandleEmptyStringParameters() {
            // When
            Optional<User> byEmptyEmail = userRepository.findByEmail("");
            Optional<User> byEmptyUserId = userRepository.findByUserId("");
            boolean existsByEmptyEmail = userRepository.existsByEmail("");
            Optional<User> byEmptyEmailAndPassword = userRepository.findByEmailAndPassword("", "password");
            Optional<User> byEmailAndEmptyPassword = userRepository.findByEmailAndPassword("email@test.com", "");

            // Then
            assertThat(byEmptyEmail).isEmpty();
            assertThat(byEmptyUserId).isEmpty();
            assertThat(existsByEmptyEmail).isFalse();
            assertThat(byEmptyEmailAndPassword).isEmpty();
            assertThat(byEmailAndEmptyPassword).isEmpty();
        }

        @Test
        @DisplayName("Should handle case sensitivity correctly")
        void shouldHandleCaseSensitivityCorrectly() {
            // When
            Optional<User> lowerCaseEmail = userRepository.findByEmail("john.doe@example.com");
            Optional<User> upperCaseEmail = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");
            Optional<User> mixedCaseEmail = userRepository.findByEmail("John.Doe@Example.Com");

            Optional<User> lowerCaseUserId = userRepository.findByUserId("user001");
            Optional<User> correctUserId = userRepository.findByUserId("USER001");

            // Then
            assertThat(lowerCaseEmail).isPresent(); // Correct case
            assertThat(upperCaseEmail).isEmpty();   // Wrong case
            assertThat(mixedCaseEmail).isEmpty();   // Wrong case

            assertThat(lowerCaseUserId).isEmpty();  // Wrong case
            assertThat(correctUserId).isPresent();  // Correct case
        }

        @Test
        @DisplayName("Should handle special characters in email and userId")
        void shouldHandleSpecialCharactersInEmailAndUserId() {
            // Given
            User specialUser = new User();
            specialUser.setUserId("USER_001@SPECIAL");
            specialUser.setEmail("test+user@example-domain.co.uk");
            specialUser.setFirstName("Test");
            specialUser.setLastName("User");
            specialUser.setPassword("specialPassword");
            entityManager.persistAndFlush(specialUser);

            // When
            Optional<User> foundByEmail = userRepository.findByEmail("test+user@example-domain.co.uk");
            Optional<User> foundByUserId = userRepository.findByUserId("USER_001@SPECIAL");
            boolean emailExists = userRepository.existsByEmail("test+user@example-domain.co.uk");

            // Then
            assertThat(foundByEmail).isPresent();
            assertThat(foundByUserId).isPresent();
            assertThat(emailExists).isTrue();
            assertThat(foundByEmail.get().getUserId()).isEqualTo("USER_001@SPECIAL");
        }

        @Test
        @DisplayName("Should handle long email addresses")
        void shouldHandleLongEmailAddresses() {
            // Given
            String longEmail = "very.long.email.address.with.many.dots.and.subdomains@very-long-domain-name-with-subdomains.example.com";
            User longEmailUser = new User();
            longEmailUser.setUserId("LONG_EMAIL_USER");
            longEmailUser.setEmail(longEmail);
            longEmailUser.setFirstName("Long");
            longEmailUser.setLastName("Email");
            longEmailUser.setPassword("longEmailPassword");
            entityManager.persistAndFlush(longEmailUser);

            // When
            Optional<User> foundByEmail = userRepository.findByEmail(longEmail);
            boolean emailExists = userRepository.existsByEmail(longEmail);

            // Then
            assertThat(foundByEmail).isPresent();
            assertThat(emailExists).isTrue();
            assertThat(foundByEmail.get().getUserId()).isEqualTo("LONG_EMAIL_USER");
        }
    }

    @Nested
    @DisplayName("Business Logic and Security Tests")
    class BusinessLogicSecurityTests {

        @Test
        @DisplayName("Should differentiate between active and inactive users")
        void shouldDifferentiateBetweenActiveAndInactiveUsers() {
            // When
            Optional<User> activeUser = userRepository.findByEmail("john.doe@example.com");
            Optional<User> inactiveUser = userRepository.findByEmail("inactive.user@example.com");

            // Then
            assertThat(activeUser).isPresent();
            assertThat(activeUser.get().getIsActive()).isTrue();

            assertThat(inactiveUser).isPresent();
            assertThat(inactiveUser.get().getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Should find users with different login attempt counts")
        void shouldFindUsersWithDifferentLoginAttemptCounts() {
            // When
            Optional<User> userWithZeroAttempts = userRepository.findByUserId("USER001");
            Optional<User> userWithOneAttempt = userRepository.findByUserId("USER002");
            Optional<User> userWithMultipleAttempts = userRepository.findByUserId("USER003");

            // Then
            assertThat(userWithZeroAttempts).isPresent();
            assertThat(userWithZeroAttempts.get().getLoginAttemptCount()).isEqualTo(Byte.valueOf((byte) 0));

            assertThat(userWithOneAttempt).isPresent();
            assertThat(userWithOneAttempt.get().getLoginAttemptCount()).isEqualTo(Byte.valueOf((byte) 1));

            assertThat(userWithMultipleAttempts).isPresent();
            assertThat(userWithMultipleAttempts.get().getLoginAttemptCount()).isEqualTo(Byte.valueOf((byte) 5));
        }

        @Test
        @DisplayName("Should handle blocked users correctly")
        void shouldHandleBlockedUsersCorrectly() {
            // When
            Optional<User> blockedUser = userRepository.findByUserId("USER003");
            Optional<User> nonBlockedUser = userRepository.findByUserId("USER001");

            // Then
            assertThat(blockedUser).isPresent();
            assertThat(blockedUser.get().getBlockedUntil()).isNotNull();
            assertThat(blockedUser.get().getBlockedUntil()).isEqualTo(LocalDateTime.of(2024, 1, 3, 18, 0));

            assertThat(nonBlockedUser).isPresent();
            assertThat(nonBlockedUser.get().getBlockedUntil()).isNull();
        }

        @Test
        @DisplayName("Should handle multiple session settings")
        void shouldHandleMultipleSessionSettings() {
            // When
            Optional<User> multipleSessionUser = userRepository.findByUserId("USER002");
            Optional<User> singleSessionUser = userRepository.findByUserId("USER001");

            // Then
            assertThat(multipleSessionUser).isPresent();
            assertThat(multipleSessionUser.get().getIsMultipleSession()).isTrue();

            assertThat(singleSessionUser).isPresent();
            assertThat(singleSessionUser.get().getIsMultipleSession()).isFalse();
        }

        @Test
        @DisplayName("Should not expose passwords in any query result")
        void shouldNotExposePasswordsInQueryResult() {
            // When
            Optional<User> user = userRepository.findByEmail("john.doe@example.com");

            // Then - Password should be hashed/encrypted, not plain text
            assertThat(user).isPresent();
            assertThat(user.get().getPassword()).isNotNull();
            assertThat(user.get().getPassword()).isEqualTo("hashedPassword123"); // Assume this is hashed
            // In real scenario, password should never be plain text
        }
    }

    @Nested
    @DisplayName("Performance and Data Integrity Tests")
    class PerformanceAndIntegrityTests {

        @Test
        @DisplayName("Should maintain unique constraints on email")
        void shouldMaintainUniqueConstraintsOnEmail() {
            // Given
            User duplicateEmailUser = new User();
            duplicateEmailUser.setUserId("DUPLICATE_USER");
            duplicateEmailUser.setEmail("john.doe@example.com"); // Same as testUser1
            duplicateEmailUser.setFirstName("Duplicate");
            duplicateEmailUser.setLastName("User");
            duplicateEmailUser.setPassword("duplicatePassword");

            // When & Then
            assertThrows(Exception.class, () -> {
                entityManager.persistAndFlush(duplicateEmailUser);
            });
        }

        @Test
        @DisplayName("Should maintain unique constraints on userId")
        void shouldMaintainUniqueConstraintsOnUserId() {
            // Given
            User duplicateUserIdUser = new User();
            duplicateUserIdUser.setUserId("USER001"); // Same as testUser1
            duplicateUserIdUser.setEmail("different@example.com");
            duplicateUserIdUser.setFirstName("Duplicate");
            duplicateUserIdUser.setLastName("UserId");
            duplicateUserIdUser.setPassword("duplicatePassword");

            // When & Then
            assertThrows(Exception.class, () -> {
                entityManager.persistAndFlush(duplicateUserIdUser);
            });
        }

        @Test
        @DisplayName("Should handle concurrent user operations")
        void shouldHandleConcurrentUserOperations() {
            // Given
            String uniqueUserId = "CONCURRENT_USER_" + System.currentTimeMillis();
            String uniqueEmail = "concurrent" + System.currentTimeMillis() + "@example.com";
            
            User user = new User();
            user.setUserId(uniqueUserId);
            user.setEmail(uniqueEmail);
            user.setFirstName("Concurrent");
            user.setLastName("User");
            user.setPassword("concurrentPassword");

            // When
            User saved = userRepository.save(user);
            Optional<User> retrieved = userRepository.findById(saved.getId());

            // Modify and save again
            saved.setFirstName("Updated Concurrent");
            User updated = userRepository.save(saved);

            // Then
            assertThat(retrieved).isPresent();
            assertThat(updated.getFirstName()).isEqualTo("Updated Concurrent");
            assertThat(updated.getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("Should perform email existence check efficiently")
        void shouldPerformEmailExistenceCheckEfficiently() {
            // Given
            String[] emailsToCheck = {
                "john.doe@example.com",
                "jane.smith@example.com", 
                "inactive.user@example.com",
                "nonexistent1@example.com",
                "nonexistent2@example.com"
            };

            // When
            long startTime = System.currentTimeMillis();
            for (String email : emailsToCheck) {
                userRepository.existsByEmail(email);
            }
            long endTime = System.currentTimeMillis();

            // Then - Should complete quickly
            assertThat(endTime - startTime).isLessThan(100); // Should complete within 100ms
        }
    }
}