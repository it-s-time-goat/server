package com.goat.server.review.domain;

import com.goat.server.global.domain.BaseTimeEntity;
import com.goat.server.global.domain.ImageInfo;
import com.goat.server.directory.domain.Directory;
import com.goat.server.mypage.domain.User;
import com.goat.server.review.domain.type.Date;
import com.goat.server.review.dto.request.ReviewUpdateRequest;
import com.goat.server.review.util.ReviewRemoveListener;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(ReviewRemoveListener.class)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Embedded
    private ImageInfo imageInfo;

    @Column(name = "review_title", length = 50)
    private String title;

    @Column(name = "content", length = 512)
    private String content;

    @Column(name = "is_repeatable")
    private Boolean isRepeatable;

    @Column(name = "is_auto_repeat")
    private Boolean isAutoRepeat;

    @OneToMany(mappedBy = "review", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ReviewDate> reviewDates = new ArrayList<>();

    @Column(name = "remind_time")
    private LocalTime remindTime;

    @Column(name = "review_start_date")
    private LocalDate reviewStartDate;

    @Column(name = "review_end_date")
    private LocalDate reviewEndDate;

    @JoinColumn(name = "directory_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Directory directory;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "review_cnt")
    private Long reviewCnt;

    @Column(name = "is_star")
    private Boolean isStar;

    @Builder
    public Review(ImageInfo imageInfo, String title, String content, Boolean isRepeatable,
                  Boolean isAutoRepeat, LocalTime remindTime, LocalDate reviewStartDate, LocalDate reviewEndDate,
                  Directory directory, User user, Long reviewCnt, Boolean isStar) {
        this.imageInfo = imageInfo;
        this.title = title;
        this.content = content;
        this.isRepeatable = isRepeatable;
        this.isAutoRepeat = isAutoRepeat;
        this.remindTime = remindTime;
        this.reviewStartDate = reviewStartDate;
        this.reviewEndDate = reviewEndDate;
        this.directory = directory;
        this.user = user;
        this.reviewCnt = (reviewCnt != null) ? reviewCnt : 0L;
        this.isStar = isStar;
    }

    public void setImageInfo(ImageInfo imageInfo){
        this.imageInfo = imageInfo;
    }

    public void setReviewDates(List<ReviewDate> reviewDates) {
        this.reviewDates.clear();
        if (reviewDates != null) {
            this.reviewDates.addAll(reviewDates);
            for (ReviewDate reviewDate : reviewDates) {
                reviewDate.setReview(this);
            }
        }
    }

    public void updateReview(ReviewUpdateRequest reviewUpdateRequest, ImageInfo imageInfo) {
        this.title = reviewUpdateRequest.title();
        this.content = reviewUpdateRequest.content();
        this.isRepeatable = reviewUpdateRequest.repeat();
        this.isAutoRepeat = reviewUpdateRequest.autoRepeat();
        this.remindTime = reviewUpdateRequest.remindTime();
        this.reviewStartDate = reviewUpdateRequest.reviewStartDate();
        this.reviewEndDate = reviewUpdateRequest.reviewEndDate();
        this.imageInfo = imageInfo;

        if (!reviewUpdateRequest.autoRepeat()) {
            List<Date> newReviewDates = reviewUpdateRequest.reviewDates().stream()
                    .map(Date::valueOf)
                    .toList();

            // 기존 reviewDates에서 삭제해야 할 날짜 제거
            this.reviewDates.removeIf(existingReviewDate -> !newReviewDates.contains(existingReviewDate.getDate()));

            // 새로운 날짜 추가
            for (Date date : newReviewDates) {
                boolean exists = this.reviewDates.stream()
                        .anyMatch(existingReviewDate -> existingReviewDate.getDate().equals(date));

                if (!exists) {
                    this.reviewDates.add(ReviewDate.builder()
                            .date(date)
                            .review(this)
                            .build());
                }
            }
        }
    }

    public String getImageUrl() {
        if (this.imageInfo == null) {
            return "default-image-url";
        } else {
            return this.imageInfo.getImageUrl();
        }
    }

    public void updateDirectory(Directory directory){
        this.directory = directory;
    }

    public void resetReviewCnt() {
        this.reviewCnt = 0L;
    }

    public void increaseReviewCnt() {
        this.reviewCnt++;
    }

    public void updateIsStar(Boolean isStar) {
        this.isStar = isStar;
    }
}
