package com.atelie.db.user;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Slice<User> findAllBy(Pageable pageable);

    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    List<User> findAllByRoleNotAndStatus(Role role, UserStatus status);

    Optional<User> findByUsername(String username);

    Slice<User> findAllByStatusNot(UserStatus status, Pageable pageable);

    Optional<User> findTopByUsernameAndIdNot(
            String username,
            UUID id
    );
}