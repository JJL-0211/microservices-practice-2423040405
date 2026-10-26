package com.zjgsu.ljj.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_no", nullable = false, length = 32, unique = true)
    private String userNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String phone;

    protected User() {}

    public User(String userNo, String name, String phone) {
        this.userNo = userNo;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getUserNo() { return userNo; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}
