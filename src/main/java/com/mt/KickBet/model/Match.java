package com.mt.KickBet.model;

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
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "home_team", nullable = false, length = 80)
    @ToString.Include
    private String homeTeam;

    @Column(name = "away_team", nullable = false, length = 80)
    @ToString.Include
    private String awayTeam;

    @Column(name = "start_time", nullable = false)
    @ToString.Include
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_result", nullable = true)
    private Result finalResult;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "match")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private List<Bet> bets = new ArrayList<>();

}
