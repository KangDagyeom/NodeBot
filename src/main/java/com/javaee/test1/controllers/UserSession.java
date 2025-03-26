package com.javaee.test1.controllers;

public class UserSession {
    private static UserSession instance;
    private String avatar;
    private String username;
    private String subscriptionPlan;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUserInfo(String avatar, String username, String subscriptionPlan) {
        this.avatar = avatar;
        this.username = username;
        this.subscriptionPlan = subscriptionPlan;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void clearSession() {
        instance = null;
    }
}

