package com.wafa.assurance.service;

import com.wafa.assurance.dto.ChangePasswordRequest;
import com.wafa.assurance.dto.CurrentUserProfileDTO;
import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CurrentUserProfileService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakAdminService keycloakAdminService;

    @Transactional(readOnly = true)
    public CurrentUserProfileDTO getCurrentProfile() {
        return CurrentUserProfileDTO.from(currentUserService.requireCurrentUser());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = currentUserService.requireCurrentUser();

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nouveau mot de passe doit être différent de l'actuel.");
        }

        boolean validCurrentPassword = keycloakAdminService.validateUserCredentials(currentUser.getEmail(), request.getCurrentPassword());
        if (!validCurrentPassword) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe actuel est incorrect.");
        }

        keycloakAdminService.resetPassword(currentUser.getKeycloakUserId(), currentUser.getEmail(), request.getNewPassword());
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
}