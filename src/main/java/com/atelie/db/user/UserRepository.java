package com.atelie.db.user;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Slice<User> findAllBy(Pageable pageable);

    Optional<User> findByUsername(String username);

    List<User> findAllByRoleNotAndActiveTrue(Role role);

}