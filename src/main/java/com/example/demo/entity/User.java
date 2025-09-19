package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String uname;
    private String email;
    private String password;

    public User() {}
    public User(String uname, String email, String password) {
        this.uname = uname; this.email = email; this.password = password;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}


