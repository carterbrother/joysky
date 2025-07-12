package com.joysky.ms.ct.login.controller;

import com.joysky.ms.ct.login.entity.User;
import com.joysky.ms.ct.login.repository.UserRepository;
import com.joysky.ms.ct.login.common.R;
import com.joysky.ms.ct.login.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/users")
public class UserAdminController {
    @Autowired
    private UserRepository userRepository;

    // 查询所有用户
    @GetMapping
    public R<List<User>> list() {
        return R.success(userRepository.findAll());
    }

    // 根据ID查询用户
    @GetMapping("/{id}")
    public R<User> get(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return R.success(user.get());
        } else {
            throw new BusinessException("未找到该用户");
        }
    }

    // 创建新用户
    @PostMapping
    public R<User> create(@RequestBody User user) {
        user.setId(null); // 确保插入新用户
        return R.success(userRepository.save(user));
    }

    // 更新用户
    @PutMapping("/{id}")
    public R<User> update(@PathVariable Long id, @RequestBody User user) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("未找到该用户");
        }
        user.setId(id);
        return R.success(userRepository.save(user));
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("未找到该用户");
        }
        userRepository.deleteById(id);
        return R.success();
    }
}