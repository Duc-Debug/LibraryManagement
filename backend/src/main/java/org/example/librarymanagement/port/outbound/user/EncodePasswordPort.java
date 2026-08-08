package org.example.librarymanagement.port.outbound.user;

public interface EncodePasswordPort {

    String encode(String rawPassword);
}
