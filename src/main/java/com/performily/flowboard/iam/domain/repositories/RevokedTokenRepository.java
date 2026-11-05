package com.performily.flowboard.iam.domain.repositories;

import com.performily.flowboard.iam.domain.model.entities.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {}