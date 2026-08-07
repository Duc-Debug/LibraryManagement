package org.example.librarymanagement.infrastructure.web.reader;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateReaderRequest(

        @NotBlank(message = "Reader name must not be blank.")
        @Size(
                max = 100,
                message = "Reader name must not exceed 100 characters."
        )
        String name,

        @NotBlank(message = "Email must not be blank.")
        @Email(message = "Email format is invalid.")
        @Size(
                max = 100,
                message = "Email must not exceed 100 characters."
        )
        String email,

        @NotBlank(message = "Phone number must not be blank.")
        @Pattern(
                regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",
                message = "Phone number format is invalid."
        )
        String phoneNumber,

        @NotBlank(message = "Address must not be blank.")
        @Size(
                max = 255,
                message = "Address must not exceed 255 characters."
        )
        String address
) {
}