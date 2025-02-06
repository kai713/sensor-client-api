package com.example.demo.service.implementation;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ResponseLogin;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.interfaces.IUserService;
import com.example.demo.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDTO register(User user) {

        User savedUser = new User();

        try {
            if (user.getRole() == null || user.getRole().toString().isBlank()) {
                user.setRole(UserRole.USER);
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException(user.getEmail() + " уже существует");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            savedUser = userRepository.save(user);

        } catch (Exception e) {
            return null;
        }
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    @Transactional
    public ResponseLogin login(LoginRequest loginRequest) {

        ResponseLogin response = new ResponseLogin();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            var token = jwtUtils.generateToken(user);
            var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            response.setStatusCode(200);
            response.setToken(token);
            response.setRefreshToken(refreshToken.getToken());
            response.setRole(user.getRole().toString());
            response.setExpirationTime("1 час");
            response.setMessage("Успешно");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Ошибка во время логина: " + e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    public UserDTO changeRoleById(Long userId, UserRole userRole) {
        User user = new User();
        try {
            user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            user.setRole(userRole);
            userRepository.save(user);
        } catch (Exception e) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }
}
