package com.joysky.demo.encryt.controller;


import com.joysky.demo.encryt.core.R;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @description: 密码加密解密控制器，提供给运维加密和解密用
 * @see:com.joysky.joycode.api.common.rest.ops
 * @author:carter
 * @createTime:2022/2/11 10:54
 */
//@Api(tags = "运维安全接口")
@RestController
public class PasswordController {


    @Autowired
    private  StringEncryptor jasyptStringEncryptor;


//    @ApiOperation("加密接口")
    @GetMapping("/ops/password/encrypt")
    public R encrypt(@RequestParam("password")String password){
        return  R.ok(jasyptStringEncryptor.encrypt(password));
//        return  R.ok(password);

    }


//    @ApiOperation("解密接口")
    @GetMapping("/ops/password/decrypt")
    public R decrypt(@RequestParam("password")String password){
        return  R.ok(jasyptStringEncryptor.decrypt(password));
    }


}










