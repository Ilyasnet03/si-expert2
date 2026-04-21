package com.wafa.assurance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.keycloak.admin")
public class KeycloakAdminProperties {
    private String serverUrl = "http://localhost:8081";
    private String realm = "si-expert";
    private String adminRealm = "master";
    private String clientId = "admin-cli";
    private String clientSecret;
    private String username = "admin";
    private String password = "admin";
}