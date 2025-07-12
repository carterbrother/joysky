package com.joysky.ms.ct.login.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.joysky.ms.ct.login.config.MailConfig;
import com.joysky.ms.ct.login.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    
    private final MailConfig mailConfig;
    
    // 邮件发送限速缓存，每个邮箱5秒内只能发送一次
    private final Cache<String, Long> rateLimitCache;
    
    public MailServiceImpl(MailConfig mailConfig) {
        this.mailConfig = mailConfig;
        this.rateLimitCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .maximumSize(1000)
                .build();
    }
    
    @Override
    public boolean sendPasswordResetCode(String toEmail, String code) {
        try {
            // 检查发送频率限制
            if (isRateLimited(toEmail)) {
                log.warn("[邮件服务] 发送频率受限, 邮箱: {}, 请等待5秒后重试", toEmail);
                return false;
            }
            
            // 创建邮件账户配置
            MailAccount account = createMailAccount();
            
            // 构建邮件内容
            String htmlContent = buildPasswordResetEmailContent(code);
            
            // 设置JavaMail系统属性以确保正确的字符编码
            System.setProperty("mail.mime.charset", "UTF-8");
            System.setProperty("file.encoding", "UTF-8");
            System.setProperty("mail.mime.splitlongparameters", "false");
            
            // 发送邮件，直接使用原始标题，让JavaMail处理编码
            Mail.create(account)
                .setTos(toEmail)
                .setTitle(mailConfig.getSubject().getPassword().getReset())
                .setContent(htmlContent)
                .setHtml(true)
                .setCharset(StandardCharsets.UTF_8) // 设置字符编码为UTF-8
                .send();
            
            // 记录发送时间
            recordSendTime(toEmail);
            
            log.info("[邮件服务] 密码重置验证码已发送至: {}", toEmail);
            return true;
            
        } catch (Exception e) {
            log.error("[邮件服务] 发送密码重置验证码失败, 收件人: {}, 错误: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String generateCode() {
        return RandomUtil.randomNumbers(mailConfig.getCode().getLength());
    }
    
    /**
     * 检查邮件发送频率限制
     * @param email 邮箱地址
     * @return true表示受限，false表示可以发送
     */
    private boolean isRateLimited(String email) {
        return rateLimitCache.getIfPresent(email) != null;
    }
    
    /**
     * 记录邮件发送时间
     * @param email 邮箱地址
     */
    private void recordSendTime(String email) {
        rateLimitCache.put(email, System.currentTimeMillis());
    }
    
    /**
     * 创建邮件账户配置
     */
    private MailAccount createMailAccount() {
        MailAccount account = new MailAccount();
        account.setHost(mailConfig.getSmtp().getHost());
        account.setPort(mailConfig.getSmtp().getPort());
        account.setAuth(mailConfig.getSmtp().getAuth());
        account.setUser(mailConfig.getSmtp().getUsername());
        account.setPass(mailConfig.getSmtp().getPassword());
        account.setFrom(mailConfig.getFrom().getAddress());
        
        // STARTTLS配置
        account.setStarttlsEnable(mailConfig.getSmtp().getStarttls().getEnable());
        
        // SSL配置
        if (mailConfig.getSmtp().getSsl().getEnable() != null) {
            account.setSslEnable(mailConfig.getSmtp().getSsl().getEnable());
        } else {
            account.setSslEnable(true); // 默认使用SSL
        }
        
        // SSL协议配置
        if (mailConfig.getSmtp().getSsl().getProtocols() != null) {
            account.setSslProtocols(mailConfig.getSmtp().getSsl().getProtocols());
        }
        
        // Socket Factory配置
        if (mailConfig.getSmtp().getSocketFactory().getPort() != null) {
            account.setSocketFactoryPort(mailConfig.getSmtp().getSocketFactory().getPort());
        }
        if (mailConfig.getSmtp().getSocketFactory().getClazz() != null) {
            account.setSocketFactoryClass(mailConfig.getSmtp().getSocketFactory().getClazz());
        }
        if (mailConfig.getSmtp().getSocketFactory().getFallback() != null) {
            account.setSocketFactoryFallback(mailConfig.getSmtp().getSocketFactory().getFallback());
        }
        
        // 超时配置
        if (mailConfig.getSmtp().getTimeout() != null) {
            account.setTimeout(mailConfig.getSmtp().getTimeout());
        }
        if (mailConfig.getSmtp().getConnectiontimeout() != null) {
            account.setConnectionTimeout(mailConfig.getSmtp().getConnectiontimeout());
        }
        
        // 设置字符编码为UTF-8，解决中文乱码问题
        account.setCharset(StandardCharsets.UTF_8);
        
        // 设置MIME字符集
        if (mailConfig.getSmtp().getMime().getCharset() != null) {
            // 通过系统属性设置JavaMail的默认字符集
            System.setProperty("mail.mime.charset", mailConfig.getSmtp().getMime().getCharset());
        }
        
        // 设置传输协议
        if (mailConfig.getSmtp().getTransportProtocol() != null) {
            System.setProperty("mail.transport.protocol", mailConfig.getSmtp().getTransportProtocol());
        }
        
        // 设置调试模式（可选，用于排查问题）
        account.setDebug(false);
        
        return account;
    }
    
    /**
     * 构建密码重置邮件HTML内容
     * 参考登录页面设计风格
     */
    private String buildPasswordResetEmailContent(String code) {
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html lang='zh-CN'>\n" +
            "<head>\n" +
            "    <meta charset='UTF-8'>\n" +
            "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
            "    <title>密码重置验证码</title>\n" +
            "    <style>\n" +
            "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
            "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f8f9fa; padding: 20px; }\n" +
            "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 20px; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1); overflow: hidden; }\n" +
            "        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 40px 30px; text-align: center; }\n" +
            "        .header h1 { font-size: 28px; margin-bottom: 10px; font-weight: 300; }\n" +
            "        .header p { font-size: 16px; opacity: 0.9; }\n" +
            "        .content { padding: 40px 30px; }\n" +
            "        .code-section { text-align: center; margin: 30px 0; }\n" +
            "        .code-label { font-size: 16px; color: #555; margin-bottom: 15px; }\n" +
            "        .code-box { display: inline-block; background: linear-gradient(45deg, #ff6b6b, #ffa500); color: white; padding: 20px 40px; border-radius: 15px; font-size: 32px; font-weight: bold; letter-spacing: 8px; margin: 10px 0; box-shadow: 0 10px 20px rgba(255, 107, 107, 0.3); }\n" +
            "        .info-section { background: #f8f9fa; border-radius: 15px; padding: 25px; margin: 25px 0; }\n" +
            "        .info-title { font-size: 18px; color: #333; margin-bottom: 15px; font-weight: 600; }\n" +
            "        .info-list { list-style: none; }\n" +
            "        .info-list li { color: #666; margin-bottom: 8px; padding-left: 20px; position: relative; }\n" +
            "        .info-list li:before { content: '•'; color: #667eea; font-weight: bold; position: absolute; left: 0; }\n" +
            "        .warning { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 10px; padding: 20px; margin: 20px 0; }\n" +
            "        .warning-title { color: #856404; font-weight: 600; margin-bottom: 10px; }\n" +
            "        .warning-text { color: #856404; font-size: 14px; }\n" +
            "        .footer { background: #f8f9fa; padding: 30px; text-align: center; border-top: 1px solid #e9ecef; }\n" +
            "        .footer p { color: #666; font-size: 14px; line-height: 1.6; }\n" +
            "        .footer a { color: #667eea; text-decoration: none; }\n" +
            "        .footer a:hover { text-decoration: underline; }\n" +
            "        @media (max-width: 600px) {\n" +
            "            .container { margin: 10px; border-radius: 15px; }\n" +
            "            .header { padding: 30px 20px; }\n" +
            "            .content { padding: 30px 20px; }\n" +
            "            .code-box { font-size: 24px; padding: 15px 30px; letter-spacing: 4px; }\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class='container'>\n" +
            "        <div class='header'>\n" +
            "            <h1>🔐 密码重置</h1>\n" +
            "            <p>您的密码重置验证码已生成</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class='content'>\n" +
            "            <div class='code-section'>\n" +
            "                <div class='code-label'>您的验证码是：</div>\n" +
            "                <div class='code-box'>%s</div>\n" +
            "            </div>\n" +
            "            \n" +
            "            <div class='info-section'>\n" +
            "                <div class='info-title'>📋 使用说明</div>\n" +
            "                <ul class='info-list'>\n" +
            "                    <li>请在密码重置页面输入此验证码</li>\n" +
            "                    <li>验证码有效期为 %d 分钟</li>\n" +
            "                    <li>验证码仅可使用一次</li>\n" +
            "                    <li>请勿将验证码告知他人</li>\n" +
            "                </ul>\n" +
            "            </div>\n" +
            "            \n" +
            "            <div class='warning'>\n" +
            "                <div class='warning-title'>⚠️ 安全提醒</div>\n" +
            "                <div class='warning-text'>\n" +
            "                    如果您没有申请密码重置，请忽略此邮件。为了您的账户安全，请定期更换密码，并使用强密码。\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div class='footer'>\n" +
            "            <p>\n" +
            "                此邮件由系统自动发送，请勿回复。<br>\n" +
            "                如有疑问，请联系 <a href='mailto:support@example.com'>技术支持</a>\n" +
            "            </p>\n" +
            "            <p style='margin-top: 15px; color: #999; font-size: 12px;'>\n" +
            "                © 2024 %s. 保留所有权利。\n" +
            "            </p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            code,
            mailConfig.getCode().getExpire().getMinutes(),
            mailConfig.getFrom().getName()
        );
    }
}