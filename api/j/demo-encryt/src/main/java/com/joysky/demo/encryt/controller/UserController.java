package com.joysky.demo.encryt.controller;

import com.joysky.demo.encryt.core.PageR;
import com.joysky.demo.encryt.core.R;
import com.joysky.demo.encryt.entity.User;
import com.joysky.demo.encryt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create") // 创建用户
    public R<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return new R<>(0, "成功", 1, savedUser);
        // 返回保存后的用户
    }

    @PostMapping("/all") // 获取所有用户（带分页）
    public R<List<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        PageR<User> userPage = userService.getAllUsers(PageRequest.of(page-1, size));
        return new R<>(0, "成功", userPage.getTotal(), userPage.getData());
    }

    @PostMapping("/getUserById") // 根据 ID 获取用户
    public R<User> getUserById(@RequestParam Long id) {
        User user = userService.getUserById(id);
        return new R<>(0, "成功", 1, user);
    }

    @PostMapping("/update") // 更新用户
    public R<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return new R<>(0, "成功", 1, updatedUser);
    }

    @PostMapping("/delete") // 删除用户
    public R<Void> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return new R<>(0, "删除成功", 0, null);
    }

    @GetMapping("/refreshCache") // 删除用户
    public R<Void> refreshCache() {
        return new R<>(0, "刷新成功", 0, null);
    }

}