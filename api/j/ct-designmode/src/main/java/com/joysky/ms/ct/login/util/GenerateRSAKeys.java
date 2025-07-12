package com.joysky.ms.ct.login.util;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 简单的RSA密钥生成工具
 */
public class GenerateRSAKeys {

    public static void main(String[] args) {
        try {
            // 创建密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            
            // 生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            // 将密钥编码为Base64字符串
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            // 创建目录
            File keysDir = new File("src/main/resources/keys");
            if (!keysDir.exists()) {
                keysDir.mkdirs();
            }
            
            // 保存公钥到文件
            File publicKeyFile = new File("src/main/resources/keys/rsa_public.key");
            try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
                fos.write(publicKeyString.getBytes());
            }
            
            // 保存私钥到文件
            File privateKeyFile = new File("src/main/resources/keys/rsa_private.key");
            try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
                fos.write(privateKeyString.getBytes());
            }
            
            System.out.println("RSA密钥对已生成并保存到文件：");
            System.out.println("公钥：" + publicKeyFile.getAbsolutePath());
            System.out.println("私钥：" + privateKeyFile.getAbsolutePath());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}