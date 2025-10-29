package com.mt.KickBet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "bets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_bets_user_match", columnNames = {"user_id", "match_id"})
        },
        indexes = {
                @Index(name = "idx_bets_user", columnList = "user_id"),
                @Index(name = "idx_bets_match", columnList = "match_id")
        }
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bet_user"))
    @ToString.Exclude
    @JsonIgnore
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bet_match"))
    @ToString.Exclude
    @JsonIgnore
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(name = "pick", nullable = false, length = 8)
    private Result pick;

    @Column(nullable = false)
    @Builder.Default
    private Integer pointsAwarded = 0;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
