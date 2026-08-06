package org.example.librarymanagement.infrastructure.persistence.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CardNumberGeneratorAdapterTest {

    private CardNumberGeneratorAdapter generator;

    @BeforeEach
    void setUp() {
        generator = new CardNumberGeneratorAdapter();
    }

    @Test
    @DisplayName("Mã thẻ sinh ra phải có tiền tố RD- và đủ độ dài tiêu chuẩn")
    void generateNextCardNumber_ShouldReturnValidFormat() {
        String cardNumber = generator.generateNextCardNumber();

        assertNotNull(cardNumber);
        assertTrue(cardNumber.startsWith("RD-"), "Mã thẻ phải bắt đầu bằng tiền tố RD-");
        assertTrue(cardNumber.length() >= 13, "Mã thẻ phải có độ dài tiêu chuẩn");
    }
}
