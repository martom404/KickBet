package com.mt.KickBet.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private int points = 0;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();
}
