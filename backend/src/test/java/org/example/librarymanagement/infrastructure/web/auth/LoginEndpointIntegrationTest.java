package org.example.librarymanagement.infrastructure.web.auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

import org.example.librarymanagement.infrastructure.persistence.user.UserJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class LoginEndpointIntegrationTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void cleanDatabase() {
        userJpaRepository.deleteAll();
    }

    @Test
    void loginEndpointIsCallableAndValidatesInput() throws Exception {
        HttpResponse<String> response = postLogin("""
                {"username":"","password":""}
                """);

        assertEquals(400, response.statusCode());
        assertFalse(response.body().contains("passwordHash"));
    }

    @Test
    void loginEndpointReturnsJwtForValidCredentials() throws Exception {
        userJpaRepository.save(new UserJpaEntity(
                null,
                "alice",
                passwordEncoder.encode("secret"),
                "Alice Nguyen",
                "alice@example.com",
                null,
                true,
                null,
                null,
                null,
                Set.of()
        ));

        HttpResponse<String> response = postLogin("""
                {"username":"alice","password":"secret"}
                """);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"accessToken\""));
        assertTrue(response.body().contains("\"username\":\"alice\""));
        assertFalse(response.body().contains("passwordHash"));
    }

    @Test
    void loginEndpointReturnsUnauthorizedForUnknownUser() throws Exception {
        HttpResponse<String> response = postLogin("""
                {"username":"alice","password":"wrong"}
                """);

        assertEquals(401, response.statusCode());
        assertFalse(response.body().contains("passwordHash"));
    }

    private HttpResponse<String> postLogin(String requestBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
