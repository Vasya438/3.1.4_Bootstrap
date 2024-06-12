package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

//@Controller
//@RequestMapping("/")
//public class AdminController {
//    private final UserService userService;
//    private final RoleService roleService;
//
//    @Autowired
//    public AdminController(UserService userService, RoleService roleService) {
//        this.userService = userService;
//        this.roleService = roleService;
//    }
//
//    @GetMapping("/admin")
//    public String showUsersList(Model model) {
//        List<User> allUsersList = userService.getAllUsersList();
//        model.addAttribute("userList", allUsersList);
//        return "admin-page";
//    }
//
//    @GetMapping("/new")
//    public String addUser(@ModelAttribute("new_user") User user, Model model) {
//        model.addAttribute("roles", roleService.getListRoles());
//        return "addUser";
//    }
//
//    @PostMapping("/new")
//    public String createUser(@ModelAttribute("new_user") User user) {
//        userService.addUser(user);
//        return "redirect:/admin/users";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String editUser(Model model, @PathVariable("id") Long id) {
//        model.addAttribute("user", userService.getUserById(id));
//        model.addAttribute("roles", roleService.getListRoles());
//        return "/editUser";
//    }
//
//    @PatchMapping("/edit/{id}")
//    public String updateUser(@ModelAttribute("user") User user, @PathVariable("id") Long id) {
//        userService.updateUser(id, user);
//        return "redirect:/admin/users";
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteUser(@PathVariable("id") Long id) {
//        userService.deleteUser(id);
//        return "redirect:/admin/users";
//    }
//}

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(RoleService roleService, UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

        if (user.getPassword().isEmpty() || user.getPassword() == null) {
            User existingUser = userService.getUserById(user.getId());
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.addUser(user);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id) {
//        User user = userService.getUserById(id);
        userService.deleteUser(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/users");
        return modelAndView;
    }
}