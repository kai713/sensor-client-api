package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtils {
    // Время жизни токена (1 час)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // Секретный ключ для подписи токена
    private final SecretKey Key;

    /**
     * Конструктор для инициализации секретного ключа.
     * Секретный ключ извлекается из строки и преобразуется в байты с использованием Base64.
     */
    public JWTUtils() {
        String secreteString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T4783786376645387456738" +
                "0294329FNWN12344";
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Генерация JWT токена на основе данных пользователя.
     * @param userDetails Данные пользователя, для которого генерируется токен
     * @return Сгенерированный JWT токен
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername()) // Устанавливаем имя пользователя как subject
                .issuedAt(new Date(System.currentTimeMillis())) // Устанавливаем время выдачи токена
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Устанавливаем время истечения токена
                .signWith(Key) // Подписываем токен с использованием секретного ключа
                .compact();
    }

    /**
     * Извлечение имени пользователя из JWT токена.
     * @param token JWT токен
     * @return Имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject); // Извлекаем имя пользователя (subject)
    }

    /**
     * Обобщенный метод для извлечения данных из токена.
     * @param token JWT токен
     * @param claimsTFunction Функция для извлечения данных из токена
     * @param <T> Тип возвращаемых данных
     * @return Извлеченные данные
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }

    /**
     * Проверка валидности токена.
     * @param token JWT токен
     * @param userDetails Данные пользователя
     * @return true, если токен валиден, иначе false
     */
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Извлекаем имя пользователя из токена
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Проверяем, совпадает ли имя и не истек ли токен
    }

    /**
     * Проверка истечения срока действия токена.
     * @param token JWT токен
     * @return true, если токен истек, иначе false
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date()); // Проверяем, истек ли срок действия токена
    }
}
