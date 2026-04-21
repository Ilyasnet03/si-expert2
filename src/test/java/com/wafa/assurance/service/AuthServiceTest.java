package com.wafa.assurance.service;

import com.wafa.assurance.dto.LoginRequest;
import com.wafa.assurance.dto.LoginResponse;
import com.wafa.assurance.dto.RegisterRequest;
import com.wafa.assurance.model.User;
import com.wafa.assurance.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsTokenAndUserInfoWhenCredentialsAreValid() {
        User user = new User();
        user.setId(5L);
        user.setEmail("expert@wafa.ma");
        user.setPassword("encoded-password");
        user.setNom("Doe");
        user.setPrenom("Jane");
        user.setRole("EXPERT");

        when(userRepository.findByEmail("expert@wafa.ma")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken("expert@wafa.ma", 5L, "EXPERT")).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("expert@wafa.ma", "secret"));

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getEmail()).isEqualTo("expert@wafa.ma");
        assertThat(response.getUser().getRole()).isEqualTo("EXPERT");
    }

    @Test
    void login_rejectsUnknownEmail() {
        when(userRepository.findByEmail("missing@wafa.ma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("missing@wafa.ma", "secret")))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_rejectsWrongPassword() {
        User user = new User();
        user.setEmail("expert@wafa.ma");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("expert@wafa.ma")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("expert@wafa.ma", "wrong")))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void register_savesEncodedPasswordAndIdentityFields() {
        RegisterRequest request = new RegisterRequest("new@wafa.ma", "plain-password", "Doe", "John");

        when(userRepository.existsByEmail("new@wafa.ma")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("new@wafa.ma");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getNom()).isEqualTo("Doe");
        assertThat(savedUser.getPrenom()).isEqualTo("John");
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("existing@wafa.ma")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("existing@wafa.ma", "secret", "Dup", "User")))
            .isInstanceOf(ResponseStatusException.class)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}