package com.mt.KickBet.service;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.exception.NoUserException;
import com.mt.KickBet.exception.UserIsLockedException;
import com.mt.KickBet.model.dao.UserRepository;
import com.mt.KickBet.model.dto.auth.RegisterForm;
import com.mt.KickBet.model.dto.user.UpdateUserRequest;
import com.mt.KickBet.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> getAllUsers(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "points");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    public Page<User> getAllUsersForAdmin(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    public int calculateRankPosition(Double points) {
        long usersWithMorePoints = userRepository.countUsersWithMorePoints(points);
        return (int) (usersWithMorePoints + 1);
    }

    public Optional<User> getUserByID(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void registerUser(RegisterForm form) {
        if (userRepository.existsByEmail(form.email()))
            throw new DuplicateUserException("Użytkownik z takim emailem istnieje już w naszej bazie danych.");

        if (userRepository.existsByUsername(form.username()))
            throw new DuplicateUserException("Użytkownik z taką nazwą istnieje już w naszej bazie danych.");

        userRepository.save(
                User.builder()
                        .username(form.username())
                        .email(form.email())
                        .passwordHash(passwordEncoder.encode(form.password()))
                        .build()
        );
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserException("Brak użytkownika w systemie!"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash()))
            throw new IllegalArgumentException("Aktualne hasło jest nieprawidłowe!");

        if(passwordEncoder.matches(newPassword, user.getPasswordHash()))
            throw new IllegalArgumentException("Nowe hasło nie może być takie same jak aktualne!");

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void blockUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLocked(!user.isLocked());
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateUser (Long id, UpdateUserRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoUserException("Użytkownik nie został znaleziony."));

        if (!user.getUsername().equals(dto.username()) && userRepository.existsByUsername(dto.username())) {
            throw new DuplicateUserException("Użytkownik z taką nazwą istnieje już w naszej bazie danych.");
        }

        if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new DuplicateUserException("Użytkownik z takim emailem istnieje już w naszej bazie danych.");
        }

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPoints(dto.points());
        userRepository.save(user);
    }

    public void validateLockedStatus(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            if (user.isLocked()) {
                throw new UserIsLockedException("Twoje konto zostało zablokowane.");
            }
        });
    }
}
