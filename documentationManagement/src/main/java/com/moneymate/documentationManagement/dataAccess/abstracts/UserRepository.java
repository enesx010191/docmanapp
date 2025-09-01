package com.moneymate.documentationManagement.dataAccess.abstracts;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moneymate.documentationManagement.entities.concretes.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    boolean existsByEmail(String email);
    
    Optional<User> findByEmailAndPassword(String email, String password);

}
