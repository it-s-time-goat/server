package com.goat.server.review.dto.response;

import com.goat.server.review.domain.Review;
import lombok.Builder;

@Builder
public record ReviewDetailResponse(
        String directoryName,
        String imageUrl,
        String title,
        String content

) {
    public static ReviewDetailResponse from(Review review){
        return ReviewDetailResponse.builder()
                .directoryName(review.getDirectory().getTitle())
                .imageUrl(review.getImageInfo().getImageUrl())
                .title(review.getTitle())
                .content(review.getContent())
                .build();
    }
}