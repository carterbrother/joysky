package com.joysky.ms.ct.login.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.joysky.ms.ct.login.dto.ImageCodeResponse;
import com.joysky.ms.ct.login.service.ImageCodeService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImageCodeServiceImpl implements ImageCodeService {

    // 使用Guava Cache存储验证码，30秒过期
    private final Cache<String, String> codeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    private static final int WIDTH = 90;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final Random RANDOM = new Random();

    @Override
    public ImageCodeResponse generateImageCode() {
        // 创建图像缓冲区
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // 生成随机验证码
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            String rand = String.valueOf(RANDOM.nextInt(10));
            code.append(rand);
            g.setColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
            g.drawString(rand, 20 * i + 10, 25);
        }

        // 添加干扰线
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
            g.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT),
                    RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
        }

        g.dispose();

        // 将图像转换为Base64
        String base64Image = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            base64Image = 
            //"data:image/png;base64," +
             Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }

        // 生成UUID并存储验证码
        String uuid = UUID.randomUUID().toString();
        codeCache.put(uuid, code.toString());

        // 返回响应对象
        ImageCodeResponse response = new ImageCodeResponse();
        response.setImgCode(base64Image);
        response.setImgUuid(uuid);
        return response;
    }

    @Override
    public boolean validateImageCode(String code, String uuid) {
        if (code == null || uuid == null) {
            return false;
        }

        String cachedCode = codeCache.getIfPresent(uuid);
        if (cachedCode == null) {
            return false;
        }

        // 验证后立即删除缓存
        codeCache.invalidate(uuid);
        
        return code.equalsIgnoreCase(cachedCode);
    }
}