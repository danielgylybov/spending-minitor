package com.spendingmonitor.model;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder
@NoArgsConstructor
public class AuditEntity {

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
