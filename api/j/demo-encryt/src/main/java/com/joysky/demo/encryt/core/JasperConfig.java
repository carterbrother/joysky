package com.joysky.demo.encryt.core;

import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMStringEncryptor;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasperConfig {
    public static final Logger log = org.slf4j.LoggerFactory.getLogger(JasperConfig.class);

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        SimpleGCMConfig config = new SimpleGCMConfig();
        //设置加密密钥
        config.setSecretKeyPassword("xxxxx9578");
        config.setSecretKeyIterations(1000);
        //标准的base64编码,否则会报错,可以在线生成一个字符串的在线编码
        config.setSecretKeySalt("TG92ZV9DYXJ0ZXJfMjAyNQ==");
        config.setSecretKeyAlgorithm("PBKDF2WithHmacSHA256");
        log.info("安装属性配置文件密码增强成功！");
        return new SimpleGCMStringEncryptor(config);
    }


}
