-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（非对称加密）',
    phone VARCHAR(11) NOT NULL COMMENT '手机号（明文，用于唯一性校验）',
    email VARCHAR(100) NOT NULL COMMENT '邮箱（明文，用于唯一性校验）',
    phone_encrypted VARCHAR(500) NOT NULL COMMENT '手机号（RSA加密）',
    email_encrypted VARCHAR(500) NOT NULL COMMENT '邮箱（RSA加密）',
    phone_masked VARCHAR(20) NOT NULL COMMENT '手机号（脱敏）',
    email_masked VARCHAR(100) NOT NULL COMMENT '邮箱（脱敏）',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';