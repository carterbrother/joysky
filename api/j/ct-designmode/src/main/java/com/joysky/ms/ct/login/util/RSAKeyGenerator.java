package com.joysky.ms.ct.login.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * RSA密钥生成工具类
 * 用于生成RSA密钥对并保存到文件
 */
public class RSAKeyGenerator {
    private static final String RSA_ALGORITHM = "RSA";
    
    /**
     * 生成RSA密钥对并保存到指定目录
     * 
     * @param publicKeyPath 公钥保存路径
     * @param privateKeyPath 私钥保存路径
     * @param keySize 密钥大小（通常为2048或4096）
     * @return 生成的密钥对
     */
    public static KeyPair generateAndSaveRSAKeyPair(String publicKeyPath, String privateKeyPath, int keySize) {
        try {
            // 创建密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(keySize);
            
            // 生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            // 将密钥编码为Base64字符串
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            
            // 保存公钥到文件
            saveKeyToFile(publicKeyPath, publicKeyString);
            
            // 保存私钥到文件
            saveKeyToFile(privateKeyPath, privateKeyString);
            
            System.out.println("RSA密钥对已生成并保存到文件：\n公钥：" + publicKeyPath + "\n私钥：" + privateKeyPath);
            
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成RSA密钥对失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 将密钥保存到文件
     * 
     * @param filePath 文件路径
     * @param keyContent 密钥内容
     */
    private static void saveKeyToFile(String filePath, String keyContent) {
        File file = new File(filePath);
        
        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(keyContent.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("保存密钥到文件失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 主方法，用于手动生成密钥对
     */
    public static void main(String[] args) {
        String basePath = "src/main/resources/keys/";
        String publicKeyPath = basePath + "rsa_public.key";
        String privateKeyPath = basePath + "rsa_private.key";
        
        generateAndSaveRSAKeyPair(publicKeyPath, privateKeyPath, 2048);
    }
}