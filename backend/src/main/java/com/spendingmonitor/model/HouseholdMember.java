package com.spendingmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "household_members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(HouseholdMemberId.class)
public class HouseholdMember extends AuditEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "household_id")
    private UUID householdId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role { OWNER, MEMBER }
}
