package com.mt.KickBet.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_points", columnList = "points")
        }
)
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 60)
    @JsonIgnore
    private String passwordHash;

    @Column(nullable = false)
    private int points = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Bet> bets = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean locked = false;

}
