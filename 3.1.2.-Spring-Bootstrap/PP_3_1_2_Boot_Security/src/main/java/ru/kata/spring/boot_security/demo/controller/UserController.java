package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.security.Principal;
import java.util.Optional;

//@Controller
//@RequestMapping("/user")
//public class UserController {
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public String viewUser(Principal principal, Model model) {
//        User user = userService.findByUserName(principal.getName()).get();
//        model.addAttribute("user", user);
//        return "user";
//    }
//}

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
        //Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userCurrent = userService.findByUserName(authUser.getUsername());
        modelAndView.addObject("userCurrent", userCurrent.get());
        return modelAndView;
    }
}
