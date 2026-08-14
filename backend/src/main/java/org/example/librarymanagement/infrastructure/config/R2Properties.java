package org.example.librarymanagement.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Cấu hình Cloudflare R2 Storage sử dụng Configuration Properties class.
 */
@ConfigurationProperties(prefix = "cloud.r2")
@Validated
@Getter
@Setter
public class R2Properties {

    @NotBlank(message = "Cloudflare R2 endpoint must not be blank")
    private String endpoint;

    @NotBlank(message = "Cloudflare R2 access-key must not be blank")
    private String accessKey;

    @NotBlank(message = "Cloudflare R2 secret-key must not be blank")
    private String secretKey;

    @NotBlank(message = "Cloudflare R2 bucket-name must not be blank")
    private String bucketName;

    @NotBlank(message = "Cloudflare R2 public-url must not be blank")
    private String publicUrl;
}
