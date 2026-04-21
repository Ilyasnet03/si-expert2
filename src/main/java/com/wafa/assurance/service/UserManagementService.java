package com.wafa.assurance.service;

import com.wafa.assurance.dto.UserAdminDTO;
import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakAdminService keycloakAdminService;
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";

    public List<UserAdminDTO> listUsers(String query, String role, String statut) {
        return userRepository.findAll().stream()
            .filter(user -> matches(user, query, role, statut))
            .map(UserAdminDTO::from)
            .toList();
    }

    public UserAdminDTO create(UserAdminDTO payload) {
        if (payload.getEmail() == null || payload.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire.");
        }
        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }

        String temporaryPassword = payload.getTemporaryPassword() == null || payload.getTemporaryPassword().isBlank()
            ? generateTemporaryPassword()
            : payload.getTemporaryPassword();

        User user = new User();
        user.setKeycloakUserId(keycloakAdminService.createUser(payload, temporaryPassword));
        apply(user, payload, temporaryPassword);
        user.setDerniereConnexion(LocalDateTime.now());
        return enrichWithTemporaryPassword(UserAdminDTO.from(userRepository.save(user)), temporaryPassword);
    }

    public UserAdminDTO update(Long id, UserAdminDTO payload) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));

        if (payload.getEmail() != null
            && !payload.getEmail().equalsIgnoreCase(user.getEmail())
            && userRepository.existsByEmail(payload.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }

        String newPassword = payload.getTemporaryPassword() != null && !payload.getTemporaryPassword().isBlank()
            ? payload.getTemporaryPassword()
            : null;

        String keycloakUserId = keycloakAdminService.updateUser(user.getKeycloakUserId(), user.getEmail(), payload, newPassword);
        user.setKeycloakUserId(keycloakUserId);
        apply(user, payload, newPassword);
        return UserAdminDTO.from(userRepository.save(user));
    }

    public UserAdminDTO toggleActive(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        boolean active = !Boolean.TRUE.equals(user.getActif());
        keycloakAdminService.setUserEnabled(user.getKeycloakUserId(), user.getEmail(), active);
        user.setActif(active);
        user.setStatutCompte(active ? "ACTIF" : "INACTIF");
        return UserAdminDTO.from(userRepository.save(user));
    }

    public UserAdminDTO resetPassword(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        String temporaryPassword = generateTemporaryPassword();
        keycloakAdminService.resetPassword(user.getKeycloakUserId(), user.getEmail(), temporaryPassword);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        return enrichWithTemporaryPassword(UserAdminDTO.from(user), temporaryPassword);
    }

    public void softDelete(Long id, String reason) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        keycloakAdminService.deleteUser(user.getKeycloakUserId(), user.getEmail());
        user.setActif(false);
        user.setStatutCompte(reason != null && !reason.isBlank() ? "DESACTIVE - " + reason : "DESACTIVE");
        user.setKeycloakUserId(null);
        userRepository.save(user);
    }

    private boolean matches(User user, String query, String role, String statut) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        boolean matchesQuery = normalizedQuery.isBlank()
            || contains(user.getNom(), normalizedQuery)
            || contains(user.getPrenom(), normalizedQuery)
            || contains(user.getEmail(), normalizedQuery)
            || contains(user.getMatriculeProfessionnel(), normalizedQuery);
        boolean matchesRole = role == null || role.isBlank() || role.equalsIgnoreCase(user.getRole());
        boolean matchesStatut = statut == null || statut.isBlank() || statut.equalsIgnoreCase(user.getStatutCompte());
        return matchesQuery && matchesRole && matchesStatut;
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private void apply(User user, UserAdminDTO payload, String temporaryPassword) {
        user.setNom(payload.getNom());
        user.setPrenom(payload.getPrenom());
        user.setEmail(payload.getEmail());
        user.setTelephone(payload.getTelephone());
        user.setMatriculeProfessionnel(payload.getMatriculeProfessionnel());
        user.setRole(payload.getRole() == null || payload.getRole().isBlank() ? "EXPERT" : payload.getRole());
        user.setActif(payload.getStatut() == null || !payload.getStatut().equalsIgnoreCase("INACTIF"));
        user.setStatutCompte(payload.getStatut() == null || payload.getStatut().isBlank() ? (Boolean.TRUE.equals(user.getActif()) ? "ACTIF" : "INACTIF") : payload.getStatut());
        user.setSpecialites(payload.getSpecialites() != null ? payload.getSpecialites() : List.of());
        user.setZonesIntervention(payload.getZonesIntervention() != null ? payload.getZonesIntervention() : List.of());
        user.setMaxMissionsSimultanees(payload.getMaxMissionsSimultanees());
        user.setNoteMinimaleRequise(payload.getNoteMinimaleRequise());
        if (user.getPassword() == null || temporaryPassword != null) {
            user.setPassword(passwordEncoder.encode(temporaryPassword != null ? temporaryPassword : generateTemporaryPassword()));
        }
    }

    private UserAdminDTO enrichWithTemporaryPassword(UserAdminDTO dto, String temporaryPassword) {
        dto.setTemporaryPassword(temporaryPassword);
        return dto;
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < 12; index++) {
            builder.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return builder.toString();
    }
}