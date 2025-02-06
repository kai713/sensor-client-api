package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.enums.UserRole;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.interfaces.IRefreshTokenService;
import com.example.demo.service.interfaces.IUserService;
import com.example.demo.util.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUserService userService;
    private final IRefreshTokenService refreshTokenService;
    private final JWTUtils jwtUtils;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Добавляет нового пользователя в бд",
            parameters = {
                    @Parameter(name = "name", description = "Имя пользователя", required = true, example = "John"),
                    @Parameter(name = "email", description = "Валидный email", required = true, example = "john@gmail.com"),
                    @Parameter(name = "phoneNumber", description = "Номер телефона", required = true, example = "+77754936161"),
                    @Parameter(name = "password", description = "Пароль", required = true, example = "Johny Depp"),
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Возвращает UserDTO с параметрами которые были отправленный"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        if (userService.register(modelMapper.map(userDTO, User.class)) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(modelMapper.map(userDTO, UserDTO.class), HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "Делает логин существующего пользователя, возвращает jwt access token и refresh token",
            description = "Добавляет нового пользователя в бд",
            parameters = {
                    @Parameter(name = "email", description = "Ваш email, валидация и проверка пользователя в бд происходить за счет почты", required = true, example = "john@gmail.com"),
                    @Parameter(name = "password", description = "Ваш пароль который вы ввели при регистрации", required = true, example = "Johny Depp"),
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Возвращает ResponseLogin с параметрами accessToken и refreshToken"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<ResponseLogin> login(@RequestBody LoginRequest loginRequest) {
        try {
            ResponseLogin response = userService.login(loginRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseLogin());
        }
    }

    @Operation(
            summary = "При исчерпали срок действии accessToken-a, за счет refreshToken можно получить новый accessToken",
            description = "Возвращает accessToken если refreshToken валидный",
            parameters = {
                    @Parameter(name = "refreshToken", description = "refreshToken который выдается при login", required = true, example = "7f6d6c77-f65e-450b-96b9-1049944a5165"),
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Возвращает AccessToken с параметрами accessToken и refreshToken"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(refreshToken)
                .filter(refreshTokenService::validateRefreshToken)
                .map(token -> {
                    var user = token.getUser();
                    String newAccessToken = jwtUtils.generateToken(user);
                    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token")));
    }

    @PatchMapping("/changeRole")
    @Operation(
            summary = "Позволяет поменять роль пользователя",
            description = "Меняет роль пользователя за счет userId который берется из токена",
            parameters = {
                    @Parameter(name = "role", description = "два параметра, USER и ADMIN", required = true, example = "ADMIN"),
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Возвращает UserDTO с параметрами пользователя"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<UserDTO> changeUserRole(@RequestBody ChangeUserRoleRequest changeUserRoleRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserDTO());
        }

        UserRole userRole = changeUserRoleRequest.getUserRoleEnum();

        if (userService.changeRoleById(user.getId(), userRole) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(modelMapper.map(userRole, UserDTO.class), HttpStatus.OK);
        }
    }
}
