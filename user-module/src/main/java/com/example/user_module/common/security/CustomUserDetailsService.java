package com.example.user_module.common.security;

import com.example.user_module.auth.entity.UserEntity;
import com.example.user_module.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String idStr) throws UsernameNotFoundException {
        Long id = Long.parseLong(idStr);

        UserEntity user = authRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return new CustomUserDetails(user);
    }


}