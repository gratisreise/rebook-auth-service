package com.example.rebookauthservice.domain.service.reader;

import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.common.exception.AuthException;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthReader {
    private final AuthRepository authRepository;

    public AuthUser findByUsername(String username){
        return authRepository.findByUsername(username)
            .orElseThrow(() -> AuthException.unknown("사용자를 찾을 수 없습니다."));
    }

    public AuthUser findByProviderAndProviderId(Provider provider, String providerId) {
        return authRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(AuthUser::new);
    }

    public boolean existsByUsername(String username) {
        return authRepository.existsByUsername(username);
    }
}
