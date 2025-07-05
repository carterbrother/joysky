package com.joysky.demo.encryt.repository;


import com.joysky.demo.encryt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}