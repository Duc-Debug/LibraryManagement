package org.example.librarymanagement.infrastructure.security;

import org.example.librarymanagement.port.outbound.user.EncodePasswordPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncodePasswordAdapter implements EncodePasswordPort {

    // Khởi tạo trực tiếp BCryptPasswordEncoder để mã hóa mật khẩu
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
