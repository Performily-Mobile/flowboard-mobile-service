package com.performily.flowboard.iam.domain.model.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @Column(name = "token_id", length = 80)
    public String tokenId;

    @Column(name = "expires_at", nullable = false)
    public Instant expiresAt;

    public RevokedToken() {}

    public RevokedToken(String id, Instant expires) {
        this.tokenId = id;
        this.expiresAt = expires;
    }
}