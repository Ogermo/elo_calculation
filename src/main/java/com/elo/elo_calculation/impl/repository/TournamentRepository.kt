package com.elo.elo_calculation.impl.repository

import com.elo.elo_calculation.impl.entity.Tournament
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@Repository
public interface TournamentRepository : JpaRepository<Tournament, Int>, JpaSpecificationExecutor<Tournament> {
}