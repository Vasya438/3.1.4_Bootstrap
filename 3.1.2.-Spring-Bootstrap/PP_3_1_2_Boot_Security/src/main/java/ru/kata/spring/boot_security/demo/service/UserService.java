package ru.kata.spring.boot_security.demo.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    List<User> getAllUsersList();

    User getUserById(Long id);

    void addUser(User user);

    void updateUser(Long id, User user);

    void deleteUser(Long id);

    Optional<User> findByUserName(String username);
}
