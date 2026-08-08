package org.example.librarymanagement.application.manage;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.application.user.UserManagementService;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.dtos.user.CreateUserCommand;
import org.example.librarymanagement.port.dtos.user.UpdateUserCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;
import org.example.librarymanagement.port.outbound.user.EncodePasswordPort;
import org.example.librarymanagement.port.outbound.user.FindUserPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.user.LoadRolePort;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class UserManagementServiceTest {


    private FindUserPort findUserPort;

    private LoadRolePort loadRolePort;

    private SaveUserPort saveUserPort;

    private EncodePasswordPort encodePasswordPort;

    private GetAuthenticatedUserPort getAuthenticatedUserPort;


    private UserManagementService userManagementService;



    @BeforeEach
    void setUp(){


        findUserPort = mock(FindUserPort.class);

        loadRolePort = mock(LoadRolePort.class);

        saveUserPort = mock(SaveUserPort.class);

        encodePasswordPort = mock(EncodePasswordPort.class);

        getAuthenticatedUserPort =
                mock(GetAuthenticatedUserPort.class);



        userManagementService =
                new UserManagementService(
                        findUserPort,
                        loadRolePort,
                        saveUserPort,
                        encodePasswordPort,
                        getAuthenticatedUserPort
                );

    }



    // =========================
    // CREATE USER
    // =========================


    @Test
    void createsUserSuccessfully(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        when(findUserPort.existsByUsername("bob"))
                .thenReturn(false);



        Role librarian =
                new Role(
                        2L,
                        "LIBRARIAN",
                        "Library Staff"
                );



        when(loadRolePort.findByName("LIBRARIAN"))
                .thenReturn(Optional.of(librarian));



        when(encodePasswordPort.encode("123456"))
                .thenReturn("encoded-password");



        User savedUser =
                new User(
                        10L,
                        "bob",
                        "encoded-password",
                        "Bob",
                        "bob@gmail.com",
                        "012345",
                        true,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Set.of(librarian)
                );



        when(saveUserPort.save(any(User.class)))
                .thenReturn(savedUser);



        UserResult result =
                userManagementService.createUser(
                        new CreateUserCommand(
                                "bob",
                                "123456",
                                "Bob",
                                "bob@gmail.com",
                                "012345",
                                "LIBRARIAN"
                        )
                );



        assertEquals(
                "bob",
                result.username()
        );


        assertTrue(
                result.roles()
                        .contains("LIBRARIAN")
        );



        verify(saveUserPort)
                .save(argThat(user ->
                        user.getUsername()
                                .equals("bob")
                        &&
                        user.hasRole("LIBRARIAN")
                ));

    }





    @Test
    void rejectsDuplicateUsername(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        when(findUserPort.existsByUsername("bob"))
                .thenReturn(true);



        assertThrows(
                DomainException.class,
                () ->
                userManagementService.createUser(
                        new CreateUserCommand(
                                "bob",
                                "123456",
                                "Bob",
                                null,
                                null,
                                "LIBRARIAN"
                        )
                )
        );



        verify(saveUserPort, never())
                .save(any());

    }





    @Test
    void rejectsUnknownRole(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        when(findUserPort.existsByUsername("bob"))
                .thenReturn(false);



        when(loadRolePort.findByName("LIBRARIAN"))
                .thenReturn(Optional.empty());



        assertThrows(
                DomainException.class,
                () ->
                userManagementService.createUser(
                        new CreateUserCommand(
                                "bob",
                                "123456",
                                "Bob",
                                null,
                                null,
                                "LIBRARIAN"
                        )
                )
        );



        verify(saveUserPort, never())
                .save(any());

    }







    // =========================
    // UPDATE USER
    // =========================



    @Test
    void updatesUserSuccessfully(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        User target =
                new User(
                        5L,
                        "bob",
                        "hash",
                        "Bob",
                        null,
                        null,
                        true,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Set.of()
                );



        when(findUserPort.findById(5L))
                .thenReturn(Optional.of(target));



        when(saveUserPort.save(any(User.class)))
                .thenReturn(target);



        UserResult result =
                userManagementService.updateUser(
                        new UpdateUserCommand(
                                5L,
                                "Bob New",
                                "bob@gmail.com",
                                "999999",
                                null
                        )
                );



        assertEquals(
                "Bob New",
                result.fullName()
        );



        verify(saveUserPort)
                .save(target);

    }





    @Test
    void updateUserFailsWhenUserNotFound(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        when(findUserPort.findById(100L))
                .thenReturn(Optional.empty());



        assertThrows(
                DomainException.class,
                () ->
                userManagementService.updateUser(
                        new UpdateUserCommand(
                                100L,
                                "ABC",
                                null,
                                null,
                                null
                        )
                )
        );



        verify(saveUserPort, never())
                .save(any());

    }







    // =========================
    // DEACTIVATE USER
    // =========================



    @Test
    void deactivatesUserSuccessfully(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        User target = normalUser();



        when(findUserPort.findById(2L))
                .thenReturn(Optional.of(target));



        userManagementService.deactivateUser(2L);



        assertFalse(
                target.isEnabled()
        );



        verify(saveUserPort)
                .save(target);

    }







    // =========================
    // GET USER
    // =========================



    @Test
    void getsUserByIdSuccessfully(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        User target =
                new User(
                        3L,
                        "alice",
                        "hash",
                        "Alice",
                        null,
                        null,
                        true,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        Set.of(
                                new Role(
                                        2L,
                                        "LIBRARIAN",
                                        "Staff"
                                )
                        )
                );



        when(findUserPort.findById(3L))
                .thenReturn(Optional.of(target));



        UserResult result =
                userManagementService.getUserById(3L);



        assertEquals(
                "alice",
                result.username()
        );


        assertTrue(
                result.roles()
                        .contains("LIBRARIAN")
        );

    }







    // =========================
    // GET ALL USERS
    // =========================



    @Test
    void getsAllUsersByRoleSuccessfully(){


        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin());



        when(findUserPort.findByRoleName("LIBRARIAN"))
                .thenReturn(
                        List.of(normalUser())
                );



        List<UserResult> result =
                userManagementService.getAllUsersByRole(
                        "LIBRARIAN"
                );



        assertEquals(
                1,
                result.size()
        );


        assertEquals(
                "user",
                result.get(0).username()
        );

    }





// =========================
// SECURITY & EDGE CASE TEST
// =========================


@Test
void librarianCannotCreateUser(){


    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(normalUser());



    assertThrows(
            DomainException.class,
            () ->
            userManagementService.createUser(
                    new CreateUserCommand(
                            "bob",
                            "123456",
                            "Bob",
                            "bob@gmail.com",
                            "012345",
                            "LIBRARIAN"
                    )
            )
    );



    verify(saveUserPort, never())
            .save(any(User.class));

}





@Test
void disabledAdminCannotManageUser(){


    User disabledAdmin =
            new User(
                    1L,
                    "admin",
                    "hash",
                    "Administrator",
                    null,
                    null,
                    false,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Set.of(
                            new Role(
                                    1L,
                                    "ADMIN",
                                    "Administrator"
                            )
                    )
            );



    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(disabledAdmin);



    assertThrows(
            DomainException.class,
            () ->
            userManagementService.getAllUsersByRole(
                    "LIBRARIAN"
            )
    );



    verify(findUserPort, never())
            .findByRoleName(any());

}



@Test
void enablesUserWhenUpdateEnabledTrue(){


    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(admin());



    User target =
            new User(
                    2L,
                    "user",
                    "hash",
                    "User",
                    "user@gmail.com",
                    "012345",
                    false,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    Set.of(
                            new Role(
                                    2L,
                                    "LIBRARIAN",
                                    "Librarian"
                            )
                    )
            );



    when(findUserPort.findById(2L))
            .thenReturn(Optional.of(target));



    when(saveUserPort.save(any(User.class)))
            .thenReturn(target);



    UserResult result =
            userManagementService.updateUser(
                    new UpdateUserCommand(
                            2L,
                            "User Updated",
                            "user@gmail.com",
                            "012345",
                            true
                    )
            );



    assertTrue(
            target.isEnabled()
    );



    assertTrue(
            result.enabled()
    );



    assertEquals(
            "User Updated",
            result.fullName()
    );



    verify(saveUserPort)
            .save(target);

}

@Test
void disablesUserWhenUpdateEnabledFalse(){


    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(admin());



    User target = normalUser();



    when(findUserPort.findById(2L))
            .thenReturn(Optional.of(target));



    when(saveUserPort.save(any(User.class)))
            .thenReturn(target);



    userManagementService.updateUser(
            new UpdateUserCommand(
                    2L,
                    "User",
                    "user@gmail.com",
                    "012345",
                    false
            )
    );



    assertFalse(
            target.isEnabled()
    );



    verify(saveUserPort)
            .save(target);

}





@Test
void getUserByIdFailsWhenUserNotFound(){


    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(admin());



    when(findUserPort.findById(99L))
            .thenReturn(Optional.empty());



    assertThrows(
            DomainException.class,
            () ->
            userManagementService.getUserById(99L)
    );



}

    // =========================
    // MOCK DATA
    // =========================



    private User admin(){


        return new User(
                1L,
                "admin",
                "hash",
                "Administrator",
                null,
                null,
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(
                        new Role(
                                1L,
                                "ADMIN",
                                "Administrator"
                        )
                )
        );

    }



    private User normalUser(){


        return new User(
                2L,
                "user",
                "hash",
                "User",
                null,
                null,
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(
                        new Role(
                                2L,
                                "LIBRARIAN",
                                "Librarian"
                        )
                )
        );

    }

}