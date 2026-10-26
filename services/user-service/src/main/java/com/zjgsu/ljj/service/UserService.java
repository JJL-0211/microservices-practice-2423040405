package com.zjgsu.ljj.service;

import com.zjgsu.ljj.model.User;
import com.zjgsu.ljj.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> list() { return repo.findAll(); }

    public User get(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在 id=" + id));
    }

    public User create(User user) { return repo.save(user); }
}
