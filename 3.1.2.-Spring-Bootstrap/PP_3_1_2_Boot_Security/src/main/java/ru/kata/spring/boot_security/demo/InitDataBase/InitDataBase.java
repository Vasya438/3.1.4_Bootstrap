package ru.kata.spring.boot_security.demo.InitDataBase;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Set;

public class InitDataBase {

    private final UserService userService;

    public InitDataBase(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void initUsers() {
        initAdmin();
        initUser();
    }

    public void initAdmin() {
        Role roleAdmin = new Role("ROLE_ADMIN");
        User user = new User("Admin", "admin@mail.ru", "admin", "admin", Set.of(roleAdmin));
        userService.addUser(user);
    }

    public void initUser() {
        Role roleUser = new Role("ROLE_USER");
        User user = new User("User", "user@mail.ru", "user", "user", Set.of(roleUser));
        userService.addUser(user);
    }
}

