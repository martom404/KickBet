package com.mt.KickBet.service;

import com.mt.KickBet.exception.DuplicateUserException;
import com.mt.KickBet.model.dao.UserRepository;
import com.mt.KickBet.model.dto.auth.RegisterForm;
import com.mt.KickBet.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByID(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void registerUser(RegisterForm form) {
        if (userRepository.existsByEmail(form.email())) {
            throw new DuplicateUserException("Taki email już istnieje w bazie danych.");
        }
        if (userRepository.existsByUsername(form.username())) {
            throw new DuplicateUserException("Taki użytkownik już istnieje w bazie danych.");
        }

        User user = new User();
        user.setEmail(form.email());
        user.setUsername(form.username());
        user.setPasswordHash(passwordEncoder.encode(form.password()));

        userRepository.save(user);
    }
}
