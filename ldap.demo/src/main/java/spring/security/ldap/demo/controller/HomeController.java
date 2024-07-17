package spring.security.ldap.demo.controller;

import org.springframework.web.bind.annotation.*;
import spring.security.ldap.demo.service.UserService;
import spring.security.ldap.demo.vo.User;

import java.util.List;

@RestController
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "Welcome to the home page!";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users")
    public void createUser(@RequestBody User user) {
        userService.createUser(user);
    }

    //Get a particular user by there uid (lookup)
    //Lookup the supplied DN and return the found object.
    @GetMapping("/users/{uid}")
    public User getUser(@PathVariable String uid) {
        return userService.getUser(uid);
    }

    @PutMapping("/users/{uid}")
    public void updateUser(@PathVariable String uid) {
        userService.updateUser(uid);
    }

    @DeleteMapping("/users/{uid}")
    public void deleteUser(@PathVariable String uid) {
        userService.deleteUser(uid);
    }

}