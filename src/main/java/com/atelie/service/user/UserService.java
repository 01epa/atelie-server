package com.atelie.service.user;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.db.user.UserRepository;
import com.atelie.db.user.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    public static final Sort DEFAULT_SORTING = Sort.by(
            Sort.Order.desc("username"),
            Sort.Order.desc("id")
    );
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo,
                       PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> list(Pageable pageable) {
        return repo.findAllByStatusNot(UserStatus.DELETED, pageable).toList();
    }

    @Transactional
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("{bcrypt}")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        repo.saveAndFlush(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return repo.findAllByRoleNotAndStatus(Role.ADMIN, UserStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(String username) {
        return repo.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(UUID userId) {
        return repo.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUser(String username, UUID userId) {
        return repo.findTopByUsernameAndIdNot(username, userId);
    }
}