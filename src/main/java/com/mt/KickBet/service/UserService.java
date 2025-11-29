package com.mt.KickBet.service;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.exception.NoUserException;
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

    public int getUserRankPosition(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserException("Brak użytkownika w systemie!"));
        return calculateRankPosition(user.getPoints());
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
            throw new DuplicateUserException("Taki email już istnieje w bazie danych.");

        if (userRepository.existsByUsername(form.username()))
            throw new DuplicateUserException("Użytkownik o takiej nazwie już istnieje w bazie danych.");

        User user = new User();
        user.setEmail(form.email());
        user.setUsername(form.username());
        user.setPasswordHash(passwordEncoder.encode(form.password()));
        userRepository.save(user);
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
        userRepository.findById(id).ifPresent(user -> {
            user.setUsername(dto.username());
            user.setEmail(dto.email());
            user.setPoints(dto.points());
            userRepository.save(user);
        });
    }
}
