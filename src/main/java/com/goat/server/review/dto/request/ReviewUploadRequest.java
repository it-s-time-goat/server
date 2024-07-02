package com.goat.server.review.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goat.server.directory.domain.Directory;
import com.goat.server.mypage.domain.User;
import com.goat.server.review.domain.Review;
import com.goat.server.review.domain.ReviewDate;
import com.goat.server.review.domain.type.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record ReviewUploadRequest(
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
    public Review toReview(User user, Directory directory){
        Review review = Review.builder()
                .user(user)
                .title(title)
                .content(content)
                .directory(directory)
                .isRepeatable(repeat)
                .isAutoRepeat(autoRepeat)
                .remindTime(remindTime)
                .reviewStartDate(reviewStartDate)
                .reviewEndDate(reviewEndDate)
                .isPostShare(postShare)
                .build();

        if (reviewDates != null && !reviewDates.isEmpty()) {
            List<ReviewDate> dates = reviewDates.stream()
                    .map(date -> ReviewDate.builder()
                            .date(Date.valueOf(date.toUpperCase())) // Convert string to enum
                            .review(review)
                            .build())
                    .collect(Collectors.toList());
            review.setReviewDates(dates);
        }
        return review;
    }
}
