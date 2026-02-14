package com.powerme.service.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.refresh-cookie")
public class RefreshCookieProperties {

    private boolean secure;
    private String sameSite;
    private String domain;

    public boolean isSecure() { return secure; }
    public void setSecure(boolean secure) { this.secure = secure; }

    public String getSameSite() { return sameSite; }
    public void setSameSite(String sameSite) { this.sameSite = sameSite; }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
