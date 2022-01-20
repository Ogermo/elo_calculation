package com.elo.elo_calculation.impl.repository;

import com.elo.elo_calculation.impl.entity.Match;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
}
