package com.spendingmonitor.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "import_batches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportBatch {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "household_id", nullable = false)
    private UUID householdId;

    private String source;

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String fileName;
    private int totalRows;
    private int appliedRows;
    private int skippedRows;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    public enum Status { UPLOADED, PARSED, APPLIED, DISCARDED }
}
