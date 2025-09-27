package dev.alex.auth.starter.auth_spring_boot_starter.model;

public class UserContext {
    private String role;
    private Integer userId;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}