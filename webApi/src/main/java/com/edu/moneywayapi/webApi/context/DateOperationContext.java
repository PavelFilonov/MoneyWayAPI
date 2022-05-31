package com.edu.moneywayapi.webApi.context;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DateOperationContext {
    private Long categoryId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
