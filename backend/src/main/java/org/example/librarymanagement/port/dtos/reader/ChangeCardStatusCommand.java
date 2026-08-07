package org.example.librarymanagement.port.dtos.reader;


import org.example.librarymanagement.domain.enums.CardStatus;
public record ChangeCardStatusCommand(
        Long readerId,
        CardStatus newStatus
) {}