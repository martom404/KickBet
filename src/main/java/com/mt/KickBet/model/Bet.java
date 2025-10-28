package com.mt.KickBet.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets", uniqueConstraints = {
        @UniqueConstraint(name="uk_bets_user_match" , columnNames = {"user_id", "match_id"})
})
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Bet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, foreignKey = @ForeignKey(name="fk_bet_user"))
    private User user;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="match_id", nullable=false, foreignKey = @ForeignKey(name="fk_bet_match"))
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result;

    private Integer pointsAwarded;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
