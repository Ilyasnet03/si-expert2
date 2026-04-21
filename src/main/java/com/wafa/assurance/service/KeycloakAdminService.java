package com.wafa.assurance.service;

import com.wafa.assurance.config.KeycloakAdminProperties;
import com.wafa.assurance.dto.UserAdminDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final RestClient.Builder restClientBuilder;
    private final KeycloakAdminProperties properties;

    public String createUser(UserAdminDTO payload, String password) {
        String token = getAdminAccessToken();
        Map<String, Object> representation = buildUserRepresentation(payload, isEnabled(payload.getStatut()));
        representation.put("credentials", List.of(buildPasswordCredential(password)));

        try {
            ResponseEntity<Void> response = adminClient(token)
                .post()
                .uri("/admin/realms/{realm}/users", properties.getRealm())
                .contentType(MediaType.APPLICATION_JSON)
                .body(representation)
                .retrieve()
                .toBodilessEntity();

            String userId = extractUserId(response.getHeaders().getLocation())
                .orElseGet(() -> findUserIdByEmail(token, payload.getEmail())
                    .orElseThrow(() -> new IllegalStateException("Utilisateur créé dans Keycloak mais identifiant introuvable.")));

            syncRole(token, userId, payload.getRole());
            return userId;
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(resolveKeycloakError("création", exception), exception);
        }
    }

    public String updateUser(String existingKeycloakUserId, String currentEmail, UserAdminDTO payload, String newPassword) {
        String token = getAdminAccessToken();
        String userId = resolveUserId(token, existingKeycloakUserId, currentEmail);

        try {
            adminClient(token)
                .put()
                .uri("/admin/realms/{realm}/users/{userId}", properties.getRealm(), userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildUserRepresentation(payload, isEnabled(payload.getStatut())))
                .retrieve()
                .toBodilessEntity();

            syncRole(token, userId, payload.getRole());

            if (newPassword != null && !newPassword.isBlank()) {
                resetPasswordRequest(token, userId, newPassword);
            }

            return userId;
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(resolveKeycloakError("mise à jour", exception), exception);
        }
    }

    public void setUserEnabled(String existingKeycloakUserId, String email, boolean enabled) {
        String token = getAdminAccessToken();
        String userId = resolveUserId(token, existingKeycloakUserId, email);

        try {
            Map<String, Object> current = adminClient(token)
                .get()
                .uri("/admin/realms/{realm}/users/{userId}", properties.getRealm(), userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

            Map<String, Object> update = new HashMap<>();
            update.put("username", current != null ? current.get("username") : email);
            update.put("email", current != null ? current.get("email") : email);
            update.put("firstName", current != null ? current.get("firstName") : null);
            update.put("lastName", current != null ? current.get("lastName") : null);
            update.put("enabled", enabled);
            update.put("emailVerified", current != null && Boolean.TRUE.equals(current.get("emailVerified")));

            adminClient(token)
                .put()
                .uri("/admin/realms/{realm}/users/{userId}", properties.getRealm(), userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(update)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(resolveKeycloakError("activation", exception), exception);
        }
    }

    public void resetPassword(String existingKeycloakUserId, String email, String password) {
        String token = getAdminAccessToken();
        String userId = resolveUserId(token, existingKeycloakUserId, email);
        resetPasswordRequest(token, userId, password);
    }

    public void deleteUser(String existingKeycloakUserId, String email) {
        String token = getAdminAccessToken();
        String userId = resolveUserId(token, existingKeycloakUserId, email);

        try {
            adminClient(token)
                .delete()
                .uri("/admin/realms/{realm}/users/{userId}", properties.getRealm(), userId)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(resolveKeycloakError("suppression", exception), exception);
        }
    }

    public boolean validateUserCredentials(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("username", username);
        form.add("password", password);

        if (properties.getClientSecret() != null && !properties.getClientSecret().isBlank()) {
            form.add("client_secret", properties.getClientSecret());
        }

        try {
            Map<String, Object> response = RestClient.builder()
                .baseUrl(properties.getServerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.getRealm())
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

            return response != null && response.get("access_token") != null;
        } catch (RestClientResponseException exception) {
            return false;
        }
    }

    private void resetPasswordRequest(String token, String userId, String password) {
        try {
            adminClient(token)
                .put()
                .uri("/admin/realms/{realm}/users/{userId}/reset-password", properties.getRealm(), userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildPasswordCredential(password))
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new IllegalArgumentException(resolveKeycloakError("réinitialisation du mot de passe", exception), exception);
        }
    }

    private Map<String, Object> buildUserRepresentation(UserAdminDTO payload, boolean enabled) {
        Map<String, Object> representation = new HashMap<>();
        representation.put("username", payload.getEmail());
        representation.put("email", payload.getEmail());
        representation.put("firstName", payload.getPrenom());
        representation.put("lastName", payload.getNom());
        representation.put("enabled", enabled);
        representation.put("emailVerified", true);
        return representation;
    }

    private Map<String, Object> buildPasswordCredential(String password) {
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);
        return credential;
    }

    private boolean isEnabled(String statut) {
        return statut == null || !statut.equalsIgnoreCase("INACTIF");
    }

    private void syncRole(String token, String userId, String roleName) {
        List<Map<String, Object>> currentRoles = adminClient(token)
            .get()
            .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.getRealm(), userId)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

        List<Map<String, Object>> managedRoles = (currentRoles == null ? List.<Map<String, Object>>of() : currentRoles).stream()
            .filter(role -> {
                Object name = role.get("name");
                return name != null && ("ADMIN".equalsIgnoreCase(String.valueOf(name)) || "EXPERT".equalsIgnoreCase(String.valueOf(name)));
            })
            .toList();

        if (!managedRoles.isEmpty()) {
            adminClient(token)
                .method(HttpMethod.DELETE)
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.getRealm(), userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(managedRoles)
                .retrieve()
                .toBodilessEntity();
        }

        if (roleName != null && !roleName.isBlank()) {
            Map<String, Object> roleRepresentation = adminClient(token)
                .get()
                .uri("/admin/realms/{realm}/roles/{role}", properties.getRealm(), roleName.toUpperCase())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

            adminClient(token)
                .post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.getRealm(), userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(roleRepresentation))
                .retrieve()
                .toBodilessEntity();
        }
    }

    private String getAdminAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        boolean useClientCredentials = properties.getClientSecret() != null && !properties.getClientSecret().isBlank();

        form.add("grant_type", useClientCredentials ? "client_credentials" : "password");
        form.add("client_id", properties.getClientId());

        if (useClientCredentials) {
            form.add("client_secret", properties.getClientSecret());
        } else {
            form.add("username", properties.getUsername());
            form.add("password", properties.getPassword());
        }

        try {
            Map<String, Object> response = RestClient.builder()
                .baseUrl(properties.getServerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.getAdminRealm())
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

            Object accessToken = response != null ? response.get("access_token") : null;
            if (accessToken == null) {
                throw new IllegalStateException("Impossible d'obtenir un token d'administration Keycloak.");
            }

            return String.valueOf(accessToken);
        } catch (RestClientResponseException ex) {
            throw new IllegalStateException(
                "Authentification Keycloak échouée (realm=" + properties.getAdminRealm() + ") : " + ex.getStatusCode() + " — vérifiez les identifiants admin.", ex);
        }
    }

    private RestClient adminClient(String token) {
        return restClientBuilder
            .baseUrl(properties.getServerUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();
    }

    private String resolveUserId(String token, String existingKeycloakUserId, String email) {
        if (existingKeycloakUserId != null && !existingKeycloakUserId.isBlank()) {
            return existingKeycloakUserId;
        }

        return findUserIdByEmail(token, email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur Keycloak introuvable pour l'email " + email));
    }

    private Optional<String> findUserIdByEmail(String token, String email) {
        List<Map<String, Object>> users = adminClient(token)
            .get()
            .uri(uriBuilder -> uriBuilder
                .path("/admin/realms/{realm}/users")
                .queryParam("email", email)
                .queryParam("exact", true)
                .build(properties.getRealm()))
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

        if (users == null || users.isEmpty()) {
            return Optional.empty();
        }

        Object id = users.get(0).get("id");
        return id == null ? Optional.empty() : Optional.of(String.valueOf(id));
    }

    private Optional<String> extractUserId(URI location) {
        if (location == null) {
            return Optional.empty();
        }

        String path = location.getPath();
        if (path == null || path.isBlank()) {
            return Optional.empty();
        }

        int lastSlash = path.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == path.length() - 1) {
            return Optional.empty();
        }

        return Optional.of(path.substring(lastSlash + 1));
    }

    private String resolveKeycloakError(String action, RestClientResponseException exception) {
        if (exception.getStatusCode().value() == 409) {
            return "Conflit Keycloak lors de la " + action + " de l'utilisateur.";
        }

        return "Erreur Keycloak lors de la " + action + " de l'utilisateur: " + exception.getStatusText();
    }
}