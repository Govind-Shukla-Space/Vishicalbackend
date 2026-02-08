package com.store.vishical.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${cookie.domain:localhost}")
    private String cookieDomain;

    @Value("${cookie.secure:false}")
    private boolean secure;

    /**
     * Create an HTTP-only cookie for JWT token
     */
    public Cookie createTokenCookie(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true); // Cannot be accessed via JavaScript
        cookie.setSecure(secure); // Only sent over HTTPS in production
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000)); // Convert milliseconds to seconds
        
        // Set domain for cross-subdomain access if needed
        if (!"localhost".equals(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        // SameSite=Lax for CSRF protection while allowing top-level navigation
        cookie.setAttribute("SameSite", "Lax");
        
        return cookie;
    }

    /**
     * Create an HTTP-only cookie for shopId
     */
    public Cookie createShopIdCookie(String shopId) {
        Cookie cookie = new Cookie("shopId", shopId);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));
        
        if (!"localhost".equals(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        cookie.setAttribute("SameSite", "Lax");
        
        return cookie;
    }

    /**
     * Create a cookie to delete/clear an existing cookie
     */
    public Cookie createExpiredCookie(String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        
        if (!"localhost".equals(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        
        return cookie;
    }

    /**
     * Add token and shopId cookies to response
     */
    public void addAuthCookies(HttpServletResponse response, String token, String shopId) {
        response.addCookie(createTokenCookie(token));
        if (shopId != null && !shopId.isEmpty()) {
            response.addCookie(createShopIdCookie(shopId));
        }
    }

    /**
     * Clear authentication cookies
     */
    public void clearAuthCookies(HttpServletResponse response) {
        response.addCookie(createExpiredCookie("token"));
        response.addCookie(createExpiredCookie("shopId"));
    }
}
