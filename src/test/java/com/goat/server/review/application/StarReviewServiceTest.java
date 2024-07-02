package com.goat.server.review.application;

import com.goat.server.directory.domain.Directory;
import com.goat.server.directory.dto.response.DirectoryResponse;
import com.goat.server.mypage.repository.UserRepository;
import com.goat.server.review.domain.Review;
import com.goat.server.review.domain.StarReview;
import com.goat.server.review.dto.response.ReviewSimpleResponse;
import com.goat.server.review.dto.response.StarReviewResponse;
import com.goat.server.review.dto.response.StarReviewResponseList;
import com.goat.server.review.repository.ReviewRepository;
import com.goat.server.review.repository.StarReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static com.goat.server.directory.fixture.DirectoryFixture.CHILD_DIRECTORY1;
import static com.goat.server.directory.fixture.DirectoryFixture.CHILD_DIRECTORY2;
import static com.goat.server.mypage.fixture.UserFixture.USER_USER;
import static com.goat.server.review.fixture.ReviewFixture.DUMMY_REVIEW1;
import static com.goat.server.review.fixture.ReviewFixture.DUMMY_REVIEW2;
//import static com.goat.server.review.fixture.StarReviewFixture.DUMMY_STARREVIEW1;
import static com.goat.server.review.fixture.StarReviewFixture.DUMMY_STARREVIEW2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StarReviewServiceTest {

    @InjectMocks
    private StarReviewService starReviewService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StarReviewRepository starReviewRepository;

    @Captor
    ArgumentCaptor<StarReview> starReviewCaptor;

    @Test
    @DisplayName("즐겨찾기 추가 테스트")
    void manageFavoriteTest1() {
        //given
        given(userRepository.findById(USER_USER.getUserId())).willReturn(Optional.of(USER_USER));
        given(reviewRepository.findById(DUMMY_REVIEW1.getId())).willReturn(Optional.of(DUMMY_REVIEW1));
        given(starReviewRepository.findByUserAndReview(USER_USER, DUMMY_REVIEW1)).willReturn(Optional.empty());

        //when
        starReviewService.manageFavorite(USER_USER.getUserId(), DUMMY_REVIEW1.getId());

        //then
        verify(starReviewRepository, times(1)).save(starReviewCaptor.capture());
        StarReview savedStarReview = starReviewCaptor.getValue();
        assertThat(savedStarReview.getUser()).isEqualTo(USER_USER);
        assertThat(savedStarReview.getReview()).isEqualTo(DUMMY_REVIEW1);
    }


    @Test
    @DisplayName("즐겨찾기 삭제 테스트")
    void manageFavoriteTest2() {

        //given
        given(userRepository.findById(USER_USER.getUserId())).willReturn(Optional.of(USER_USER));
        given(reviewRepository.findById(DUMMY_REVIEW2.getId())).willReturn(Optional.of(DUMMY_REVIEW2));
        given(starReviewRepository.findByUserAndReview(USER_USER, DUMMY_REVIEW2)).willReturn(Optional.of(DUMMY_STARREVIEW2));

        //when
        starReviewService.manageFavorite(USER_USER.getUserId(), DUMMY_REVIEW2.getId());

        //then
        verify(starReviewRepository, times(1)).delete(starReviewCaptor.capture());
    }
}