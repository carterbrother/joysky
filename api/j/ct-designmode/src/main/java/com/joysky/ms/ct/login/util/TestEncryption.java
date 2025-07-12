package com.joysky.ms.ct.login.util;

/**
 * 测试加密工具类
 */
public class TestEncryption {
    
    public static void main(String[] args) {
        try {
            // 测试加密和解密
            String originalText = "Hello, RSA!";
            System.out.println("原始文本: " + originalText);
            
            // 加密
            String encryptedText = EncryptionUtil.encryptRSA(originalText);
            System.out.println("加密后: " + encryptedText);
            
            // 解密
            String decryptedText = EncryptionUtil.decryptRSA(encryptedText);
            System.out.println("解密后: " + decryptedText);
            
            // 验证结果
            System.out.println("验证结果: " + originalText.equals(decryptedText));
            
            // 输出公钥信息
            System.out.println("公钥算法: " + EncryptionUtil.getPublicKey().getAlgorithm());
            System.out.println("公钥格式: " + EncryptionUtil.getPublicKey().getFormat());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}