package org.example.librarymanagement.infrastructure.web.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.librarymanagement.infrastructure.web.auth.dtos.UpdateProfileRequest;
import org.example.librarymanagement.infrastructure.web.reader.CreateReaderRequest;
import org.example.librarymanagement.infrastructure.web.reader.UpdateReaderRequest;
import org.example.librarymanagement.infrastructure.web.user.CreateLibrarianRequest;
import org.example.librarymanagement.infrastructure.web.user.UpdateLibrarianRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmailValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @ParameterizedTest
    @DisplayName("CreateReaderRequest từ chối email không chứa domain chuẩn hoặc có edge-cases lỗi (VD: duc@ksaf, duc..nguyen@gmail.com)")
    @ValueSource(strings = {
            "duc@ksaf", "duc@gmail", "duc@.com", "@gmail.com", "duc@gmail.", "duc@gmail.c",
            "duc..nguyen@gmail.com", ".duc@gmail.com", "duc.@gmail.com",
            "duc@-gmail.com", "duc@gmail-.com", "duc@gmail..com"
    })
    void createReaderRequest_InvalidEmail_ShouldFailValidation(String invalidEmail) {
        CreateReaderRequest request = new CreateReaderRequest(
                "Nguyen Van A",
                invalidEmail,
                "0987654321",
                "Ha Noi"
        );

        Set<ConstraintViolation<CreateReaderRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Email không hợp lệ '" + invalidEmail + "' phải bị từ chối!");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @ParameterizedTest
    @DisplayName("CreateReaderRequest chấp nhận các email chuẩn chứa domain đầy đủ (VD: duc@gmail.com, user@sub.domain.co.uk)")
    @ValueSource(strings = {"duc@gmail.com", "user.name+tag@domain.co.uk", "test_123@sub.domain.vn", "TEST@EXAMPLE.ORG"})
    void createReaderRequest_ValidEmail_ShouldPassValidation(String validEmail) {
        CreateReaderRequest request = new CreateReaderRequest(
                "Nguyen Van A",
                validEmail,
                "0987654321",
                "Ha Noi"
        );

        Set<ConstraintViolation<CreateReaderRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Email hợp lệ '" + validEmail + "' phải trôi qua validation!");
    }

    @Test
    @DisplayName("UpdateReaderRequest từ chối email không chứa dấu chấm domain (duc@ksaf)")
    void updateReaderRequest_InvalidEmail_ShouldFail() {
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Nguyen Van A",
                "duc@ksaf",
                "0987654321",
                "Ha Noi"
        );

        Set<ConstraintViolation<UpdateReaderRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("UpdateProfileRequest từ chối email duc@ksaf")
    void updateProfileRequest_InvalidEmail_ShouldFail() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "Nguyen Van B",
                "duc@ksaf",
                "0987654321"
        );

        Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email phải chứa tên miền hợp lệ")));
    }

    @Test
    @DisplayName("CreateLibrarianRequest từ chối email duc@ksaf")
    void createLibrarianRequest_InvalidEmail_ShouldFail() {
        CreateLibrarianRequest request = new CreateLibrarianRequest(
                "librarian1",
                "password123",
                "Nguyen Van C",
                "duc@ksaf",
                "0987654321"
        );

        Set<ConstraintViolation<CreateLibrarianRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email phải chứa tên miền hợp lệ")));
    }

    @Test
    @DisplayName("UpdateLibrarianRequest từ chối email duc@ksaf")
    void updateLibrarianRequest_InvalidEmail_ShouldFail() {
        UpdateLibrarianRequest request = new UpdateLibrarianRequest(
                "Nguyen Van D",
                "duc@ksaf",
                "0987654321",
                true
        );

        Set<ConstraintViolation<UpdateLibrarianRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email phải chứa tên miền hợp lệ")));
    }
}
