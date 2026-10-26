package com.zjgsu.ljj.controller;

import com.zjgsu.ljj.model.User;
import com.zjgsu.ljj.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> list() { return service.list(); }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody CreateUserRequest req) {
        User u = service.create(new User(req.userNo(), req.name(), req.phone()));
        return ResponseEntity.status(HttpStatus.CREATED).body(u);
    }

    public record CreateUserRequest(String userNo, String name, String phone) {}
}
