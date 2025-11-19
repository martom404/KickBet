package com.mt.KickBet.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "matches"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "hidden", nullable = false)
    @Builder.Default
    private Boolean hidden = false;

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
