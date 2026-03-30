package com.example.rebookauthservice.domain.repository;

import com.example.rebookauthservice.domain.model.entity.AuthUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class AuthWriter {
    private final AuthRepository authRepository;

    public void save(AuthUser user) {
        authRepository.save(user);
    }

    public AuthUser saveOrUpdate(AuthUser authUser) {

        return null;
    }
}
