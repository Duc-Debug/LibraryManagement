package org.example.librarymanagement.infrastructure.web.category;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import org.example.librarymanagement.port.inbound.category.CategoryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class CategorySecurityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void categoryEndpointWithoutTokenReturnsUnauthorizedJson()
            throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + port + "/api/categories"
                ))
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("\"code\":\"UNAUTHORIZED\""));
        assertTrue(response.body().contains("\"timestamp\""));
    }

    @Test
    void objectMapperSerializesCategoryResponseDateTimes() {
        CategoryResult result =
                new CategoryResult(
                        1L,
                        "Science",
                        "Science books",
                        true,
                        LocalDateTime.of(2026, 8, 5, 9, 0),
                        LocalDateTime.of(2026, 8, 5, 9, 30)
                );

        assertDoesNotThrow(
                () -> objectMapper.writeValueAsString(result)
        );
    }
}
