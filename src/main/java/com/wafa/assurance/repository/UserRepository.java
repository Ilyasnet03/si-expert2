package com.wafa.assurance.repository;

import com.wafa.assurance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByKeycloakUserId(String keycloakUserId);
    boolean existsByEmail(String email);
    List<User> findByRoleIgnoreCaseAndActifTrue(String role);
}
