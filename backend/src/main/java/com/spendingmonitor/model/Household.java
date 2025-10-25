package com.spendingmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "households")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Household extends AuditEntity {
    @Id @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;
}
