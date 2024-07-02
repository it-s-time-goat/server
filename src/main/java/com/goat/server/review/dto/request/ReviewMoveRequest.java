package com.goat.server.review.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReviewMoveRequest(
        @Positive Long reviewId,
        @NotNull Long targetDirectoryId
) {
}