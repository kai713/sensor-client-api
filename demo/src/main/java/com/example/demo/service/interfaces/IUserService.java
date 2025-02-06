package com.example.demo.service.interfaces;


import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ResponseLogin;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;

public interface IUserService {
    UserDTO register(User user);

    ResponseLogin login(LoginRequest loginRequest);

    UserDTO changeRoleById(Long userId, UserRole userRole);
}
