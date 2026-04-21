package com.wafa.assurance.service;

import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    @Transactional
    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur authentifié introuvable.");
        }

        Jwt jwt = extractJwt(authentication);
        String authenticationName = normalize(authentication.getName());

        if (jwt != null) {
            User user = findFromJwt(authenticationName, jwt)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Aucun utilisateur local n'est associé à votre compte Keycloak."
                ));
            return synchronizeWithJwt(user, jwt);
        }

        return resolveByEmail(authenticationName)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Aucun utilisateur local n'est associé au compte authentifié."
            ));
    }

    private Optional<User> findFromJwt(String authenticationName, Jwt jwt) {
        List<String> emails = collectEmailCandidates(authenticationName, jwt);
        List<String> usernames = collectUsernameCandidates(authenticationName, jwt);
        String keycloakUserId = normalize(jwt.getSubject());

        if (StringUtils.hasText(keycloakUserId)) {
            Optional<User> byKeycloakId = userRepository.findByKeycloakUserId(keycloakUserId);
            if (byKeycloakId.isPresent()) {
                return byKeycloakId;
            }
        }

        for (String email : emails) {
            Optional<User> byEmail = resolveByEmail(email);
            if (byEmail.isPresent()) {
                return byEmail;
            }
        }

        for (String username : usernames) {
            Optional<User> byUsername = resolveByUsername(username);
            if (byUsername.isPresent()) {
                return byUsername;
            }
        }

        return createFromJwt(jwt, emails, usernames);
    }

    private Optional<User> resolveByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(email);
    }

    private Optional<User> resolveByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        return userRepository.findAll().stream()
            .filter(user -> StringUtils.hasText(user.getEmail()))
            .filter(user -> normalizedUsername.equals(extractEmailLocalPart(user.getEmail())))
            .findFirst();
    }

    private Optional<User> createFromJwt(Jwt jwt, List<String> emails, List<String> usernames) {
        String email = emails.stream().findFirst().orElseGet(() -> synthesizeEmail(usernames, jwt));
        if (!StringUtils.hasText(email)) {
            return Optional.empty();
        }

        User user = new User();
        user.setEmail(email);
        user.setKeycloakUserId(normalize(jwt.getSubject()));
        user.setPrenom(firstNonBlank(normalize(jwt.getClaimAsString("given_name")), firstUsername(usernames), "Utilisateur"));
        user.setNom(firstNonBlank(normalize(jwt.getClaimAsString("family_name")), "Keycloak"));
        user.setRole(firstNonBlank(resolveRole(jwt), "EXPERT"));
        user.setActif(true);
        user.setStatutCompte("ACTIF");
        user.setPassword("KEYCLOAK_MANAGED");
        return Optional.of(userRepository.save(user));
    }

    private User synchronizeWithJwt(User user, Jwt jwt) {
        boolean changed = false;

        String subject = normalize(jwt.getSubject());
        if (StringUtils.hasText(subject) && !subject.equals(user.getKeycloakUserId())) {
            user.setKeycloakUserId(subject);
            changed = true;
        }

        String email = collectEmailCandidates(null, jwt).stream().findFirst().orElse(null);
        if (StringUtils.hasText(email) && !email.equalsIgnoreCase(user.getEmail())) {
            user.setEmail(email);
            changed = true;
        }

        String firstName = normalize(jwt.getClaimAsString("given_name"));
        if (StringUtils.hasText(firstName) && !firstName.equals(user.getPrenom())) {
            user.setPrenom(firstName);
            changed = true;
        }

        String lastName = normalize(jwt.getClaimAsString("family_name"));
        if (StringUtils.hasText(lastName) && !lastName.equals(user.getNom())) {
            user.setNom(lastName);
            changed = true;
        }

        String role = resolveRole(jwt);
        if (StringUtils.hasText(role) && !role.equalsIgnoreCase(user.getRole())) {
            user.setRole(role);
            changed = true;
        }

        if (!Boolean.TRUE.equals(user.getActif())) {
            user.setActif(true);
            if (!StringUtils.hasText(user.getStatutCompte()) || "INACTIF".equalsIgnoreCase(user.getStatutCompte())) {
                user.setStatutCompte("ACTIF");
            }
            changed = true;
        }

        return changed ? userRepository.save(user) : user;
    }

    private Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getToken();
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    private List<String> collectEmailCandidates(String authenticationName, Jwt jwt) {
        Set<String> values = new LinkedHashSet<>();
        addIfPresent(values, jwt.getClaimAsString("email"));
        addIfPresent(values, authenticationName);

        String preferredUsername = normalize(jwt.getClaimAsString("preferred_username"));
        if (preferredUsername != null && preferredUsername.contains("@")) {
            values.add(preferredUsername);
        }

        String subject = normalize(jwt.getSubject());
        if (subject != null && subject.contains("@")) {
            values.add(subject);
        }

        return new ArrayList<>(values);
    }

    private List<String> collectUsernameCandidates(String authenticationName, Jwt jwt) {
        Set<String> values = new LinkedHashSet<>();
        addIfPresent(values, jwt.getClaimAsString("preferred_username"));
        addIfPresent(values, authenticationName);
        return values.stream()
            .map(value -> value.toLowerCase(Locale.ROOT))
            .toList();
    }

    private void addIfPresent(Collection<String> values, String value) {
        String normalized = normalize(value);
        if (normalized != null) {
            values.add(normalized);
        }
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String resolveRole(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();
        addRoles(roles, jwt.getClaim("realm_access"));
        Object resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof Map<?, ?> resourceAccessMap) {
            resourceAccessMap.values().forEach(value -> addRoles(roles, value));
        }

        if (roles.stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role))) {
            return "ADMIN";
        }
        if (roles.stream().anyMatch(role -> "EXPERT".equalsIgnoreCase(role))) {
            return "EXPERT";
        }
        return roles.stream().findFirst().map(role -> role.toUpperCase(Locale.ROOT)).orElse(null);
    }

    private String synthesizeEmail(List<String> usernames, Jwt jwt) {
        String username = firstUsername(usernames);
        if (StringUtils.hasText(username)) {
            return username + "@keycloak.local";
        }

        String subject = normalize(jwt.getSubject());
        if (StringUtils.hasText(subject)) {
            return subject.toLowerCase(Locale.ROOT) + "@keycloak.local";
        }

        return null;
    }

    private String firstUsername(List<String> usernames) {
        return usernames.stream().findFirst().orElse(null);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String extractEmailLocalPart(String email) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        if (!StringUtils.hasText(normalizedEmail)) {
            return null;
        }
        int atIndex = normalizedEmail.indexOf('@');
        return atIndex > 0 ? normalizedEmail.substring(0, atIndex) : normalizedEmail;
    }

    private void addRoles(Set<String> roles, Object accessClaim) {
        if (!(accessClaim instanceof Map<?, ?> accessMap)) {
            return;
        }
        Object claimRoles = accessMap.get("roles");
        if (claimRoles instanceof Collection<?> roleCollection) {
            roleCollection.stream()
                .map(String::valueOf)
                .filter(StringUtils::hasText)
                .forEach(roles::add);
        }
    }
}