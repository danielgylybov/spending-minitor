package com.spendingmonitor.model;

import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HouseholdMemberId implements Serializable {
    private UUID userId;
    private UUID householdId;
}
