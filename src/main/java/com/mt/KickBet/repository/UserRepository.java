package com.mt.KickBet.repository;


import com.mt.KickBet.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndLockedIsFalse(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
