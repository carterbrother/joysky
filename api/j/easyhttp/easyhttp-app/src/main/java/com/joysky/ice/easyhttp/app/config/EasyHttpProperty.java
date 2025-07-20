package com.joysky.ice.easyhttp.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "easy-http")
@Component
public class EasyHttpProperty {

    /**
     * authClientUrl: auth client url
     */
    private String authClientUrl;

    public EasyHttpProperty() {
    }

    public EasyHttpProperty(String authClientUrl) {
        this.authClientUrl = authClientUrl;
    }

    public String getAuthClientUrl() {
        return authClientUrl;
    }

    public void setAuthClientUrl(String authClientUrl) {
        this.authClientUrl = authClientUrl;
    }


}
