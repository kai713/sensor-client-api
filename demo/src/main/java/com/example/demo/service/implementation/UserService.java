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

    // Репозиторий для работы с пользователями
    private final UserRepository userRepository;

    // Шифратор паролей
    private final PasswordEncoder passwordEncoder;

    // Утилиты для работы с JWT
    private final JWTUtils jwtUtils;

    // Менеджер для аутентификации пользователей
    private final AuthenticationManager authenticationManager;

    // Сервис для работы с refresh-токенами
    private final RefreshTokenService refreshTokenService;

    // Маппер для преобразования сущностей в DTO
    private final ModelMapper modelMapper;

    /**
     * Регистрация нового пользователя.
     * Если роль пользователя не указана, присваивается роль USER.
     * Пароль пользователя шифруется перед сохранением.
     * @param user Пользователь, который регистрируется
     * @return DTO пользователя, если регистрация прошла успешно, иначе null
     */
    @Override
    @Transactional
    public UserDTO register(User user) {

        User savedUser = new User();

        try {
            // Если роль не указана, присваиваем роль USER
            if (user.getRole() == null || user.getRole().toString().isBlank()) {
                user.setRole(UserRole.USER);
            }

            // Проверка на уникальность email
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException(user.getEmail() + " уже существует");
            }

            // Шифрование пароля
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Сохранение пользователя в базе данных
            savedUser = userRepository.save(user);

        } catch (Exception e) {
            return null;
        }

        // Преобразование сущности в DTO и возврат
        return modelMapper.map(savedUser, UserDTO.class);
    }

    /**
     * Логин пользователя.
     * Аутентификация выполняется с использованием email и пароля.
     * Возвращается ответ с токеном и refresh-токеном.
     * @param loginRequest Данные для логина
     * @return Ответ с токенами и статусом
     */
    @Override
    @Transactional
    public ResponseLogin login(LoginRequest loginRequest) {

        ResponseLogin response = new ResponseLogin();

        try {
            // Аутентификация пользователя
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Поиск пользователя по email
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Генерация токенов
            var token = jwtUtils.generateToken(user);
            var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            // Установка данных в ответ
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

    /**
     * Изменение роли пользователя по ID.
     * @param userId ID пользователя
     * @param userRole Новая роль пользователя
     * @return DTO обновленного пользователя, если изменение прошло успешно, иначе null
     */
    @Override
    @Transactional
    public UserDTO changeRoleById(Long userId, UserRole userRole) {
        User user = new User();
        try {
            // Поиск пользователя по ID
            user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Установка новой роли
            user.setRole(userRole);

            // Сохранение обновленных данных
            userRepository.save(user);
        } catch (Exception e) {
            return null;
        }

        // Преобразование сущности в DTO и возврат
        return modelMapper.map(user, UserDTO.class);
    }
}
