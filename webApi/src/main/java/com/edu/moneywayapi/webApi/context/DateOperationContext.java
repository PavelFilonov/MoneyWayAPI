package com.edu.moneywayapi.webApi.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateOperationContext {
    private Long categoryId;
    private String fromDate;
    private String toDate;
}
