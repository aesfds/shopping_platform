package com.example.shopping_platform.auth.repository;

import com.example.shopping_platform.auth.entity.AuthSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByTokenHashAndRevokedFalse(String tokenHash);
}
