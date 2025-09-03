# Spring Boot Logger Kullanım Rehberi

## Logger Seviyeleri (Log Levels)

### 1. **TRACE** - En Detaylı
```java
logger.trace("Method parameters: userId={}, deviceId={}", userId, deviceId);
logger.trace("Database query: {}", sql);
```
- En düşük seviye
- Çok detaylı debug bilgileri
- Production'da genellikle kapalı

### 2. **DEBUG** - Geliştirme
```java
logger.debug("Processing device creation for: {}", deviceName);
logger.debug("Found {} devices matching criteria", devices.size());
```
- Geliştirme sırasında kullanılır
- Uygulama akışını takip etmek için
- Production'da genellikle kapalı

### 3. **INFO** - Genel Bilgi
```java
logger.info("Device created successfully with id: {}", deviceId);
logger.info("Starting application with profile: {}", profile);
```
- Önemli olayları loglar
- Production'da açık
- Business logic'teki önemli adımlar

### 4. **WARN** - Uyarılar
```java
logger.warn("Device not found with id: {}, returning empty result", id);
logger.warn("Database connection slow: {} ms", responseTime);
```
- Sorun değil ama dikkat edilmesi gereken durumlar
- Performance sorunları
- Deprecated method kullanımları

### 5. **ERROR** - Hatalar
```java
logger.error("Failed to create device: {}", deviceName, exception);
logger.error("Database connection failed", exception);
```
- Uygulama hataları
- Exception'lar
- Kritik sorunlar

## Dosya Yapısı

Logger konfigürasyonu için şu dosyaları ekleyin:

```
src/main/resources/
├── logback-spring.xml      (Ana log konfigürasyonu)
├── application.properties  (Log seviye ayarları)
└── logs/                   (Oluşturulacak log dosyaları)
    ├── application.log
    ├── error.log
    └── database.log
```

## Logger Kullanım Örnekleri

### Service Katmanında Logger
```java
@Service
public class DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    
    public Device createDevice(Device device) {
        logger.info("Creating new device: {}", device.getName());
        
        try {
            // İş mantığı
            Device savedDevice = deviceRepository.save(device);
            logger.info("Device created successfully with id: {}", savedDevice.getId());
            return savedDevice;
            
        } catch (DataIntegrityViolationException e) {
            logger.error("Device name already exists: {}", device.getName(), e);
            throw new DuplicateDeviceException("Device name already exists");
            
        } catch (Exception e) {
            logger.error("Unexpected error creating device: {}", device.getName(), e);
            throw new ServiceException("Failed to create device");
        }
    }
}
```

### Controller Katmanında Logger
```java
@RestController
public class DeviceController {
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
    
    @PostMapping("/api/devices")
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        logger.info("POST /api/devices - Creating device: {}", device.getName());
        
        try {
            Device createdDevice = deviceService.createDevice(device);
            logger.info("Device created successfully: {}", createdDevice.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
            
        } catch (ValidationException e) {
            logger.warn("Validation error for device: {} - {}", device.getName(), e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Error creating device: {}", device.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

## Log Seviye Konfigürasyonu

### application.properties ile
```properties
# Root logger seviyesi
logging.level.root=INFO

# Paket bazlı seviyeler
logging.level.com.example.devicelanguagemodelsystem=DEBUG
logging.level.com.example.devicelanguagemodelsystem.service=INFO
logging.level.com.example.devicelanguagemodelsystem.controller=DEBUG

# Framework logları
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE

# Performance logger
logging.level.com.example.devicelanguagemodelsystem.config.LoggingAspect=INFO
```

### Environment bazlı konfigürasyon
```properties
# Development
spring.profiles.active=dev
logging.level.com.example=DEBUG

# Production  
spring.profiles.active=prod
logging.level.com.example=INFO
logging.level.org.springframework=WARN
```

## Structured Logging (JSON Format)

### JSON formatı için dependency ekleyin:
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### logback-spring.xml'e JSON encoder ekleyin:
```xml
<appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.json</file>
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
        <providers>
            <timestamp/>
            <logLevel/>
            <loggerName/>
            <message/>
            <stackTrace/>
            <mdc/>
        </providers>
    </encoder>
</appender>
```

## MDC (Mapped Diagnostic Context) Kullanımı

```java
import org.slf4j.MDC;

@Service
public class DeviceService {
    
    public Device createDevice(Device device) {
        // Context bilgisi ekle
        MDC.put("operation", "create_device");
        MDC.put("deviceName", device.getName());
        MDC.put("userId", getCurrentUserId());
        
        try {
            logger.info("Starting device creation");
            // İş mantığı
            logger.info("Device creation completed");
            return result;
        } finally {
            // Context'i temizle
            MDC.clear();
        }
    }
}
```

## Performance Monitoring

AOP ile otomatik performance logging:

```java
@Around("@annotation(Monitored)")
public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    
    try {
        Object result = joinPoint.proceed();
        stopWatch.stop();
        
        long executionTime = stopWatch.getTotalTimeMillis();
        if (executionTime > 1000) {
            logger.warn("SLOW OPERATION: {} took {} ms", 
                       joinPoint.getSignature().getName(), executionTime);
        }
        
        return result;
    } catch (Exception e) {
        logger.error("Operation failed: {}", joinPoint.getSignature().getName(), e);
        throw e;
    }
}
```

## Log Monitoring ve Alerts

### ELK Stack entegrasyonu için:
```properties
# Logstash için TCP appender
logging.config=classpath:logback-spring.xml
```

### Metrics ile entegrasyon:
```java
@Component
public class LogMetrics {
    private final MeterRegistry meterRegistry;
    
    @EventListener
    public void handleLogEvent(LoggingEvent event) {
        if (event.getLevel().equals(Level.ERROR)) {
            meterRegistry.counter("log.error", "logger", event.getLoggerName()).increment();
        }
    }
}
```

## En İyi Pratikler

1. **Logger instance'ı static final yapın**
2. **Parameterized messages kullanın** (`{}` ile)
3. **Exception'ları son parametre olarak geçin**
4. **Sensitive bilgileri logllamayın** (şifre, kart no, vs.)
5. **Production'da TRACE/DEBUG kapalı tutun**
6. **Log dosyalarını rotate edin** (boyut/zaman bazlı)
7. **Structured logging kullanın** (JSON format)
8. **MDC ile context bilgisi ekleyin**

## Test Etme

```bash
# Uygulamayı başlatın
mvn spring-boot:run

# Log dosyalarını kontrol edin
tail -f logs/application.log
tail -f logs/error.log

# Log seviyesini runtime'da değiştirmek için actuator endpoint'i kullanın
curl -X POST http://localhost:8080/actuator/loggers/com.example.devicelanguagemodelsystem \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "TRACE"}'
```