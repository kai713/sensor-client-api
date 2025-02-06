package com.example.demo.service.implementation;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.repositories.RefreshTokenRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.interfaces.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    // Репозиторий для работы с refresh-токенами
    private final RefreshTokenRepository refreshTokenRepository;

    // Репозиторий для работы с пользователями
    private final UserRepository userRepository;

    /**
     * Создает новый refresh-токен для пользователя по его email.
     * Если у пользователя уже есть refresh-токен, он удаляется и создается новый.
     * @param email Адрес электронной почты пользователя
     * @return Сохраненный refresh-токен
     */
    public RefreshToken createRefreshToken(String email) {
        // Получаем пользователя по email
        User user = userRepository.findByEmail(email).orElseThrow();

        // Удаляем существующий refresh-токен для пользователя
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        // Создаем новый refresh-токен
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString()); // Генерация нового уникального токена
        refreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS)); // Установка времени истечения через 7 дней

        // Сохраняем и возвращаем новый refresh-токен
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Находит refresh-токен по его значению.
     * @param token Строковое представление refresh-токена
     * @return Опционально возвращает найденный refresh-токен
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Удаляет refresh-токен по его значению.
     * @param token Строковое представление refresh-токена
     */
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Проверяет, действителен ли refresh-токен.
     * Токен считается действительным, если его дата истечения не прошла.
     * @param token Refresh-токен для проверки
     * @return true, если токен действителен, иначе false
     */
    public boolean validateRefreshToken(RefreshToken token) {
        return token.getExpiryDate().isAfter(Instant.now()); // Проверка на истечение срока действия
    }
}
