package org.example.librarymanagement.infrastructure.security;

import org.example.librarymanagement.port.outbound.auth.TokenBlacklistPort;
import org.springframework.stereotype.Component;
@Component
public class TokenBlacklistAdapter implements TokenBlacklistPort {

    // private final RedisTemplate<String, String> redisTemplate; 
    // hoặc private final BlacklistRepository repository;

    @Override
    public void blacklistToken(String token) {
        // Code thực tế để lưu token vào DB hoặc Redis
        // Ví dụ: redisTemplate.opsForValue().set(token, "blacklisted", timeout);
    }
}