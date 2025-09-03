```java
package com.example.devicelanguagemodelsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "devices")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Device name is required")
    @Size(min = 2, max = 100, message = "Device name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @NotBlank(message = "Device type is required")
    @Column(nullable = false)
    private String deviceType;
    
    @Column(name = "operating_system")
    private String operatingSystem;
    
    @Column(name = "memory_gb")
    private Double memoryGB;
    
    @Column(name = "storage_gb")
    private Double storageGB;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(mappedBy = "devices", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("devices")
    private Set<LanguageModel> languageModels = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Device() {}
    
    public Device(String name, String description, String deviceType, String operatingSystem, Double memoryGB, Double storageGB) {
        this.name = name;
        this.description = description;
        this.deviceType = deviceType;
        this.operatingSystem = operatingSystem;
        this.memoryGB = memoryGB;
        this.storageGB = storageGB;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getOperatingSystem() {
        return operatingSystem;
    }
    
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
    
    public Double getMemoryGB() {
        return memoryGB;
    }
    
    public void setMemoryGB(Double memoryGB) {
        this.memoryGB = memoryGB;
    }
    
    public Double getStorageGB() {
        return storageGB;
    }
    
    public void setStorageGB(Double storageGB) {
        this.storageGB = storageGB;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Set<LanguageModel> getLanguageModels() {
        return languageModels;
    }
    
    public void setLanguageModels(Set<LanguageModel> languageModels) {
        this.languageModels = languageModels;
    }
    
    // Helper methods
    public void addLanguageModel(LanguageModel languageModel) {
        this.languageModels.add(languageModel);
        languageModel.getDevices().add(this);
    }
    
    public void removeLanguageModel(LanguageModel languageModel) {
        this.languageModels.remove(languageModel);
        languageModel.getDevices().remove(this);
    }
    
    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", memoryGB=" + memoryGB +
                ", storageGB=" + storageGB +
                '}';
    }
}
```

```java
package com.example.devicelanguagemodelsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "language_models")
public class LanguageModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Model name is required")
    @Size(min = 2, max = 100, message = "Model name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "model_version")
    private String version;
    
    @Column(name = "model_size")
    private String size;
    
    @Column(name = "parameters_count")
    private Long parametersCount;
    
    @Column(name = "provider")
    private String provider;
    
    @Column(name = "model_type")
    private String modelType;
    
    @Column(name = "memory_requirements_gb")
    private Double memoryRequirementsGB;
    
    @Column(name = "storage_requirements_gb")
    private Double storageRequirementsGB;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "device_language_model",
        joinColumns = @JoinColumn(name = "language_model_id"),
        inverseJoinColumns = @JoinColumn(name = "device_id")
    )
    @JsonIgnoreProperties("languageModels")
    private Set<Device> devices = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public LanguageModel() {}
    
    public LanguageModel(String name, String description, String version, String size, 
                        Long parametersCount, String provider, String modelType,
                        Double memoryRequirementsGB, Double storageRequirementsGB) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.size = size;
        this.parametersCount = parametersCount;
        this.provider = provider;
        this.modelType = modelType;
        this.memoryRequirementsGB = memoryRequirementsGB;
        this.storageRequirementsGB = storageRequirementsGB;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public Long getParametersCount() {
        return parametersCount;
    }
    
    public void setParametersCount(Long parametersCount) {
        this.parametersCount = parametersCount;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getModelType() {
        return modelType;
    }
    
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
    
    public Double getMemoryRequirementsGB() {
        return memoryRequirementsGB;
    }
    
    public void setMemoryRequirementsGB(Double memoryRequirementsGB) {
        this.memoryRequirementsGB = memoryRequirementsGB;
    }
    
    public Double getStorageRequirementsGB() {
        return storageRequirementsGB;
    }
    
    public void setStorageRequirementsGB(Double storageRequirementsGB) {
        this.storageRequirementsGB = storageRequirementsGB;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Set<Device> getDevices() {
        return devices;
    }
    
    public void setDevices(Set<Device> devices) {
        this.devices = devices;
    }
    
    // Helper methods
    public void addDevice(Device device) {
        this.devices.add(device);
        device.getLanguageModels().add(this);
    }
    
    public void removeDevice(Device device) {
        this.devices.remove(device);
        device.getLanguageModels().remove(this);
    }
    
    @Override
    public String toString() {
        return "LanguageModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", size='" + size + '\'' +
                ", provider='" + provider + '\'' +
                ", modelType='" + modelType + '\'' +
                '}';
    }
}
```

```java
package com.example.devicelanguagemodelsystem.repository;

import com.example.devicelanguagemodelsystem.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByName(String name);
    
    List<Device> findByDeviceType(String deviceType);
    
    List<Device> findByOperatingSystem(String operatingSystem);
    
    @Query("SELECT d FROM Device d WHERE d.memoryGB >= :minMemory")
    List<Device> findByMinMemory(@Param("minMemory") Double minMemory);
    
    @Query("SELECT d FROM Device d WHERE d.storageGB >= :minStorage")
    List<Device> findByMinStorage(@Param("minStorage") Double minStorage);
    
    @Query("SELECT d FROM Device d JOIN d.languageModels lm WHERE lm.id = :languageModelId")
    List<Device> findDevicesWithLanguageModel(@Param("languageModelId") Long languageModelId);
    
    @Query("SELECT d FROM Device d WHERE SIZE(d.languageModels) > 0")
    List<Device> findDevicesWithLanguageModels();
    
    @Query("SELECT d FROM Device d WHERE SIZE(d.languageModels) = 0")
    List<Device> findDevicesWithoutLanguageModels();
    
    @Query("SELECT d FROM Device d WHERE SIZE(d.languageModels) = :count")
    List<Device> findDevicesWithLanguageModelCount(@Param("count") int count);
}
```

```java
package com.example.devicelanguagemodelsystem.repository;

import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageModelRepository extends JpaRepository<LanguageModel, Long> {
    
    Optional<LanguageModel> findByName(String name);
    
    List<LanguageModel> findByProvider(String provider);
    
    List<LanguageModel> findByModelType(String modelType);
    
    List<LanguageModel> findByVersion(String version);
    
    @Query("SELECT lm FROM LanguageModel lm WHERE lm.parametersCount >= :minParams")
    List<LanguageModel> findByMinParametersCount(@Param("minParams") Long minParams);
    
    @Query("SELECT lm FROM LanguageModel lm WHERE lm.memoryRequirementsGB <= :maxMemory")
    List<LanguageModel> findByMaxMemoryRequirement(@Param("maxMemory") Double maxMemory);
    
    @Query("SELECT lm FROM LanguageModel lm WHERE lm.storageRequirementsGB <= :maxStorage")
    List<LanguageModel> findByMaxStorageRequirement(@Param("maxStorage") Double maxStorage);
    
    @Query("SELECT lm FROM LanguageModel lm JOIN lm.devices d WHERE d.id = :deviceId")
    List<LanguageModel> findLanguageModelsOnDevice(@Param("deviceId") Long deviceId);
    
    @Query("SELECT lm FROM LanguageModel lm WHERE SIZE(lm.devices) > 0")
    List<LanguageModel> findLanguageModelsWithDevices();
    
    @Query("SELECT lm FROM LanguageModel lm WHERE SIZE(lm.devices) = 0")
    List<LanguageModel> findLanguageModelsWithoutDevices();
    
    @Query("SELECT lm FROM LanguageModel lm WHERE SIZE(lm.devices) = :count")
    List<LanguageModel> findLanguageModelsWithDeviceCount(@Param("count") int count);
    
    @Query("SELECT lm, SIZE(lm.devices) as deviceCount FROM LanguageModel lm GROUP BY lm ORDER BY deviceCount DESC")
    List<Object[]> findLanguageModelsOrderByDeviceCount();
}
```

```java
package com.example.devicelanguagemodelsystem.service;

import com.example.devicelanguagemodelsystem.entity.Device;
import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import com.example.devicelanguagemodelsystem.repository.DeviceRepository;
import com.example.devicelanguagemodelsystem.repository.LanguageModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final LanguageModelRepository languageModelRepository;
    
    @Autowired
    public DeviceService(DeviceRepository deviceRepository, LanguageModelRepository languageModelRepository) {
        this.deviceRepository = deviceRepository;
        this.languageModelRepository = languageModelRepository;
    }
    
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }
    
    public Optional<Device> getDeviceByName(String name) {
        return deviceRepository.findByName(name);
    }
    
    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }
    
    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
        
        device.setName(deviceDetails.getName());
        device.setDescription(deviceDetails.getDescription());
        device.setDeviceType(deviceDetails.getDeviceType());
        device.setOperatingSystem(deviceDetails.getOperatingSystem());
        device.setMemoryGB(deviceDetails.getMemoryGB());
        device.setStorageGB(deviceDetails.getStorageGB());
        
        return deviceRepository.save(device);
    }
    
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
        
        // Remove all language model associations before deleting
        device.getLanguageModels().clear();
        deviceRepository.save(device);
        deviceRepository.delete(device);
    }
    
    public Device addLanguageModelToDevice(Long deviceId, Long languageModelId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
        
        LanguageModel languageModel = languageModelRepository.findById(languageModelId)
                .orElseThrow(() -> new RuntimeException("Language model not found with id: " + languageModelId));
        
        device.addLanguageModel(languageModel);
        return deviceRepository.save(device);
    }
    
    public Device removeLanguageModelFromDevice(Long deviceId, Long languageModelId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
        
        LanguageModel languageModel = languageModelRepository.findById(languageModelId)
                .orElseThrow(() -> new RuntimeException("Language model not found with id: " + languageModelId));
        
        device.removeLanguageModel(languageModel);
        return deviceRepository.save(device);
    }
    
    public List<LanguageModel> getLanguageModelsOnDevice(Long deviceId) {
        return languageModelRepository.findLanguageModelsOnDevice(deviceId);
    }
    
    public List<Device> getDevicesByType(String deviceType) {
        return deviceRepository.findByDeviceType(deviceType);
    }
    
    public List<Device> getDevicesByOperatingSystem(String operatingSystem) {
        return deviceRepository.findByOperatingSystem(operatingSystem);
    }
    
    public List<Device> getDevicesWithMinMemory(Double minMemory) {
        return deviceRepository.findByMinMemory(minMemory);
    }
    
    public List<Device> getDevicesWithMinStorage(Double minStorage) {
        return deviceRepository.findByMinStorage(minStorage);
    }
    
    public List<Device> getDevicesWithLanguageModels() {
        return deviceRepository.findDevicesWithLanguageModels();
    }
    
    public List<Device> getDevicesWithoutLanguageModels() {
        return deviceRepository.findDevicesWithoutLanguageModels();
    }
    
    public List<Device> getDevicesWithLanguageModel(Long languageModelId) {
        return deviceRepository.findDevicesWithLanguageModel(languageModelId);
    }
}
```

```java
package com.example.devicelanguagemodelsystem.service;

import com.example.devicelanguagemodelsystem.entity.Device;
import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import com.example.devicelanguagemodelsystem.repository.DeviceRepository;
import com.example.devicelanguagemodelsystem.repository.LanguageModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LanguageModelService {
    
    private final LanguageModelRepository languageModelRepository;
    private final DeviceRepository deviceRepository;
    
    @Autowired
    public LanguageModelService(LanguageModelRepository languageModelRepository, DeviceRepository deviceRepository) {
        this.languageModelRepository = languageModelRepository;
        this.deviceRepository = deviceRepository;
    }
    
    public List<LanguageModel> getAllLanguageModels() {
        return languageModelRepository.findAll();
    }
    
    public Optional<LanguageModel> getLanguageModelById(Long id) {
        return languageModelRepository.findById(id);
    }
    
    public Optional<LanguageModel> getLanguageModelByName(String name) {
        return languageModelRepository.findByName(name);
    }
    
    public LanguageModel createLanguageModel(LanguageModel languageModel) {
        return languageModelRepository.save(languageModel);
    }
    
    public LanguageModel updateLanguageModel(Long id, LanguageModel languageModelDetails) {
        LanguageModel languageModel = languageModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language model not found with id: " + id));
        
        languageModel.setName(languageModelDetails.getName());
        languageModel.setDescription(languageModelDetails.getDescription());
        languageModel.setVersion(languageModelDetails.getVersion());
        languageModel.setSize(languageModelDetails.getSize());
        languageModel.setParametersCount(languageModelDetails.getParametersCount());
        languageModel.setProvider(languageModelDetails.getProvider());
        languageModel.setModelType(languageModelDetails.getModelType());
        languageModel.setMemoryRequirementsGB(languageModelDetails.getMemoryRequirementsGB());
        languageModel.setStorageRequirementsGB(languageModelDetails.getStorageRequirementsGB());
        
        return languageModelRepository.save(languageModel);
    }
    
    public void deleteLanguageModel(Long id) {
        LanguageModel languageModel = languageModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language model not found with id: " + id));
        
        // Remove all device associations before deleting
        languageModel.getDevices().clear();
        languageModelRepository.save(languageModel);
        languageModelRepository.delete(languageModel);
    }
    
    public LanguageModel addDeviceToLanguageModel(Long languageModelId, Long deviceId) {
        LanguageModel languageModel = languageModelRepository.findById(languageModelId)
                .orElseThrow(() -> new RuntimeException("Language model not found with id: " + languageModelId));
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
        
        languageModel.addDevice(device);
        return languageModelRepository.save(languageModel);
    }
    
    public LanguageModel removeDeviceFromLanguageModel(Long languageModelId, Long deviceId) {
        LanguageModel languageModel = languageModelRepository.findById(languageModelId)
                .orElseThrow(() -> new RuntimeException("Language model not found with id: " + languageModelId));
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
        
        languageModel.removeDevice(device);
        return languageModelRepository.save(languageModel);
    }
    
    public List<Device> getDevicesWithLanguageModel(Long languageModelId) {
        return deviceRepository.findDevicesWithLanguageModel(languageModelId);
    }
    
    public List<LanguageModel> getLanguageModelsByProvider(String provider) {
        return languageModelRepository.findByProvider(provider);
    }
    
    public List<LanguageModel> getLanguageModelsByType(String modelType) {
        return languageModelRepository.findByModelType(modelType);
    }
    
    public List<LanguageModel> getLanguageModelsByVersion(String version) {
        return languageModelRepository.findByVersion(version);
    }
    
    public List<LanguageModel> getLanguageModelsWithMinParameters(Long minParams) {
        return languageModelRepository.findByMinParametersCount(minParams);
    }
    
    public List<LanguageModel> getLanguageModelsWithMaxMemoryRequirement(Double maxMemory) {
        return languageModelRepository.findByMaxMemoryRequirement(maxMemory);
    }
    
    public List<LanguageModel> getLanguageModelsWithMaxStorageRequirement(Double maxStorage) {
        return languageModelRepository.findByMaxStorageRequirement(maxStorage);
    }
    
    public List<LanguageModel> getLanguageModelsWithDevices() {
        return languageModelRepository.findLanguageModelsWithDevices();
    }
    
    public List<LanguageModel> getLanguageModelsWithoutDevices() {
        return languageModelRepository.findLanguageModelsWithoutDevices();
    }
    
    public List<Object[]> getLanguageModelsOrderedByDeviceCount() {
        return languageModelRepository.findLanguageModelsOrderByDeviceCount();
    }
}
```

```java
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
package com.example.devicelanguagemodelsystem.controller;

import com.example.devicelanguagemodelsystem.entity.Device;
import com.example.devicelanguagemodelsystem.entity.LanguageModel;
import com.example.devicelanguagemodelsystem.service.LanguageModelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/language-models")
@CrossOrigin(origins = "*")
public class LanguageModelController {
    
    private final LanguageModelService languageModelService;
    
    @Autowired
    public LanguageModelController(LanguageModelService languageModelService) {
        this.languageModelService = languageModelService;
    }
    
    @GetMapping
    public ResponseEntity<List<LanguageModel>> getAllLanguageModels() {
        List<LanguageModel> languageModels = languageModelService.getAllLanguageModels();
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LanguageModel> getLanguageModelById(@PathVariable Long id) {
        Optional<LanguageModel> languageModel = languageModelService.getLanguageModelById(id);
        return languageModel.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<LanguageModel> getLanguageModelByName(@PathVariable String name) {
        Optional<LanguageModel> languageModel = languageModelService.getLanguageModelByName(name);
        return languageModel.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<LanguageModel> createLanguageModel(@Valid @RequestBody LanguageModel languageModel) {
        try {
            LanguageModel createdLanguageModel = languageModelService.createLanguageModel(languageModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLanguageModel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LanguageModel> updateLanguageModel(@PathVariable Long id, 
                                                           @Valid @RequestBody LanguageModel languageModelDetails) {
        try {
            LanguageModel updatedLanguageModel = languageModelService.updateLanguageModel(id, languageModelDetails);
            return ResponseEntity.ok(updatedLanguageModel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLanguageModel(@PathVariable Long id) {
        try {
            languageModelService.deleteLanguageModel(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{languageModelId}/devices/{deviceId}")
    public ResponseEntity<LanguageModel> addDeviceToLanguageModel(@PathVariable Long languageModelId, 
                                                                 @PathVariable Long deviceId) {
        try {
            LanguageModel updatedLanguageModel = languageModelService.addDeviceToLanguageModel(languageModelId, deviceId);
            return ResponseEntity.ok(updatedLanguageModel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{languageModelId}/devices/{deviceId}")
    public ResponseEntity<LanguageModel> removeDeviceFromLanguageModel(@PathVariable Long languageModelId, 
                                                                      @PathVariable Long deviceId) {
        try {
            LanguageModel updatedLanguageModel = languageModelService.removeDeviceFromLanguageModel(languageModelId, deviceId);
            return ResponseEntity.ok(updatedLanguageModel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/devices")
    public ResponseEntity<List<Device>> getDevicesWithLanguageModel(@PathVariable Long id) {
        try {
            List<Device> devices = languageModelService.getDevicesWithLanguageModel(id);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-provider/{provider}")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsByProvider(@PathVariable String provider) {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsByProvider(provider);
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/by-type/{modelType}")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsByType(@PathVariable String modelType) {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsByType(modelType);
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/by-version/{version}")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsByVersion(@PathVariable String version) {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsByVersion(version);
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/min-parameters/{minParams}")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsWithMinParameters(@PathVariable Long minParams) {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsWithMinParameters(minParams);
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/max-memory/{maxMemory}")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsWithMaxMemoryRequirement(@PathVariable Double maxMemory) {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsWithMaxMemoryRequirement(maxMemory);
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/max-storage/{maxStorage}")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsWithMaxStorageRequirement(@PathVariable Double maxStorage) {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsWithMaxStorageRequirement(maxStorage);
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/with-devices")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsWithDevices() {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsWithDevices();
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/without-devices")
    public ResponseEntity<List<LanguageModel>> getLanguageModelsWithoutDevices() {
        List<LanguageModel> languageModels = languageModelService.getLanguageModelsWithoutDevices();
        return ResponseEntity.ok(languageModels);
    }
    
    @GetMapping("/device-count-stats")
    public ResponseEntity<List<Map<String, Object>>> getLanguageModelsOrderedByDeviceCount() {
        List<Object[]> results = languageModelService.getLanguageModelsOrderedByDeviceCount();
        List<Map<String, Object>> response = results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("languageModel", result[0]);
                    map.put("deviceCount", result[1]);
                    return map;
                })
                .toList();
        return ResponseEntity.ok(response);
    }
}
```