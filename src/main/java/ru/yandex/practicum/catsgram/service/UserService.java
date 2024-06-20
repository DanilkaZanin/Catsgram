package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();


    public Optional<User> findById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        } else if (!isEmailValid(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    private boolean isEmailValid(String email) {
        return users.values().stream().noneMatch(user1 -> user1.getEmail().equals(email));
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User update(User user) {

        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        } else if (!isEmailValid(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());

            if (user.getEmail() == null) {
                user.setEmail(oldUser.getEmail());
            } else if (user.getUsername() == null) {
                user.setUsername(oldUser.getUsername());
            } else if (user.getPassword() == null) {
                user.setPassword(oldUser.getPassword());
            }

            users.put(user.getId(), user);
            return user;
        }
        throw new NotFoundException("Пост с id = " + user.getId() + " не найден");
    }
}
