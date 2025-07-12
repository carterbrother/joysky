package com.joysky.ms.ct.login.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private static final String RSA_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY_PATH = "src/main/resources/keys/rsa_public.key";
    private static final String PRIVATE_KEY_PATH = "src/main/resources/keys/rsa_private.key";
    private static KeyPair keyPair;

    static {
        try {
            // 从资源文件加载RSA密钥对
            PublicKey publicKey = loadPublicKey();
            PrivateKey privateKey = loadPrivateKey();
            
            // 如果密钥文件不存在，则生成新的密钥对并保存
            if (publicKey == null || privateKey == null) {
                System.out.println("RSA密钥文件不存在，正在生成新的密钥对...");
                String basePath = "src/main/resources/";
                keyPair = RSAKeyGenerator.generateAndSaveRSAKeyPair(
                    basePath + PUBLIC_KEY_PATH, 
                    basePath + PRIVATE_KEY_PATH, 
                    2048
                );
            } else {
                // 使用加载的密钥创建KeyPair对象
                keyPair = new KeyPair(publicKey, privateKey);
                System.out.println("已从文件加载RSA密钥对");
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化RSA密钥对失败", e);
        }
    }
    
    /**
     * 从资源文件加载公钥
     */
    private static PublicKey loadPublicKey() {
        try {
            String publicKeyContent = readKeyFile(PUBLIC_KEY_PATH);
            if (publicKeyContent == null) return null;
            
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            System.err.println("加载公钥失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 从资源文件加载私钥
     */
    private static PrivateKey loadPrivateKey() {
        try {
            String privateKeyContent = readKeyFile(PRIVATE_KEY_PATH);
            if (privateKeyContent == null) return null;
            
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            System.err.println("加载私钥失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 读取密钥文件内容
     */
    private static String readKeyFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("读取密钥文件失败: " + e.getMessage());
            return null;
        }
    }

    public static String encryptRSA(String data) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    public static String decryptRSA(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    public static PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public static PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}