package com.goat.server.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record ReviewUpdateRequest(
        String title,
        String content,
        Long directoryId,
        Boolean repeat,
        Boolean autoRepeat,
        List<String> reviewDates,
        @Schema(example = "15:00", type = "string") LocalTime remindTime,
        LocalDate reviewStartDate,
        LocalDate reviewEndDate,
        Boolean postShare
) {
}