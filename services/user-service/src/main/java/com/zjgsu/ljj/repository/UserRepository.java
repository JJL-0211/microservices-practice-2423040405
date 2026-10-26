package com.zjgsu.ljj.repository;

import com.zjgsu.ljj.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
