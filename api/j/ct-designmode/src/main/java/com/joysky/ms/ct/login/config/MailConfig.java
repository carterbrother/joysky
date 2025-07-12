package com.joysky.ms.ct.login.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮件配置类
 * 读取application.properties中的邮件配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "mail")
public class MailConfig {
    
    /**
     * SMTP配置
     */
    private Smtp smtp = new Smtp();
    
    /**
     * 发件人配置
     */
    private From from = new From();
    
    /**
     * 邮件主题配置
     */
    private Subject subject = new Subject();
    
    /**
     * 验证码配置
     */
    private Code code = new Code();
    
    @Data
    public static class Smtp {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private Boolean auth;
        private Starttls starttls = new Starttls();
        private Ssl ssl = new Ssl();
        private SocketFactory socketFactory = new SocketFactory();
        private Integer timeout;
        private Integer connectiontimeout;
        
        // 邮件编码配置
        private Mime mime = new Mime();
        private String transportProtocol;
        
        @Data
        public static class Starttls {
            private Boolean enable;
        }
        
        @Data
        public static class Ssl {
            private String trust;
            private Boolean enable;
            private String protocols;
        }
        
        @Data
        public static class SocketFactory {
            private Integer port;
            private String clazz; // 使用clazz避免与Java关键字class冲突
            private Boolean fallback;
        }
        
        @Data
        public static class Mime {
            private String charset;
        }
    }
    
    @Data
    public static class From {
        private String name;
        private String address;
    }
    
    @Data
    public static class Subject {
        private Password password = new Password();
        
        @Data
        public static class Password {
            private String reset;
        }
    }
    
    @Data
    public static class Code {
        private Expire expire = new Expire();
        private Integer length;
        
        @Data
        public static class Expire {
            private Integer minutes;
        }
    }
}