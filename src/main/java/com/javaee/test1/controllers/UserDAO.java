package com.javaee.test1.controllers;

import com.javaee.test1.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAO {
    private User mapResult(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("Username"),
                UUID.fromString(rs.getString("UserID")),
                rs.getString("Email"),
                rs.getString("PasswordHash"),
                rs.getString("Avatar"),
                rs.getBoolean("RegisterdPlan"),
                rs.getTimestamp("CreatedAt"),
                rs.getTimestamp("LastLogin"),
                rs.getBoolean("IsActive")
        );
    }
}
