package com.goat.server.review.dto.response;

import com.goat.server.review.domain.Review;
import lombok.Builder;

@Builder
public record ReviewHomeResponse(
        Long reviewId,
        String imageUrl

) {
    public static ReviewHomeResponse from(Review review) {
        return ReviewHomeResponse.builder()
                .reviewId(review.getId())
                .imageUrl(review.getImageUrl())
                .build();
    }
}