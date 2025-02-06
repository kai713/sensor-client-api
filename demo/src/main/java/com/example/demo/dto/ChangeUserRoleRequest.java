package com.example.demo.dto;

import com.example.demo.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeUserRoleRequest {
    private String role;

    public UserRole getUserRoleEnum() {
        return UserRole.valueOf(role.toUpperCase());
    }
}

