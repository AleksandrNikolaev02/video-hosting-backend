package dev.alex.auth.starter.auth_spring_boot_starter.util;

import dev.alex.auth.starter.auth_spring_boot_starter.model.UserContext;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> contextHolder = new ThreadLocal<>();

    public static void setContext(UserContext context) {
        contextHolder.set(context);
    }

    public static UserContext getContext() {
        return contextHolder.get();
    }

    public static void remove() {
        contextHolder.remove();
    }
}
