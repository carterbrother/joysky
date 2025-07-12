package com.joysky.ms.ct.login.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_phone", columnList = "phone"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_create_time", columnList = "createTime")
})
public class User {
  
    /** 用户id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名 */
    @Column(length = 20, nullable = false)
    private String username;

    /** 密码 */
    @Column(nullable = false)
    private String password;

    /** 手机号 */
    @Column(length = 11, nullable = false)
    private String phone;

    /** 邮箱 */
    @Column(length = 100, nullable = false)
    private String email;

    /** 加密后的手机号 */
    @Column(nullable = false,length = 2048)
    private String phoneEncrypted;

    /** 加密后的邮箱 */
    @Column(nullable = false,length = 2048)
    private String emailEncrypted;

    /** 脱敏后的手机号 */
    @Column(length = 20, nullable = false)
    private String phoneMasked;

    /** 脱敏后的邮箱 */
    @Column(length = 100, nullable = false)
    private String emailMasked;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}