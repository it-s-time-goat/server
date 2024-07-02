package com.goat.server.review.application;

import com.goat.server.mypage.domain.User;
import com.goat.server.mypage.exception.UserNotFoundException;
import com.goat.server.mypage.exception.errorcode.MypageErrorCode;
import com.goat.server.mypage.repository.UserRepository;
import com.goat.server.review.domain.Review;
import com.goat.server.review.domain.StarReview;
import com.goat.server.review.dto.response.StarReviewInfoResponse;
import com.goat.server.review.dto.response.StarReviewResponseList;
import com.goat.server.review.exception.ReviewNotFoundException;
import com.goat.server.review.exception.errorcode.ReviewErrorCode;
import com.goat.server.review.repository.ReviewRepository;
import com.goat.server.review.repository.StarReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StarReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final StarReviewRepository starReviewRepository;

    private static final int PAGE_SIZE_STAR_REVIEW = 2;

    /**
     * 즐겨찾기 추가 및 삭제
     */
    @Transactional
    public void manageFavorite(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));

        starReviewRepository.findByUserAndReview(user, review).ifPresentOrElse(
                starReviewRepository::delete,
                () -> starReviewRepository.save(new StarReview(user, review))
        );
    }

    /**
     * 즐겨찾기한 목록 보기
     */
    public StarReviewResponseList getMyFavoriteReviews(Long userId, int page){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, PAGE_SIZE_STAR_REVIEW, Sort.by("createdDate").descending());
        Page<Review> reviews = reviewRepository.findAllStarReviewByUserId(user.getUserId(), pageable);

        List<StarReviewInfoResponse> starReviewInfoResponses = reviews.getContent().stream()
                .map(StarReviewInfoResponse::from)
                .toList();

        return StarReviewResponseList.from(starReviewInfoResponses);
    }
}
