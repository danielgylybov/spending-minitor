package com.spendingmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "merchant_aliases")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MerchantAlias extends AuditEntity {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "household_id", nullable = false)
    private UUID householdId;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false)
    private MatchType matchType;

    @Column(nullable = false)
    private String pattern;

    public enum MatchType { EXACT, CONTAINS, REGEX, IBAN, MCC }
}
