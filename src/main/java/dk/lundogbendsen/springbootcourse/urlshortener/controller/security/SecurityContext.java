package dk.lundogbendsen.springbootcourse.urlshortener.controller.security;


import dk.lundogbendsen.springbootcourse.urlshortener.model.User;

public class SecurityContext {
    static final ThreadLocal<User> userState = new ThreadLocal<>();
    static final ThreadLocal<String> protectTokenState = new ThreadLocal<>();

    public static void setUser(User user) {
        userState.set(user);
    }

    public static User getUser() {
        return userState.get();
    }

    public static void setProtectToken(String protectToken) {
        protectTokenState.set(protectToken);
    }

    public static String getProtectToken() {
        return protectTokenState.get();
    }
}
