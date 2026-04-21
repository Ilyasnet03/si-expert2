package com.wafa.assurance.security;

import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service d'authentification pour le login MVC (formulaire Thymeleaf).
 * Charge l'utilisateur depuis la base et attribue le rôle ROLE_ADMIN ou ROLE_EXPERT.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        String role = user.getRole() != null ? user.getRole() : "EXPERT";

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getActif() != null ? user.getActif() : true,
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
