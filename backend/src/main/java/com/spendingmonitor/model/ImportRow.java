package com.spendingmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "import_rows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportRow {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "batch_id", nullable = false)
    private UUID batchId;
    private String rawDate;
    private String rawAmount;
    private String rawCurrency;
    private String rawCounterparty;
    private String rawReference;
    private String rawMcc;
    private String rawIban;

    private Instant parsedAt;
    private Boolean parseOk;
    private String parseError;
    private LocalDate occurredAt;
    private BigDecimal amount;
    private String currency;
    private String counterparty;
    private String reference;
    private UUID merchantId;
    private UUID categoryId;
    private String dedupeHash;
    private String decidedBy;
}
