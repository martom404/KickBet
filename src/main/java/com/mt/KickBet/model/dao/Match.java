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
        name = "matches",
        indexes = {
                @Index(name = "idx_matches_start_time", columnList = "start_time")
        }
)
@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true)
    private Long externalId;

    @Column(name = "home_team", nullable = false, length = 80)
    private String homeTeam;

    @Column(name = "away_team", nullable = false, length = 80)
    private String awayTeam;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_result")
    private Result finalResult;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "match")
    @Builder.Default
    @JsonIgnore
    private List<Bet> bets = new ArrayList<>();

}
