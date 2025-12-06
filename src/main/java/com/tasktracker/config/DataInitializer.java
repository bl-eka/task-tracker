package com.tasktracker.config;

import com.tasktracker.entity.User;
import com.tasktracker.entity.Role;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создаем администратора, если его еще нет
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
            System.out.println("Администратор создан: admin@example.com / admin123");
        }

        // Создаем тестового пользователя, если его нет
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.ROLE_USER);

            userRepository.save(user);
            System.out.println("Пользователь создан: user@example.com / user123");
        }

        // Проверяем и выводим количество пользователей
        long userCount = userRepository.count();
        System.out.println("Всего пользователей в системе: " + userCount);
    }
}