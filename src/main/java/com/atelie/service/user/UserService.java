package com.atelie.service.user;

import com.atelie.db.user.Role;
import com.atelie.db.user.User;
import com.atelie.db.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    public static final Sort DEFAULT_SORTING = Sort.by(
            Sort.Order.desc("username"),
            Sort.Order.desc("id")
    );
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<User> list(Pageable pageable) {
        if (pageable == null) {
            return repo.findAll(DEFAULT_SORTING);
        }
        return repo.findAllBy(pageable).toList();
    }

    @Transactional
    public void save(User user) {
        repo.saveAndFlush(user);
    }

    @Transactional
    public void delete(User user) {
        repo.delete(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return repo.findAllByRoleNotAndActiveTrue(Role.ADMIN);
    }
}