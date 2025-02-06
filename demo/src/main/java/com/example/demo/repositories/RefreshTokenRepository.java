package com.example.demo.repositories;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link RefreshToken}.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Находит токен обновления по его значению.
     *
     * @param token значение токена обновления
     * @return объект Optional с найденным токеном обновления, если он существует, или пустой объект Optional, если токен не найден.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Находит токен обновления по пользователю.
     *
     * @param user пользователь, чьим токеном обновления нужно найти
     * @return объект Optional с найденным токеном обновления для пользователя, если он существует, или пустой объект Optional, если токен не найден.
     */
    Optional<RefreshToken> findByUser(User user);
}
