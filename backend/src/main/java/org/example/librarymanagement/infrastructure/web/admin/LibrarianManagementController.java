package org.example.librarymanagement.infrastructure.web.admin;

import java.util.List;

import org.example.librarymanagement.port.dtos.user.CreateUserCommand;
import org.example.librarymanagement.port.dtos.user.UpdateUserCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;
import org.example.librarymanagement.port.inbound.user.ManageUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/librarians")
@RequiredArgsConstructor
public class LibrarianManagementController {

    private final ManageUserUseCase manageUserUseCase;

    @PostMapping
    public ResponseEntity<UserResult> createLibrarian(@Valid @RequestBody CreateLibrarianRequest request) {
        CreateUserCommand command = new CreateUserCommand(
                request.username(),
                request.password(),
                request.fullName(),
                request.email(),
                request.phone(),
                "LIBRARIAN" // Gán cứng role là thủ thư
        );
        UserResult response = manageUserUseCase.createUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResult> getLibrarian(@PathVariable Long id) {
        UserResult response = manageUserUseCase.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResult>> getAllLibrarians() {
        List<UserResult> responses = manageUserUseCase.getAllUsersByRole("LIBRARIAN");
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResult> updateLibrarian(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLibrarianRequest request) {

        UpdateUserCommand command = new UpdateUserCommand(
                id,
                request.fullName(),
                request.email(),
                request.phone(),
                request.enabled()
        );
        UserResult response = manageUserUseCase.updateUser(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibrarian(@PathVariable Long id) {
        manageUserUseCase.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
