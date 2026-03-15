package com.example.rebookauthservice.common.security;

import com.example.rebookauthservice.exception.CMissingDataException;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = authRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("아이디나 비밀번호가 틀립니다."));
        return new CustomUserDetails(user);
    }
}
