package com.example.demo.repositories;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email электронная почта пользователя
     * @return {@code true}, если пользователь с данным email существует, иначе {@code false}.
     */
    boolean existsByEmail(String email);

    /**
     * Находит пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя
     * @return объект {@link Optional} с найденным пользователем, если он существует, или пустой объект {@link Optional}, если пользователь не найден.
     */
    Optional<User> findByEmail(String email);
}
