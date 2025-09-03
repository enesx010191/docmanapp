```java
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender - All Logs -->
    <appender name="FILE_ALL" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
            <totalSizeCap>100MB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- File Appender - Error Logs Only -->
    <appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n%ex</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>5MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>60</maxHistory>
        </rollingPolicy>
    </appender>
    
    <!-- Database Operations Logger -->
    <appender name="FILE_DATABASE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/database.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/database.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>15</maxHistory>
        </rollingPolicy>
    </appender>
    
    <!-- Async Wrapper for better performance -->
    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="FILE_ALL" />
        <queueSize>1024</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <includeCallerData>false</includeCallerData>
    </appender>
    
    <!-- Logger Configurations -->
    
    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ASYNC_FILE" />
        <appender-ref ref="FILE_ERROR" />
    </root>
    
    <!-- Application Specific Loggers -->
    <logger name="com.example.devicelanguagemodelsystem" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ASYNC_FILE" />
        <appender-ref ref="FILE_ERROR" />
    </logger>
    
    <!-- Database Related Loggers -->
    <logger name="org.hibernate.SQL" level="DEBUG" additivity="false">
        <appender-ref ref="FILE_DATABASE" />
        <appender-ref ref="CONSOLE" />
    </logger>
    
    <logger name="org.hibernate.type.descriptor.sql" level="TRACE" additivity="false">
        <appender-ref ref="FILE_DATABASE" />
    </logger>
    
    <!-- Spring Framework Loggers -->
    <logger name="org.springframework.web" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ASYNC_FILE" />
    </logger>
    
    <logger name="org.springframework.security" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ASYNC_FILE" />
    </logger>
    
    <!-- Third Party Loggers -->
    <logger name="org.apache.http" level="WARN" />
    <logger name="org.springframework" level="INFO" />
    <logger name="org.hibernate" level="WARN" />
    
    <!-- Profile Specific Configurations -->
    <springProfile name="dev">
        <logger name="com.example.devicelanguagemodelsystem" level="TRACE" />
        <logger name="org.springframework.web" level="TRACE" />
    </springProfile>
    
    <springProfile name="prod">
        <logger name="com.example.devicelanguagemodelsystem" level="INFO" />
        <logger name="org.springframework.web" level="WARN" />
        <logger name="org.hibernate.SQL" level="WARN" />
    </springProfile>
    
</configuration>
```

```java
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/device_language_model_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Application Configuration
server.port=8080
spring.application.name=device-language-model-system

# Logging Configuration
logging.config=classpath:logback-spring.xml
logging.level.root=INFO
logging.level.com.example.devicelanguagemodelsystem=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Log File Configuration
logging.file.path=logs
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %highlight(%-5level) %cyan(%logger{15}) - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n

# Performance and Debug Logging
spring.jpa.properties.hibernate.generate_statistics=true
spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=1000
```

```java
package com.example.devicelanguagemodelsystem.service;

import com.example.devicelanguagemodelsystem.entity.Device;
import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import com.example.devicelanguagemodelsystem.repository.DeviceRepository;
import com.example.devicelanguagemodelsystem.repository.LanguageModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    
    private final DeviceRepository deviceRepository;
    private final LanguageModelRepository languageModelRepository;
    
    @Autowired
    public DeviceService(DeviceRepository deviceRepository, LanguageModelRepository languageModelRepository) {
        this.deviceRepository = deviceRepository;
        this.languageModelRepository = languageModelRepository;
        logger.info("DeviceService initialized");
    }
    
    public List<Device> getAllDevices() {
        logger.debug("Fetching all devices");
        List<Device> devices = deviceRepository.findAll();
        logger.info("Found {} devices", devices.size());
        return devices;
    }
    
    public Optional<Device> getDeviceById(Long id) {
        logger.debug("Fetching device with id: {}", id);
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isPresent()) {
            logger.info("Device found: {}", device.get().getName());
        } else {
            logger.warn("Device not found with id: {}", id);
        }
        return device;
    }
    
    public Optional<Device> getDeviceByName(String name) {
        logger.debug("Fetching device with name: {}", name);
        Optional<Device> device = deviceRepository.findByName(name);
        if (device.isPresent()) {
            logger.info("Device found by name: {}", name);
        } else {
            logger.warn("Device not found with name: {}", name);
        }
        return device;
    }
    
    public Device createDevice(Device device) {
        logger.info("Creating new device: {}", device.getName());
        try {
            Device savedDevice = deviceRepository.save(device);
            logger.info("Device created successfully with id: {}", savedDevice.getId());
            return savedDevice;
        } catch (Exception e) {
            logger.error("Error creating device: {}", device.getName(), e);
            throw e;
        }
    }
    
    public Device updateDevice(Long id, Device deviceDetails) {
        logger.info("Updating device with id: {}", id);
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Device not found for update with id: {}", id);
                    return new RuntimeException("Device not found with id: " + id);
                });
        
        logger.debug("Original device: {}", device.getName());
        
        device.setName(deviceDetails.getName());
        device.setDescription(deviceDetails.getDescription());
        device.setDeviceType(deviceDetails.getDeviceType());
        device.setOperatingSystem(deviceDetails.getOperatingSystem());
        device.setMemoryGB(deviceDetails.getMemoryGB());
        device.setStorageGB(deviceDetails.getStorageGB());
        
        try {
            Device updatedDevice = deviceRepository.save(device);
            logger.info("Device updated successfully: {}", updatedDevice.getName());
            return updatedDevice;
        } catch (Exception e) {
            logger.error("Error updating device with id: {}", id, e);
            throw e;
        }
    }
    
    public void deleteDevice(Long id) {
        logger.info("Deleting device with id: {}", id);
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Device not found for deletion with id: {}", id);
                    return new RuntimeException("Device not found with id: " + id);
                });
        
        logger.debug("Removing language model associations for device: {}", device.getName());
        
        try {
            // Remove all language model associations before deleting
            device.getLanguageModels().clear();
            deviceRepository.save(device);
            deviceRepository.delete(device);
            logger.info("Device deleted successfully: {}", device.getName());
        } catch (Exception e) {
            logger.error("Error deleting device with id: {}", id, e);
            throw e;
        }
    }
    
    public Device addLanguageModelToDevice(Long deviceId, Long languageModelId) {
        logger.info("Adding language model {} to device {}", languageModelId, deviceId);
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> {
                    logger.error("Device not found with id: {}", deviceId);
                    return new RuntimeException("Device not found with id: " + deviceId);
                });
        
        LanguageModel languageModel = languageModelRepository.findById(languageModelId)
                .orElseThrow(() -> {
                    logger.error("Language model not found with id: {}", languageModelId);
                    return new RuntimeException("Language model not found with id: " + languageModelId);
                });
        
        try {
            device.addLanguageModel(languageModel);
            Device savedDevice = deviceRepository.save(device);
            logger.info("Language model '{}' added to device '{}' successfully", 
                       languageModel.getName(), device.getName());
            return savedDevice;
        } catch (Exception e) {
            logger.error("Error adding language model {} to device {}", languageModelId, deviceId, e);
            throw e;
        }
    }
    
    public Device removeLanguageModelFromDevice(Long deviceId, Long languageModelId) {
        logger.info("Removing language model {} from device {}", languageModelId, deviceId);
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> {
                    logger.error("Device not found with id: {}", deviceId);
                    return new RuntimeException("Device not found with id: " + deviceId);
                });
        
        LanguageModel languageModel = languageModelRepository.findById(languageModelId)
                .orElseThrow(() -> {
                    logger.error("Language model not found with id: {}", languageModelId);
                    return new RuntimeException("Language model not found with id: " + languageModelId);
                });
        
        try {
            device.removeLanguageModel(languageModel);
            Device savedDevice = deviceRepository.save(device);
            logger.info("Language model '{}' removed from device '{}' successfully", 
                       languageModel.getName(), device.getName());
            return savedDevice;
        } catch (Exception e) {
            logger.error("Error removing language model {} from device {}", languageModelId, deviceId, e);
            throw e;
        }
    }
    
    public List<LanguageModel> getLanguageModelsOnDevice(Long deviceId) {
        logger.debug("Fetching language models for device id: {}", deviceId);
        List<LanguageModel> models = languageModelRepository.findLanguageModelsOnDevice(deviceId);
        logger.info("Found {} language models on device {}", models.size(), deviceId);
        return models;
    }
    
    public List<Device> getDevicesByType(String deviceType) {
        logger.debug("Fetching devices by type: {}", deviceType);
        List<Device> devices = deviceRepository.findByDeviceType(deviceType);
        logger.info("Found {} devices of type: {}", devices.size(), deviceType);
        return devices;
    }
    
    public List<Device> getDevicesByOperatingSystem(String operatingSystem) {
        logger.debug("Fetching devices by OS: {}", operatingSystem);
        List<Device> devices = deviceRepository.findByOperatingSystem(operatingSystem);
        logger.info("Found {} devices with OS: {}", devices.size(), operatingSystem);
        return devices;
    }
    
    public List<Device> getDevicesWithMinMemory(Double minMemory) {
        logger.debug("Fetching devices with minimum memory: {} GB", minMemory);
        List<Device> devices = deviceRepository.findByMinMemory(minMemory);
        logger.info("Found {} devices with minimum {} GB memory", devices.size(), minMemory);
        return devices;
    }
    
    public List<Device> getDevicesWithMinStorage(Double minStorage) {
        logger.debug("Fetching devices with minimum storage: {} GB", minStorage);
        List<Device> devices = deviceRepository.findByMinStorage(minStorage);
        logger.info("Found {} devices with minimum {} GB storage", devices.size(), minStorage);
        return devices;
    }
    
    public List<Device> getDevicesWithLanguageModels() {
        logger.debug("Fetching devices with language models");
        List<Device> devices = deviceRepository.findDevicesWithLanguageModels();
        logger.info("Found {} devices with language models", devices.size());
        return devices;
    }
    
    public List<Device> getDevicesWithoutLanguageModels() {
        logger.debug("Fetching devices without language models");
        List<Device> devices = deviceRepository.findDevicesWithoutLanguageModels();
        logger.info("Found {} devices without language models", devices.size());
        return devices;
    }
    
    public List<Device> getDevicesWithLanguageModel(Long languageModelId) {
        logger.debug("Fetching devices containing language model: {}", languageModelId);
        List<Device> devices = deviceRepository.findDevicesWithLanguageModel(languageModelId);
        logger.info("Found {} devices containing language model {}", devices.size(), languageModelId);
        return devices;
    }
}
```

```java
package com.example.devicelanguagemodelsystem.controller;

import com.example.devicelanguagemodelsystem.entity.Device;
import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import com.example.devicelanguagemodelsystem.service.DeviceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
    
    private final DeviceService deviceService;
    
    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
        logger.info("DeviceController initialized");
    }
    
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        logger.info("GET /api/devices - Fetching all devices");
        List<Device> devices = deviceService.getAllDevices();
        logger.info("Returning {} devices", devices.size());
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        logger.info("GET /api/devices/{} - Fetching device by id", id);
        Optional<Device> device = deviceService.getDeviceById(id);
        
        if (device.isPresent()) {
            logger.info("Device found: {}", device.get().getName());
            return ResponseEntity.ok(device.get());
        } else {
            logger.warn("Device not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Device> getDeviceByName(@PathVariable String name) {
        logger.info("GET /api/devices/name/{} - Fetching device by name", name);
        Optional<Device> device = deviceService.getDeviceByName(name);
        
        if (device.isPresent()) {
            logger.info("Device found by name: {}", name);
            return ResponseEntity.ok(device.get());
        } else {
            logger.warn("Device not found with name: {}", name);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody Device device) {
        logger.info("POST /api/devices - Creating new device: {}", device.getName());
        
        try {
            Device createdDevice = deviceService.createDevice(device);
            logger.info("Device created successfully with id: {}", createdDevice.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
        } catch (Exception e) {
            logger.error("Error creating device: {}", device.getName(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, 
                                             @Valid @RequestBody Device deviceDetails) {
        logger.info("PUT /api/devices/{} - Updating device", id);
        
        try {
            Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
            logger.info("Device updated successfully: {}", updatedDevice.getName());
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            logger.error("Device not found for update with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating device with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        logger.info("DELETE /api/devices/{} - Deleting device", id);
        
        try {
            deviceService.deleteDevice(id);
            logger.info("Device deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("Device not found for deletion with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting device with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{deviceId}/language-models/{languageModelId}")
    public ResponseEntity<Device> addLanguageModelToDevice(@PathVariable Long deviceId, 
                                                          @PathVariable Long languageModelId) {
        logger.info("POST /api/devices/{}/language-models/{} - Adding language model to device", 
                   deviceId, languageModelId);
        
        try {
            Device updatedDevice = deviceService.addLanguageModelToDevice(deviceId, languageModelId);
            logger.info("Language model {} added to device {} successfully", languageModelId, deviceId);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            logger.error("Error adding language model {} to device {}: {}", 
                        languageModelId, deviceId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error adding language model {} to device {}", 
                        languageModelId, deviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{deviceId}/language-models/{languageModelId}")
    public ResponseEntity<Device> removeLanguageModelFromDevice(@PathVariable Long deviceId, 
                                                               @PathVariable Long languageModelId) {
        logger.info("DELETE /api/devices/{}/language-models/{} - Removing language model from device", 
                   deviceId, languageModelId);
        
        try {
            Device updatedDevice = deviceService.removeLanguageModelFromDevice(deviceId, languageModelId);
            logger.info("Language model {} removed from device {} successfully", languageModelId, deviceId);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            logger.error("Error removing language model {} from device {}: {}", 
                        languageModelId, deviceId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Unexpected error removing language model {} from device {}", 
                        languageModelId, deviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/language-models")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsOnDevice(@PathVariable Long id) {
        logger.info("GET /api/devices/{}/language-models - Fetching language models for device", id);
        
        try {
            List<LanguageModel> languageModels = deviceService.getLanguageModelsOnDevice(id);
            logger.info("Found {} language models on device {}", languageModels.size(), id);
            return ResponseEntity.ok(languageModels);
        } catch (Exception e) {
            logger.error("Error fetching language models for device {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-type/{deviceType}")
    public ResponseEntity<List<Device>> getDevicesByType(@PathVariable String deviceType) {
        logger.info("GET /api/devices/by-type/{} - Fetching devices by type", deviceType);
        List<Device> devices = deviceService.getDevicesByType(deviceType);
        logger.info("Found {} devices of type: {}", devices.size(), deviceType);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/by-os/{operatingSystem}")
    public ResponseEntity<List<Device>> getDevicesByOperatingSystem(@PathVariable String operatingSystem) {
        logger.info("GET /api/devices/by-os/{} - Fetching devices by OS", operatingSystem);
        List<Device> devices = deviceService.getDevicesByOperatingSystem(operatingSystem);
        logger.info("Found {} devices with OS: {}", devices.size(), operatingSystem);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/min-memory/{minMemory}")
    public ResponseEntity<List<Device>> getDevicesWithMinMemory(@PathVariable Double minMemory) {
        logger.info("GET /api/devices/min-memory/{} - Fetching devices with minimum memory", minMemory);
        List<Device> devices = deviceService.getDevicesWithMinMemory(minMemory);
        logger.info("Found {} devices with minimum {} GB memory", devices.size(), minMemory);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/min-storage/{minStorage}")
    public ResponseEntity<List<Device>> getDevicesWithMinStorage(@PathVariable Double minStorage) {
        logger.info("GET /api/devices/min-storage/{} - Fetching devices with minimum storage", minStorage);
        List<Device> devices = deviceService.getDevicesWithMinStorage(minStorage);
        logger.info("Found {} devices with minimum {} GB storage", devices.size(), minStorage);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/with-language-models")
    public ResponseEntity<List<Device>> getDevicesWithLanguageModels() {
        logger.info("GET /api/devices/with-language-models - Fetching devices with language models");
        List<Device> devices = deviceService.getDevicesWithLanguageModels();
        logger.info("Found {} devices with language models", devices.size());
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/without-language-models")
    public ResponseEntity<List<Device>> getDevicesWithoutLanguageModels() {
        logger.info("GET /api/devices/without-language-models - Fetching devices without language models");
        List<Device> devices = deviceService.getDevicesWithoutLanguageModels();
        logger.info("Found {} devices without language models", devices.size());
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/with-language-model/{languageModelId}")
    public ResponseEntity<List<Device>> getDevicesWithLanguageModel(@PathVariable Long languageModelId) {
        logger.info("GET /api/devices/with-language-model/{} - Fetching devices containing language model", languageModelId);
        List<Device> devices = deviceService.getDevicesWithLanguageModel(languageModelId);
        logger.info("Found {} devices containing language model {}", devices.size(), languageModelId);
        return ResponseEntity.ok(devices);
    }
}
    package com.example.devicelanguagemodelsystem.controller;

import com.example.devicelanguagemodelsystem.entity.Device;
import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import com.example.devicelanguagemodelsystem.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    private final DeviceService deviceService;
    
    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
    
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Optional<Device> device = deviceService.getDeviceById(id);
        return device.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Device> getDeviceByName(@PathVariable String name) {
        Optional<Device> device = deviceService.getDeviceByName(name);
        return device.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody Device device) {
        try {
            Device createdDevice = deviceService.createDevice(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, 
                                             @Valid @RequestBody Device deviceDetails) {
        try {
            Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{deviceId}/language-models/{languageModelId}")
    public ResponseEntity<Device> addLanguageModelToDevice(@PathVariable Long deviceId, 
                                                          @PathVariable Long languageModelId) {
        try {
            Device updatedDevice = deviceService.addLanguageModelToDevice(deviceId, languageModelId);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{deviceId}/language-models/{languageModelId}")
    public ResponseEntity<Device> removeLanguageModelFromDevice(@PathVariable Long deviceId, 
                                                               @PathVariable Long languageModelId) {
        try {
            Device updatedDevice = deviceService.removeLanguageModelFromDevice(deviceId, languageModelId);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/language-models")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsOnDevice(@PathVariable Long id) {
        try {
            List<LanguageModel> languageModels = deviceService.getLanguageModelsOnDevice(id);
            return ResponseEntity.ok(languageModels);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-type/{deviceType}")
    public ResponseEntity<List<Device>> getDevicesByType(@PathVariable String deviceType) {
        List<Device> devices = deviceService.getDevicesByType(deviceType);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/by-os/{operatingSystem}")
    public ResponseEntity<List<Device>> getDevicesByOperatingSystem(@PathVariable String operatingSystem) {
        List<Device> devices = deviceService.getDevicesByOperatingSystem(operatingSystem);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/min-memory/{minMemory}")
    public ResponseEntity<List<Device>> getDevicesWithMinMemory(@PathVariable Double minMemory) {
        List<Device> devices = deviceService.getDevicesWithMinMemory(minMemory);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/min-storage/{minStorage}")
    public ResponseEntity<List<Device>> getDevicesWithMinStorage(@PathVariable Double minStorage) {
        List<Device> devices = deviceService.getDevicesWithMinStorage(minStorage);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/with-language-models")
    public ResponseEntity<List<Device>> getDevicesWithLanguageModels() {
        List<Device> devices = deviceService.getDevicesWithLanguageModels();
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/without-language-models")
    public ResponseEntity<List<Device>> getDevicesWithoutLanguageModels() {
        List<Device> devices = deviceService.getDevicesWithoutLanguageModels();
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/with-language-model/{languageModelId}")
    public ResponseEntity<List<Device>> getDevicesWithLanguageModel(@PathVariable Long languageModelId) {
        List<Device> devices = deviceService.getDevicesWithLanguageModel(languageModelId);
        return ResponseEntity.ok(devices);
    }
}
```

```java
package com.example.devicelanguagemodelsystem.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    /**
     * Pointcut for service layer methods
     */
    @Pointcut("execution(* com.example.devicelanguagemodelsystem.service.*.*(..))")
    public void serviceLayer() {}
    
    /**
     * Pointcut for controller layer methods
     */
    @Pointcut("execution(* com.example.devicelanguagemodelsystem.controller.*.*(..))")
    public void controllerLayer() {}
    
    /**
     * Pointcut for repository layer methods
     */
    @Pointcut("execution(* com.example.devicelanguagemodelsystem.repository.*.*(..))")
    public void repositoryLayer() {}
    
    /**
     * Around advice for performance monitoring
     */
    @Around("serviceLayer() || controllerLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        logger.debug("==> {}.{}() with arguments: {}", className, methodName, Arrays.toString(args));
        
        stopWatch.start();
        Object result;
        
        try {
            result = joinPoint.proceed();
            stopWatch.stop();
            
            long executionTime = stopWatch.getTotalTimeMillis();
            if (executionTime > 1000) {
                logger.warn("<== {}.{}() executed in {} ms (SLOW)", className, methodName, executionTime);
            } else if (executionTime > 500) {
                logger.info("<== {}.{}() executed in {} ms", className, methodName, executionTime);
            } else {
                logger.debug("<== {}.{}() executed in {} ms", className, methodName, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            stopWatch.stop();
            logger.error("<== {}.{}() failed after {} ms with error: {}", 
                        className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Before advice for method entry logging
     */
    @Before("repositoryLayer()")
    public void logRepositoryMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        logger.trace("DB Operation: {}.{}() with args: {}", className, methodName, Arrays.toString(args));
    }
    
    /**
     * After returning advice for successful operations
     */
    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        if (result != null) {
            logger.debug("Method {}.{}() returned: {}", className, methodName, result.getClass().getSimpleName());
        } else {
            logger.debug("Method {}.{}() returned: null", className, methodName);
        }
    }
    
    /**
     * After throwing advice for exception logging
     */
    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        logger.error("Exception in {}.{}() with args {}: {}", 
                    className, methodName, Arrays.toString(args), exception.getMessage(), exception);
    }
    
    /**
     * Custom pointcut for methods that modify data
     */
    @Pointcut("execution(* com.example.devicelanguagemodelsystem.service.*.create*(..)) || " +
              "execution(* com.example.devicelanguagemodelsystem.service.*.update*(..)) || " +
              "execution(* com.example.devicelanguagemodelsystem.service.*.delete*(..)) || " +
              "execution(* com.example.devicelanguagemodelsystem.service.*.add*(..)) || " +
              "execution(* com.example.devicelanguagemodelsystem.service.*.remove*(..))")
    public void dataModifyingMethods() {}
    
    /**
     * Special logging for data modification operations
     */
    @Around("dataModifyingMethods()")
    public Object logDataModification(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        logger.info("DATA MODIFICATION: Starting {}.{}() with args: {}", 
                   className, methodName, Arrays.toString(args));
        
        try {
            Object result = joinPoint.proceed();
            logger.info("DATA MODIFICATION: Successfully completed {}.{}()", className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("DATA MODIFICATION: Failed {}.{}() - {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
```

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>device-language-model-system</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>device-language-model-system</name>
    <description>Device and Language Model Management System</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

```
Java Spring Boot projenize kapsamlı bir logger sistemi eklemiş bulunuyorum! İşte eklediğim temel bileşenler:

## **🔧 Eklenen Bileşenler:**

### 1. **Logback Konfigürasyonu (`logback-spring.xml`)**
- **Console Appender**: Renkli konsol çıktısı
- **File Appenders**: 
  - `application.log` (tüm loglar)
  - `error.log` (sadece hata logları)
  - `database.log` (veritabanı işlemleri)
- **Rolling Policy**: Dosya boyutu/zaman bazlı log rotation
- **Async Appender**: Performans için asenkron logging

### 2. **Log Seviyeleri:**
- **TRACE**: En detaylı (DB query'leri, parametre değerleri)
- **DEBUG**: Geliştirme bilgileri (method entry/exit)
- **INFO**: Önemli olaylar (başarılı işlemler)  
- **WARN**: Uyarılar (bulunamayan kayıtlar)
- **ERROR**: Hatalar (exception'lar, sistem hataları)

### 3. **AOP Logging (`LoggingAspect`)**
- **Performance Monitoring**: Method execution time
- **Method Entry/Exit**: Automatic logging
- **Exception Handling**: Otomatik hata loglama
- **Data Modification Tracking**: CRUD işlemlerini özel olarak loglar

### 4. **Service ve Controller'lara Logger Eklendi**
- Structured logging with parameters
- Exception handling with context
- Business logic flow tracking

## **📂 Dosya Yapısı:**
```
src/main/resources/
├── logback-spring.xml          (Ana log config)
├── application.properties      (Log levels)
logs/
├── application.log            (Tüm loglar)
├── error.log                  (Sadece hatalar)  
└── database.log               (DB işlemleri)
```

## **💡 Kullanım Örnekleri:**

```java
// Service'te
logger.info("Device created successfully with id: {}", deviceId);
logger.warn("Device not found with id: {}", id);
logger.error("Error creating device: {}", deviceName, exception);

// Controller'da
logger.info("POST /api/devices - Creating device: {}", device.getName());
```

## **⚙️ Konfigürasyon:**

**Development için:**
```properties
logging.level.com.example.devicelanguagemodelsystem=DEBUG
```

**Production için:**
```properties
logging.level.com.example.devicelanguagemodelsystem=INFO
```

## **🚀 Özellikler:**
- ✅ Otomatik performance monitoring (1000ms üzeri SLOW warning)
- ✅ Exception tracking with stack traces  
- ✅ Database operation logging
- ✅ File rotation (10MB chunks, 30 günlük history)
- ✅ Colored console output
- ✅ Environment-specific configurations
- ✅ AOP ile method-level automatic logging

Logger sistemi artık hazır! Log dosyalarını `logs/` klasöründe görebilir, farklı environment'lar için farklı log seviyeleri ayarlayabilirsiniz.
```
