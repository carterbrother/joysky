package com.joysky.ice.easyhttp.app.client;

import com.github.vizaizai.EasyHttp;
import com.joysky.ice.easyhttp.app.config.EasyHttpProperty;
import com.joysky.ice.easyhttp.auth.client.BookHttpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookClientConfig {

    private final EasyHttpProperty easyHttpProperty;
    
    public BookClientConfig(EasyHttpProperty easyHttpProperty) {
        this.easyHttpProperty = easyHttpProperty;
    }

    @Bean
    public BookHttpService bookHttpService() {
        return EasyHttp.builder().url(easyHttpProperty.getAuthClientUrl()).build(BookHttpService.class);
    }

}
