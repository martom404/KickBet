package com.mt.KickBet.model.dao;

import com.mt.KickBet.model.entity.Role;
import com.mt.KickBet.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByRoleIsNot(Role role, Pageable pageable);

    Optional<User> findByUsernameAndLockedIsFalse(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
