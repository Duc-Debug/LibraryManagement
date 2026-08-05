package org.example.librarymanagement.infrastructure.persistence.reader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;
import org.springframework.stereotype.Component;

@Component
public class CardNumberGeneratorAdapter implements CardNumberGeneratorPort {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    @Override
    public String generateNextCardNumber() {

        String datePrefix = LocalDateTime.now().format(DATE_FORMATTER);
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "RD-" + datePrefix + "-" + randomSuffix;
    }
}
