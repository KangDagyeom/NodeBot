package com.javaee.test1.models;

import java.sql.Timestamp;
import java.util.UUID;

public class User {
    private UUID userID;
    private String email;
    private String passwordHash;
    private String username;
    private String avatar;
    private String role;
    private String subscriptionPlan;
    private Timestamp createdAt;
    private Timestamp lastActive;
    private boolean isActive;

    public User(UUID userID, String email, String passwordHash, String username, String avatar, String role, String subscriptionPlan, Timestamp createdAt, Timestamp lastActive, boolean isActive) {
        this.userID = userID;
        this.email = email;
        this.passwordHash = passwordHash;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.subscriptionPlan = subscriptionPlan;
        this.createdAt = createdAt;
        this.lastActive = lastActive;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(Timestamp lastActive) {
        this.lastActive = lastActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
