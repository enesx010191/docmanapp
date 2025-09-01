package com.moneymate.documentationManagement.entities.concretes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; 

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_ip")
    private String createdIp;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "status")
    private Byte status;

    @Column(name = "login_attempt_count")
    private Byte loginAttemptCount = 0;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    @Column(name = "is_multiple_session")
    private Boolean isMultipleSession = false;

    @Column(name = "first_login_status")
    private Boolean firstLoginStatus = true;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_create_at")
    private String passwordCreateAt;

    @Column(name = "password_update_at")
    private String passwordUpdateAt;

    @PrePersist
	public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updateAt == null) {
            updateAt = LocalDateTime.now();
        }
    }

    @PreUpdate
	public void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
