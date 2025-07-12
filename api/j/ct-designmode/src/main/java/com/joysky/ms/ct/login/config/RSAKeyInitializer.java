package com.joysky.ms.ct.login.config;

import com.joysky.ms.ct.login.util.RSAKeyGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * RSA密钥初始化器
 * 在应用启动时检查RSA密钥文件是否存在，如果不存在则生成
 */
@Component
public class RSAKeyInitializer implements CommandLineRunner {

    private static final String PUBLIC_KEY_PATH = "keys/rsa_public.key";
    private static final String PRIVATE_KEY_PATH = "keys/rsa_private.key";

    @Override
    public void run(String... args) throws Exception {
        // 检查密钥文件是否存在
        boolean publicKeyExists = checkResourceExists(PUBLIC_KEY_PATH);
        boolean privateKeyExists = checkResourceExists(PRIVATE_KEY_PATH);

        // 如果任一密钥文件不存在，则生成新的密钥对
        if (!publicKeyExists || !privateKeyExists) {
            System.out.println("RSA密钥文件不存在，正在生成新的密钥对...");
            String basePath = "src/main/resources/";
            RSAKeyGenerator.generateAndSaveRSAKeyPair(
                    basePath + PUBLIC_KEY_PATH,
                    basePath + PRIVATE_KEY_PATH,
                    2048
            );
            System.out.println("RSA密钥对生成完成");
        } else {
            System.out.println("RSA密钥文件已存在，无需重新生成");
        }
    }

    /**
     * 检查资源文件是否存在
     *
     * @param path 资源路径
     * @return 是否存在
     */
    private boolean checkResourceExists(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }
}