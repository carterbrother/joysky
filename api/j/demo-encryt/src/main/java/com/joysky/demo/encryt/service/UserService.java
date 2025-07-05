package com.joysky.demo.encryt.service;


import com.joysky.demo.encryt.core.PageR;
import com.joysky.demo.encryt.entity.User;
import com.joysky.demo.encryt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }



    public User updateUser(User user) {
        return userRepository.save(user);
    }


    public PageR<User> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return new PageR<>(userPage.getTotalElements(), userPage.getContent());
    }


    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}