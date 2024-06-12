package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.Optional;

@RestController
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ModelAndView allUsers(@AuthenticationPrincipal UserDetails authUser) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userPage");
        Optional<User> userCurrent = userService.findByUserName(authUser.getUsername());
        modelAndView.addObject("userCurrent", userCurrent.get());
        return modelAndView;
    }
}
