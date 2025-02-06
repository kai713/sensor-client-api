package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ResponseLogin;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.implementation.RefreshTokenService;
import com.example.demo.service.implementation.UserService;
import com.example.demo.util.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.USER);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setEmail("test@example.com");
    }

    @Test
    void testRegister_UserCreatedSuccessfully() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(testUserDTO);

        UserDTO result = userService.register(testUser);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        UserDTO result = userService.register(testUser);

        assertNull(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Successful() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtils.generateToken(testUser)).thenReturn("mockedToken");

        doAnswer(invocation -> null)
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        var refreshToken = new com.example.demo.entity.RefreshToken();
        refreshToken.setToken("mockedRefreshToken");
        when(refreshTokenService.createRefreshToken("test@example.com")).thenReturn(refreshToken);

        ResponseLogin response = userService.login(loginRequest);

        assertEquals(200, response.getStatusCode());
        assertEquals("mockedToken", response.getToken());
        assertEquals("mockedRefreshToken", response.getRefreshToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("unknown@example.com", "password123");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseLogin response = userService.login(loginRequest);

        assertEquals(500, response.getStatusCode());
        assertEquals("Ошибка во время логина: Пользователь не найден", response.getMessage());
    }

    @Test
    void testChangeRoleById_UserFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(testUserDTO);

        UserDTO result = userService.changeRoleById(1L, UserRole.ADMIN);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangeRoleById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        UserDTO result = userService.changeRoleById(2L, UserRole.ADMIN);

        assertNull(result);
        verify(userRepository, never()).save(any(User.class));
    }
}
