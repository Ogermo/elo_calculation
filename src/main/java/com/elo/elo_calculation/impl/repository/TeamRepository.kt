package com.elo.elo_calculation.impl.repository

import com.elo.elo_calculation.generated.ID
import com.elo.elo_calculation.impl.entity.Team
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@Repository
public interface TeamRepository : JpaRepository<Team, ID>, JpaSpecificationExecutor<Team> {
}