package com.javaee.test1.models;

import java.sql.Timestamp;
import java.util.UUID;

public class User {
    private UUID userID;
    private String email;
    private String passwordHash;
    private String username;
    private String avatar;
    private boolean registeredPlan;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;

    public User(String username, UUID userID, String email, String passwordHash, String avatar, boolean registeredPlan, Timestamp createdAt, Timestamp lastLogin, boolean isActive) {
        this.username = username;
        this.userID = userID;
        this.email = email;
        this.passwordHash = passwordHash;
        this.avatar = avatar;
        this.registeredPlan = registeredPlan;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isRegisteredPlan() {
        return registeredPlan;
    }

    public void setRegisteredPlan(boolean registeredPlan) {
        this.registeredPlan = registeredPlan;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
