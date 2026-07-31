package org.example.librarymanagement.infrastructure.persistence.user;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.SaveUserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements LoadUserPort,SaveUserPort {

    private final UserJpaRepository userJpaRepository;
private final UserPersistenceMapper userPersistenceMapper;
    public UserPersistenceAdapter(
            UserJpaRepository userJpaRepository,UserPersistenceMapper userPersistenceMapper
    ) {
        this.userJpaRepository = userJpaRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository
                .findByUsername(username)
                .map(UserPersistenceMapper::toDomain);
    }
    @Override
    public Optional<User> findById(Long id){
        return userJpaRepository
            .findById(id)
            .map(UserPersistenceMapper::toDomain);
    }

    @Override
    public void save(User user) {
       UserJpaEntity jpaEntity = userPersistenceMapper.toJpaEntity(user);
       userJpaRepository.save(jpaEntity);
    }
   
}