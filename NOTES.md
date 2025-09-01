# Full Stack Web Uygulaması

Bu proje React (TypeScript) frontend ve Java Spring Boot backend ile geliştirilmiş basit bir kullanıcı yönetim uygulamasıdır.

## Proje Yapısı

```
project-root/
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── UserForm.tsx
│   │   │   ├── UserList.tsx
│   │   │   └── Layout.tsx
│   │   ├── services/
│   │   │   └── userService.ts
│   │   ├── types/
│   │   │   └── User.ts
│   │   ├── App.tsx
│   │   └── index.tsx
│   ├── package.json
│   ├── tsconfig.json
│   └── Dockerfile
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── example/
│   │       │           └── userapi/
│   │       │               ├── UserApiApplication.java
│   │       │               ├── config/
│   │       │               │   └── CorsConfig.java
│   │       │               ├── controller/
│   │       │               │   └── UserController.java
│   │       │               ├── dto/
│   │       │               │   ├── UserRequestDto.java
│   │       │               │   └── UserResponseDto.java
│   │       │               ├── entity/
│   │       │               │   └── User.java
│   │       │               ├── repository/
│   │       │               │   └── UserRepository.java
│   │       │               ├── service/
│   │       │               │   ├── UserService.java
│   │       │               │   └── UserServiceImpl.java
│   │       │               └── exception/
│   │       │                   ├── UserNotFoundException.java
│   │       │                   └── GlobalExceptionHandler.java
│   │       └── resources/
│   │           ├── application.properties
│   │           └── logback-spring.xml
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Backend (Java Spring Boot)

### 1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Spring Boot parent dependency - versiyon yönetimi ve default ayarlar için -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>user-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>user-api</name>
    <description>User Management API with Spring Boot</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web Starter - REST API geliştirmek için -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Spring Boot Data JPA - ORM ve veritabanı işlemleri için -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- Spring Boot Validation - input validasyonu için -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- PostgreSQL Driver - veritabanı bağlantısı için -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Spring Boot Test Starter - unit ve integration testler için -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin - executable JAR oluşturmak için -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. UserApiApplication.java (Main Class)

```java
package com.example.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot uygulamasının ana başlatıcı sınıfı
 * @SpringBootApplication annotation'ı şunları içerir:
 * - @Configuration: Bean tanımları için
 * - @EnableAutoConfiguration: Spring Boot'un otomatik yapılandırması için
 * - @ComponentScan: Component taraması için
 */
@SpringBootApplication
public class UserApiApplication {

    /**
     * Uygulamayı başlatan ana metod
     * @param args komut satırı argümanları
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }
}
```

### 3. User.java (Entity)

```java
package com.example.userapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * User entity sınıfı - veritabanı tablosunu temsil eder
 * JPA annotations ile veritabanı mapping'i yapılır
 */
@Entity
@Table(name = "users") // PostgreSQL'de 'user' reserved keyword olduğu için 'users' kullanıyoruz
public class User {

    /**
     * Primary key - otomatik artan ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Kullanıcı adı - boş olamaz, 2-50 karakter arası
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Ad boş olamaz")
    @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır")
    private String firstName;

    /**
     * Kullanıcı soyadı - boş olamaz, 2-50 karakter arası
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Soyad boş olamaz")
    @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır")
    private String lastName;

    /**
     * E-posta adresi - boş olamaz, unique olmalı, email formatında
     */
    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    /**
     * Kayıt oluşturma zamanı - otomatik set edilir
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Kayıt güncelleme zamanı - her update'te otomatik güncellenir
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor - JPA için gerekli
     */
    public User() {}

    /**
     * Parametreli constructor
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Entity persist edilmeden önce çağrılır - oluşturma zamanını set eder
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Entity update edilmeden önce çağrılır - güncelleme zamanını set eder
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getter ve Setter metodları
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

### 4. UserRepository.java (Repository Interface)

```java
package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User entity için repository interface
 * JpaRepository'den extend ederek CRUD işlemleri otomatik gelir
 * Spring Data JPA bu interface'in implementasyonunu otomatik oluşturur
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * E-posta adresine göre kullanıcı arama
     * Method isimlendirme convention'ına göre otomatik query oluşur
     * @param email aranacak e-posta adresi
     * @return Optional<User> - kullanıcı bulunursa dolu, bulunamazsa boş
     */
    Optional<User> findByEmail(String email);

    /**
     * E-posta adresinin var olup olmadığını kontrol etme
     * @param email kontrol edilecek e-posta adresi
     * @return boolean - varsa true, yoksa false
     */
    boolean existsByEmail(String email);

    /**
     * Ad veya soyad ile arama yapma (case insensitive)
     * Custom JPQL query kullanımı örneği
     * @param firstName aranan ad
     * @param lastName aranan soyad
     * @return List<User> - bulunan kullanıcılar
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);
}
```

### 5. UserRequestDto.java (Request DTO)

```java
package com.example.userapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Kullanıcı oluşturma ve güncelleme için request DTO sınıfı
 * Dış dünyadan gelen veriler için kullanılır
 * Validation annotation'ları ile input kontrolleri yapılır
 */
public class UserRequestDto {

    /**
     * Kullanıcı adı - validation kuralları entity ile aynı
     */
    @NotBlank(message = "Ad boş olamaz")
    @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır")
    private String firstName;

    /**
     * Kullanıcı soyadı - validation kuralları entity ile aynı
     */
    @NotBlank(message = "Soyad boş olamaz")
    @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır")
    private String lastName;

    /**
     * E-posta adresi - validation kuralları entity ile aynı
     */
    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    // Default constructor
    public UserRequestDto() {}

    // Parametreli constructor
    public UserRequestDto(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getter ve Setter metodları
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### 6. UserResponseDto.java (Response DTO)

```java
package com.example.userapi.dto;

import com.example.userapi.entity.User;
import java.time.LocalDateTime;

/**
 * Kullanıcı bilgilerini client'a döndürmek için response DTO sınıfı
 * Entity'den farklı olarak sadece gerekli bilgileri içerir
 * Sensitive bilgiler (örn: password) burada yer almaz
 */
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public UserResponseDto() {}

    // Parametreli constructor
    public UserResponseDto(Long id, String firstName, String lastName, String email, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * User entity'sinden UserResponseDto oluşturan static factory method
     * Mapping işlemini kolaylaştırır ve kod tekrarını önler
     * @param user entity
     * @return UserResponseDto
     */
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    // Getter ve Setter metodları
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

### 7. UserService.java (Service Interface)

```java
package com.example.userapi.service;

import com.example.userapi.dto.UserRequestDto;
import com.example.userapi.dto.UserResponseDto;

import java.util.List;

/**
 * UserService interface'i
 * Business logic metodlarını tanımlar
 * Dependency Inversion Principle gereği interface üzerinden çalışıyoruz
 */
public interface UserService {

    /**
     * Tüm kullanıcıları getir
     * @return List<UserResponseDto> - tüm kullanıcılar
     */
    List<UserResponseDto> getAllUsers();

    /**
     * ID'ye göre kullanıcı getir
     * @param id kullanıcı ID'si
     * @return UserResponseDto - bulunan kullanıcı
     */
    UserResponseDto getUserById(Long id);

    /**
     * E-posta adresine göre kullanıcı getir
     * @param email e-posta adresi
     * @return UserResponseDto - bulunan kullanıcı
     */
    UserResponseDto getUserByEmail(String email);

    /**
     * Yeni kullanıcı oluştur
     * @param userRequestDto kullanıcı bilgileri
     * @return UserResponseDto - oluşturulan kullanıcı
     */
    UserResponseDto createUser(UserRequestDto userRequestDto);

    /**
     * Kullanıcı güncelle
     * @param id kullanıcı ID'si
     * @param userRequestDto yeni kullanıcı bilgileri
     * @return UserResponseDto - güncellenmiş kullanıcı
     */
    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    /**
     * Kullanıcı sil
     * @param id silinecek kullanıcı ID'si
     */
    void deleteUser(Long id);

    /**
     * Ad veya soyad ile kullanıcı arama
     * @param searchTerm arama terimi
     * @return List<UserResponseDto> - bulunan kullanıcılar
     */
    List<UserResponseDto> searchUsers(String searchTerm);
}
```

### 8. UserServiceImpl.java (Service Implementation)

```java
package com.example.userapi.service;

import com.example.userapi.dto.UserRequestDto;
import com.example.userapi.dto.UserResponseDto;
import com.example.userapi.entity.User;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService interface'inin implementasyonu
 * Business logic'in yer aldığı sınıf
 * @Service annotation'ı ile Spring IoC container'a bean olarak kaydedilir
 */
@Service
@Transactional // Tüm metodlar için transaction yönetimi
public class UserServiceImpl implements UserService {

    // SLF4J logger - loglama için
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // UserRepository dependency injection ile enjekte edilir
    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection
     * @Autowired optional çünkü tek constructor var
     * @param userRepository user repository
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Tüm kullanıcıları getir
     * @return List<UserResponseDto> - tüm kullanıcılar
     */
    @Override
    @Transactional(readOnly = true) // Read-only transaction - performans optimizasyonu
    public List<UserResponseDto> getAllUsers() {
        logger.info("Tüm kullanıcılar getiriliyor");
        
        List<User> users = userRepository.findAll();
        logger.info("Toplam {} kullanıcı bulundu", users.size());
        
        // Stream API ile entity'leri DTO'ya dönüştür
        return users.stream()
                   .map(UserResponseDto::fromEntity)
                   .collect(Collectors.toList());
    }

    /**
     * ID'ye göre kullanıcı getir
     * @param id kullanıcı ID'si
     * @return UserResponseDto - bulunan kullanıcı
     * @throws UserNotFoundException kullanıcı bulunamadığında
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        logger.info("ID: {} olan kullanıcı getiriliyor", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("ID: {} olan kullanıcı bulunamadı", id);
                return new UserNotFoundException("ID: " + id + " olan kullanıcı bulunamadı");
            });
        
        logger.info("Kullanıcı bulundu: {}", user.getEmail());
        return UserResponseDto.fromEntity(user);
    }

    /**
     * E-posta adresine göre kullanıcı getir
     * @param email e-posta adresi
     * @return UserResponseDto - bulunan kullanıcı
     * @throws UserNotFoundException kullanıcı bulunamadığında
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        logger.info("E-posta: {} olan kullanıcı getiriliyor", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.error("E-posta: {} olan kullanıcı bulunamadı", email);
                return new UserNotFoundException("E-posta: " + email + " olan kullanıcı bulunamadı");
            });
        
        logger.info("Kullanıcı bulundu: {} {}", user.getFirstName(), user.getLastName());
        return UserResponseDto.fromEntity(user);
    }

    /**
     * Yeni kullanıcı oluştur
     * @param userRequestDto kullanıcı bilgileri
     * @return UserResponseDto - oluşturulan kullanıcı
     * @throws DataIntegrityViolationException e-posta zaten mevcutsa
     */
    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        logger.info("Yeni kullanıcı oluşturuluyor: {}", userRequestDto.getEmail());
        
        // E-posta kontrolü
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            logger.error("E-posta adresi zaten mevcut: {}", userRequestDto.getEmail());
            throw new DataIntegrityViolationException("Bu e-posta adresi zaten kayıtlı");
        }
        
        // DTO'dan Entity'ye dönüşüm
        User user = new User(
            userRequestDto.getFirstName(),
            userRequestDto.getLastName(),
            userRequestDto.getEmail()
        );
        
        // Veritabanına kaydet
        User savedUser = userRepository.save(user);
        logger.info("Kullanıcı başarıyla oluşturuldu. ID: {}", savedUser.getId());
        
        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * Kullanıcı güncelle
     * @param id kullanıcı ID'si
     * @param userRequestDto yeni kullanıcı bilgileri
     * @return UserResponseDto - güncellenmiş kullanıcı
     * @throws UserNotFoundException kullanıcı bulunamadığında
     */
    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        logger.info("ID: {} olan kullanıcı güncelleniyor", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Güncellenecek kullanıcı bulunamadı. ID: {}", id);
                return new UserNotFoundException("ID: " + id + " olan kullanıcı bulunamadı");
            });
        
        // E-posta değişiyorsa ve yeni e-posta zaten varsa hata ver
        if (!user.getEmail().equals(userRequestDto.getEmail()) && 
            userRepository.existsByEmail(userRequestDto.getEmail())) {
            logger.error("Güncellenmek istenen e-posta adresi zaten mevcut: {}", 
                        userRequestDto.getEmail());
            throw new DataIntegrityViolationException("Bu e-posta adresi zaten kayıtlı");
        }
        
        // Entity güncelle
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        
        User updatedUser = userRepository.save(user);
        logger.info("Kullanıcı başarıyla güncellendi. ID: {}", updatedUser.getId());
        
        return UserResponseDto.fromEntity(updatedUser);
    }

    /**
     * Kullanıcı sil
     * @param id silinecek kullanıcı ID'si
     * @throws UserNotFoundException kullanıcı bulunamadığında
     */
    @Override
    public void deleteUser(Long id) {
        logger.info("ID: {} olan kullanıcı siliniyor", id);
        
        if (!userRepository.existsById(id)) {
            logger.error("Silinecek kullanıcı bulunamadı. ID: {}", id);
            throw new UserNotFoundException("ID: " + id + " olan kullanıcı bulunamadı");
        }
        
        userRepository.deleteById(id);
        logger.info("Kullanıcı başarıyla silindi. ID: {}", id);
    }

    /**
     * Ad veya soyad ile kullanıcı arama
     * @param searchTerm arama terimi
     * @return List<UserResponseDto> - bulunan kullanıcılar
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(String searchTerm) {
        logger.info("Kullanıcı aranıyor: {}", searchTerm);
        
        List<User> users = userRepository
            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, searchTerm);
        
        logger.info("Arama sonucu {} kullanıcı bulundu", users.size());
        
        return users.stream()
                   .map(UserResponseDto::fromEntity)
                   .collect(Collectors.toList());
    }
}
```

### 9. UserController.java (REST Controller)

```java
package com.example.userapi.controller;

import com.example.userapi.dto.UserRequestDto;
import com.example.userapi.dto.UserResponseDto;
import com.example.userapi.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User REST Controller
 * HTTP isteklerini karşılar ve uygun service metodlarını çağırır
 * @RestController = @Controller + @ResponseBody
 * @RequestMapping ile base path tanımlanır
 * @CrossOrigin ile CORS ayarları yapılır (frontend bağlantısı için)
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // React app URL'i
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // UserService dependency injection
    private final UserService userService;

    /**
     * Constructor-based dependency injection
     * @param userService user service
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Tüm kullanıcıları getir
     * GET /api/users
     * @return List<UserResponseDto> - tüm kullanıcılar
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        logger.info("GET /api/users - Tüm kullanıcılar istendi");
        
        List<UserResponseDto> users = userService.getAllUsers();
        
        logger.info("Toplam {} kullanıcı döndürülüyor", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * ID'ye göre kullanıcı getir
     * GET /api/users/{id}
     * @param id kullanıcı ID'si
     * @return UserResponseDto - bulunan kullanıcı
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Kullanıcı istendi", id);
        
        UserResponseDto user = userService.getUserById(id);
        
        logger.info("Kullanıcı bulundu ve döndürülüyor: {}", user.getEmail());
        return ResponseEntity.ok(user);
    }

    /**
     * E-posta adresine göre kullanıcı getir
     * GET /api/users/email/{email}
     * @param email e-posta adresi
     * @return UserResponseDto - bulunan kullanıcı
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - E-posta ile kullanıcı istendi", email);
        
        UserResponseDto user = userService.getUserByEmail(email);
        
        logger.info("Kullanıcı bulundu ve döndürülüyor: {} {}", 
                   user.getFirstName(), user.getLastName());
        return ResponseEntity.ok(user);
    }

    /**
     * Yeni kullanıcı oluştur
     * POST /api/users
     * @param userRequestDto kullanıcı bilgileri (request body'den gelir)
     * @return UserResponseDto - oluşturulan kullanıcı
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequestDto) {
        logger.info("POST /api/users - Yeni kullanıcı oluşturma isteği: {}", 
                   userRequestDto.getEmail());
        
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        
        logger.info("Kullanıcı başarıyla oluşturuldu. ID: {}", createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Kullanıcı güncelle
     * PUT /api/users/{id}
     * @param id kullanıcı ID'si
     * @param userRequestDto yeni kullanıcı bilgileri
     * @return UserResponseDto - güncellenmiş kullanıcı
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserRequestDto userRequestDto) {
        logger.info("PUT /api/users/{} - Kullanıcı güncelleme isteği", id);
        
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);
        
        logger.info("Kullanıcı başarıyla güncellendi. ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Kullanıcı sil
     * DELETE /api/users/{id}
     * @param id silinecek kullanıcı ID'si
     * @return ResponseEntity<Void> - boş response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Kullanıcı silme isteği", id);
        
        userService.deleteUser(id);
        
        logger.info("Kullanıcı başarıyla silindi. ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Kullanıcı arama
     * GET /api/users/search?q={searchTerm}
     * @param searchTerm arama terimi
     * @return List<UserResponseDto> - bulunan kullanıcılar
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(
            @RequestParam("q") String searchTerm) {
        logger.info("GET /api/users/search?q={} - Kullanıcı arama isteği", searchTerm);
        
        List<UserResponseDto> users = userService.searchUsers(searchTerm);
        
        logger.info("Arama sonucu {} kullanıcı bulundu", users.size());
        return ResponseEntity.ok(users);
    }
}
```

### 10. UserNotFoundException.java (Custom Exception)

```java
package com.example.userapi.exception;

/**
 * Kullanıcı bulunamadığında fırlatılan özel exception sınıfı
 * RuntimeException'dan extends eder (unchecked exception)
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Mesaj ile constructor
     * @param message hata mesajı
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Mesaj ve sebep ile constructor
     * @param message hata mesajı
     * @param cause hata sebebi
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 11. GlobalExceptionHandler.java (Exception Handler)

```java
package com.example.userapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler
 * Tüm controller'larda oluşan exception'ları yakalar ve uygun HTTP response döner
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * UserNotFoundException handler
     * 404 Not Found döner
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("UserNotFoundException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Kullanıcı Bulunamadı",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * DataIntegrityViolationException handler
     * 409 Conflict döner (duplicate email gibi durumlarda)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {
        logger.error("DataIntegrityViolationException: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Veri Bütünlüğü Hatası",
            "Bu e-posta adresi zaten kayıtlı",
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Validation exception handler
     * 400 Bad Request döner
     * @Valid annotation'ı ile validation hatalarını yakalar
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        logger.error("Validation hatası: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Hatası",
            "Gönderilen veriler geçerli değil",
            LocalDateTime.now(),
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Genel exception handler
     * 500 Internal Server Error döner
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Beklenmeyen hata: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Sunucu Hatası",
            "Beklenmeyen bir hata oluştu",
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Hata response için inner class
     */
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getter metodları
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    /**
     * Validation hataları için özel response class
     */
    public static class ValidationErrorResponse extends ErrorResponse {
        private Map<String, String> fieldErrors;

        public ValidationErrorResponse(int status, String error, String message, 
                                     LocalDateTime timestamp, Map<String, String> fieldErrors) {
            super(status, error, message, timestamp);
            this.fieldErrors = fieldErrors;
        }

        public Map<String, String> getFieldErrors() { return fieldErrors; }
    }
}
```

### 12. CorsConfig.java (CORS Configuration)

```java
package com.example.userapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS (Cross-Origin Resource Sharing) yapılandırması
 * Frontend uygulamasının backend'e erişebilmesi için gerekli
 * @Configuration ile Spring IoC container'a configuration class olarak tanıtılır
 */
@Configuration
public class CorsConfig {

    /**
     * CORS yapılandırması bean'i
     * Frontend URL'lerini ve izin verilen HTTP metodlarını tanımlar
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // İzin verilen origin'ler (frontend URL'leri)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React development server
            "http://localhost:3001"   // Alternatif port
        ));
        
        // İzin verilen HTTP metodları
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // İzin verilen header'lar
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Credentials (cookies, authorization headers) izni
        configuration.setAllowCredentials(true);
        
        // Preflight request cache süresi (saniye)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}
```

### 13. application.properties (Application Configuration)

```properties
# Uygulama adı
spring.application.name=user-api

# Server port
server.port=8080

# PostgreSQL veritabanı bağlantı ayarları
spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP connection pool ayarları (performans optimizasyonu)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA/Hibernate ayarları
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# Jackson JSON ayarları
spring.jackson.default-property-inclusion=NON_NULL
spring.jackson.serialization.write-dates-as-timestamps=false

# Validation ayarları
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

# Actuator ayarları (health check için)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized

# Logging seviyeleri
logging.level.com.example.userapi=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 14. logback-spring.xml (Logging Configuration)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- Logback yapılandırma dosyası -->
<configuration>
    
    <!-- Console appender - konsola log yazdırır -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!-- Log format: timestamp [thread] LEVEL logger - message -->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File appender - dosyaya log yazdırır -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/user-api.log</file>
        
        <!-- Rolling policy - log dosyası boyut ve tarih bazlı döner -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/user-api.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Error file appender - sadece error logları için -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>60</maxHistory>
        </rollingPolicy>
        
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Uygulama logları için özel logger -->
    <logger name="com.example.userapi" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </logger>
    
    <!-- Hibernate SQL logları -->
    <logger name="org.hibernate.SQL" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <!-- Root logger - tüm diğer loglar için -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
</configuration>
```

### 15. Backend Dockerfile

```dockerfile
# Multi-stage build
# İlk stage: Maven ile build
FROM maven:3.8.6-openjdk-17-slim AS build

# Çalışma dizini
WORKDIR /app

# pom.xml'i kopyala ve dependency'leri indir (cache optimizasyonu)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Kaynak kodları kopyala
COPY src ./src

# Uygulamayı build et
RUN mvn clean package -DskipTests

# İkinci stage: Runtime
FROM openjdk:17-jre-slim

# Sistem güncellemeleri ve gerekli paketler
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Uygulama için kullanıcı oluştur (security best practice)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Çalışma dizini
WORKDIR /app

# JAR dosyasını build stage'den kopyala
COPY --from=build /app/target/*.jar app.jar

# Log dizini oluştur
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Kullanıcı değiştir
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Port expose
EXPOSE 8080

# JVM parametreleri ile uygulamayı başlat
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Xmx512m", \
    "-Xms256m", \
    "-XX:+UseG1GC", \
    "-XX:+UseContainerSupport", \
    "-jar", \
    "app.jar"]
```

---

## Frontend (React TypeScript)

### 16. package.json

```json
{
  "name": "user-management-frontend",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "@types/node": "^16.18.0",
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "axios": "^1.6.0",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-scripts": "5.0.1",
    "typescript": "^4.9.5",
    "web-vitals": "^2.1.4"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": [
      "react-app",
      "react-app/jest"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  },
  "devDependencies": {
    "@types/jest": "^27.5.2"
  }
}
```

### 17. tsconfig.json

```json
{
  "compilerOptions": {
    "target": "es5",
    "lib": [
      "dom",
      "dom.iterable",
      "es6"
    ],
    "allowJs": true,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "forceConsistentCasingInFileNames": true,
    "noFallthroughCasesInSwitch": true,
    "module": "esnext",
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx"
  },
  "include": [
    "src"
  ]
}
```

### 18. src/types/User.ts

```typescript
/**
 * Kullanıcı tipini tanımlayan interface
 * Backend'deki UserResponseDto ile uyumlu
 */
export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Kullanıcı oluşturma/güncelleme için request tipi
 * Backend'deki UserRequestDto ile uyumlu
 */
export interface UserRequest {
  firstName: string;
  lastName: string;
  email: string;
}

/**
 * API hata response tipi
 */
export interface ApiError {
  status: number;
  error: string;
  message: string;
  timestamp: string;
  fieldErrors?: Record<string, string>;
}
```

### 19. src/services/userService.ts

```typescript
import axios, { AxiosResponse } from 'axios';
import { User, UserRequest, ApiError } from '../types/User';

/**
 * Axios instance - base URL ve common ayarlar
 */
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request interceptor - tüm request'lere log ekleme
 */
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - hata yönetimi
 */
api.interceptors.response.use(
  (response) => {
    console.log(`API Response: ${response.status} ${response.config.url}`);
    return response;
  },
  (error) => {
    console.error('API Response Error:', error.response?.data || error.message);
    
    // Hata mesajını kullanıcı dostu hale getir
    if (error.response?.data) {
      const apiError: ApiError = error.response.data;
      error.message = apiError.message || 'Bir hata oluştu';
    }
    
    return Promise.reject(error);
  }
);

/**
 * User Service Class
 * Backend API ile iletişim kuran servis sınıfı
 */
export class UserService {
  
  /**
   * Tüm kullanıcıları getir
   * @returns Promise<User[]>
   */
  static async getAllUsers(): Promise<User[]> {
    try {
      const response: AxiosResponse<User[]> = await api.get('/users');
      return response.data;
    } catch (error) {
      console.error('Kullanıcılar getirilirken hata:', error);
      throw error;
    }
  }

  /**
   * ID'ye göre kullanıcı getir
   * @param id - kullanıcı ID'si
   * @returns Promise<User>
   */
  static async getUserById(id: number): Promise<User> {
    try {
      const response: AxiosResponse<User> = await api.get(`/users/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Kullanıcı (ID: ${id}) getirilirken hata:`, error);
      throw error;
    }
  }

  /**
   * Yeni kullanıcı oluştur
   * @param userData - kullanıcı bilgileri
   * @returns Promise<User>
   */
  static async createUser(userData: UserRequest): Promise<User> {
    try {
      const response: AxiosResponse<User> = await api.post('/users', userData);
      return response.data;
    } catch (error) {
      console.error('Kullanıcı oluşturulurken hata:', error);
      throw error;
    }
  }

  /**
   * Kullanıcı güncelle
   * @param id - kullanıcı ID'si
   * @param userData - yeni kullanıcı bilgileri
   * @returns Promise<User>
   */
  static async updateUser(id: number, userData: UserRequest): Promise<User> {
    try {
      const response: AxiosResponse<User> = await api.put(`/users/${id}`, userData);
      return response.data;
    } catch (error) {
      console.error(`Kullanıcı (ID: ${id}) güncellenirken hata:`, error);
      throw error;
    }
  }

  /**
   * Kullanıcı sil
   * @param id - silinecek kullanıcı ID'si
   * @returns Promise<void>
   */
  static async deleteUser(id: number): Promise<void> {
    try {
      await api.delete(`/users/${id}`);
    } catch (error) {
      console.error(`Kullanıcı (ID: ${id}) silinirken hata:`, error);
      throw error;
    }
  }

  /**
   * Kullanıcı arama
   * @param searchTerm - arama terimi
   * @returns Promise<User[]>
   */
  static async searchUsers(searchTerm: string): Promise<User[]> {
    try {
      const response: AxiosResponse<User[]> = await api.get(`/users/search?q=${encodeURIComponent(searchTerm)}`);
      return response.data;
    } catch (error) {
      console.error(`Kullanıcı aranırken hata (${searchTerm}):`, error);
      throw error;
    }
  }
}

export default UserService;
```

### 20. src/components/Layout.tsx

```tsx
import React from 'react';

/**
 * Layout Props interface
 */
interface LayoutProps {
  children: React.ReactNode;
}

/**
 * Layout Component
 * Tüm sayfalar için ortak layout sağlar
 * Header, main content area ve footer içerir
 */
const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center">
              <h1 className="text-2xl font-bold text-gray-900">
                👥 Kullanıcı Yönetim Sistemi
              </h1>
            </div>
            <div className="text-sm text-gray-500">
              React + Spring Boot
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-auto">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="py-4 text-center text-sm text-gray-500">
            © 2024 Kullanıcı Yönetim Sistemi. Modern web teknolojileri ile geliştirilmiştir.
          </div>
        </div>
      </footer>
    </div>
  );
};

;