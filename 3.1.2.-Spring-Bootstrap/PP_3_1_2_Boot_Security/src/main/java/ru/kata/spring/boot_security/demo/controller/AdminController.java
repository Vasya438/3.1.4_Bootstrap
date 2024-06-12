package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserServiceImpl userService;

    @Autowired
    public AdminController(RoleService roleService, UserServiceImpl userService) {
        this.roleService = roleService;
        this.userService = userService;

    }

    @GetMapping("/users")
    public ModelAndView getAllUsers(@AuthenticationPrincipal UserDetails authUser) {
        List<User> users = userService.getAllUsersList();
        List<Role> roleList = roleService.getListRoles();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("adminPage");
        modelAndView.addObject("userList", users);
        modelAndView.addObject("roleList", roleList);

        Optional<User> currentUser = userService.findByUserName(authUser.getUsername());
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView getEditUserPage(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        List<Role> roleList = roleService.getListRoles();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("adminPage");
        modelAndView.addObject("roles", roleList);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/edit")
    public ModelAndView updateUser(@ModelAttribute("user") User user, @RequestParam("role") String[] role) {
        if (role[0].equals("ROLE_NONE")) {
            User existingUser = userService.getUserById(user.getId());
            user.setRoles(existingUser.getRoles());
        } else {
            Set<Role> rolesSet = new HashSet<>();
            for (String roleName : role) {
                Role currentRole = roleService.getRoleByRoleName(roleName);
                rolesSet.add(currentRole);
            }
            user.setRoles(rolesSet);
        }
        userService.updateUser(user.getId(), user);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }

    @GetMapping("/add")
    public ModelAndView getAddUserPage() {
        List<Role> roleList = roleService.getListRoles();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("roles", roleList);
        modelAndView.setViewName("adminPage");
        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView addUser(@ModelAttribute("user") User user, @RequestParam("role") String[] role) {
        Set<Role> rolesSet = new HashSet<>();
        for (String roleName : role) {
            Role currentRole = roleService.getRoleByRoleName(roleName);
            rolesSet.add(currentRole);
        }
        user.setRoles(rolesSet);

        userService.addUser(user);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }
}