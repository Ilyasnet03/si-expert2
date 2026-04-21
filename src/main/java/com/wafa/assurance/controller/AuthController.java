package com.wafa.assurance.controller;

import com.wafa.assurance.dto.ChangePasswordRequest;
import com.wafa.assurance.dto.CurrentUserProfileDTO;
import com.wafa.assurance.dto.LoginResponse;
import com.wafa.assurance.service.CurrentUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${app.frontend-url:http://localhost:3000}")
@RequiredArgsConstructor
public class AuthController {

    private final CurrentUserProfileService currentUserProfileService;

    @GetMapping("/me")
    public ResponseEntity<LoginResponse.UserInfo> me(@AuthenticationPrincipal Jwt jwt) {
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            null,
            firstNonBlank(jwt.getClaimAsString("email"), jwt.getClaimAsString("preferred_username"), jwt.getSubject()),
            firstNonBlank(jwt.getClaimAsString("family_name"), ""),
            firstNonBlank(jwt.getClaimAsString("given_name"), ""),
            resolveRole(jwt)
        );

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/profile")
    public ResponseEntity<CurrentUserProfileDTO> profile() {
        return ResponseEntity.ok(currentUserProfileService.getCurrentProfile());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        currentUserProfileService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body("Authentification gérée par Keycloak");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body("Création de comptes gérée par Keycloak");
    }

    private String resolveRole(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof java.util.Map<?, ?> realmAccessMap) {
            Object roles = realmAccessMap.get("roles");
            if (roles instanceof java.util.Collection<?> roleCollection) {
                if (roleCollection.stream().map(String::valueOf).anyMatch(role -> "ADMIN".equalsIgnoreCase(role))) {
                    return "ADMIN";
                }

                if (roleCollection.stream().map(String::valueOf).anyMatch(role -> "EXPERT".equalsIgnoreCase(role))) {
                    return "EXPERT";
                }
            }
        }

        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return "";
    }
}
