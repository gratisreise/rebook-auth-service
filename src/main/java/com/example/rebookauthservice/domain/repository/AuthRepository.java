package com.example.rebookauthservice.domain.repository;

import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<AuthUser> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<AuthUser> findByUserId(String userId);
}
