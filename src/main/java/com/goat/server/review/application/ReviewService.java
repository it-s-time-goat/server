package com.goat.server.review.application;

import com.goat.server.directory.domain.Directory;
import com.goat.server.directory.exception.DirectoryNotFoundException;
import com.goat.server.directory.exception.errorcode.DirectoryErrorCode;
import com.goat.server.directory.repository.DirectoryRepository;
import com.goat.server.global.application.S3Uploader;
import com.goat.server.global.config.SchedulerConfiguration;
import com.goat.server.global.domain.ImageInfo;
import com.goat.server.mypage.application.UserService;
import com.goat.server.mypage.domain.User;
import com.goat.server.mypage.exception.UserNotFoundException;
import com.goat.server.mypage.exception.errorcode.MypageErrorCode;
import com.goat.server.mypage.repository.UserRepository;
import com.goat.server.notification.application.NotificationService;
import com.goat.server.notification.domain.Notification;
import com.goat.server.review.domain.Review;
import com.goat.server.review.domain.ReviewDate;
import com.goat.server.review.domain.UnViewedReview;
import com.goat.server.review.dto.request.ReviewMoveRequest;
import com.goat.server.review.dto.request.ReviewUpdateRequest;
import com.goat.server.review.dto.request.ReviewUploadRequest;
import com.goat.server.review.dto.response.ReviewDetailResponse;
import com.goat.server.review.dto.response.RandomReviewsResponse;
import com.goat.server.review.dto.response.ReviewHomeResponse;
import com.goat.server.review.dto.response.ReviewHomeResponseList;
import com.goat.server.review.dto.response.MissedReviewResponse;
import com.goat.server.review.exception.ReviewNotFoundException;
import com.goat.server.review.exception.errorcode.ReviewErrorCode;
import com.goat.server.directory.application.type.SortType;
import com.goat.server.review.dto.response.ReviewSimpleResponse;
import com.goat.server.review.repository.ReviewRepository;

import java.util.List;

import com.goat.server.review.repository.UnViewedReviewRepository;
import com.goat.server.review.util.ReviewShuffleStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final DirectoryRepository directoryRepository;
    private final S3Uploader s3Uploader;
    private final UserService userService;
    private final NotificationService notificationService;

    private final SchedulerConfiguration schedulerConfiguration;

    private final ReviewShuffleStrategy reviewShuffleStrategy;
    private final UnViewedReviewRepository unViewedReviewRepository;

    private static final int PAGE_SIZE_HOME = 4;
    private static final String FOLDER_NAME = "goat";

    /**
     * 폴더에 속한 리뷰 목록 조회
     */
    public List<ReviewSimpleResponse> getReviewSimpleResponseList(
            Long userId, Long directoryId, List<SortType> sort, String search) {

        return reviewRepository.findAllReviewSimpleResponseBySortAndSearch(userId, directoryId, sort, search);
    }

    /**
     * 홈 화면에서 최근 등록 복습 이미지 조회
     */
    public ReviewHomeResponseList getRecentReviews(Long userId, int page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, PAGE_SIZE_HOME, Sort.by("createdDate").descending());
        Page<Review> reviews = reviewRepository.findAllReviewImageByUserId(user.getUserId(), pageable);

        List<ReviewHomeResponse> homeResponses = reviews.stream()
                .map(ReviewHomeResponse::from)
                .toList();

        return ReviewHomeResponseList.from(homeResponses);
    }

    /**
     * 복습 업로드
     */
    @Transactional
    public void uploadReview(Long userId, MultipartFile multipartFile, ReviewUploadRequest request) {
        log.info("[ReviewService.uploadReview]");

        User user = userService.findUser(userId);
        Directory directory = directoryRepository.findById(request.directoryId())
                .orElseThrow(() -> new DirectoryNotFoundException(DirectoryErrorCode.DIRECTORY_NOT_FOUND));

        Review review = request.toReview(user, directory);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            ImageInfo imageInfo = s3Uploader.upload(multipartFile, FOLDER_NAME);
            review.setImageInfo(imageInfo);
        }
        reviewRepository.save(review);

        if(review.getIsRepeatable()) {
            registerNotification(review);
        }
    }

    /**
     * 복습 업로드 시 알림 등록
     */
    private void registerNotification(Review review) {

        if(review.getIsAutoRepeat()) {
            try {
                schedulerConfiguration.scheduleAutoReviewNotification(review);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        for (ReviewDate date : review.getReviewDates()) {
            try {
                schedulerConfiguration.scheduleCustomReviewNotification(review, date);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 복습 자료 한 개 상세 보기
     */
    @Transactional
    public ReviewDetailResponse getDetailReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdAndUser_UserId(reviewId, userId)
                .orElseThrow(() -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
        review.increaseReviewCnt();
        return ReviewDetailResponse.from(review);
    }

    /**
     * 복습 자료 수정
     */
    @Transactional
    public void updateReview(Long reviewId, MultipartFile multipartFile, ReviewUpdateRequest reviewUpdateRequest) {
        Review review = findReview(reviewId);

        ImageInfo imageInfo = review.getImageInfo();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            if (imageInfo != null) {
                s3Uploader.deleteImage(review.getImageInfo());
                imageInfo = s3Uploader.upload(multipartFile, FOLDER_NAME);
            } else {
                imageInfo = s3Uploader.upload(multipartFile, FOLDER_NAME);
            }
        }
        Directory directory = directoryRepository.findById(reviewUpdateRequest.directoryId())
                .orElseThrow(() -> new DirectoryNotFoundException(DirectoryErrorCode.DIRECTORY_NOT_FOUND));
        review.updateReview(reviewUpdateRequest, imageInfo);
        review.updateDirectory(directory);
    }

    /**
     * 복습 자료 한 개 삭제
     */
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = findReview(reviewId);
        unViewedReviewRepository.deleteByReviewId(reviewId);
        reviewRepository.delete(review);
    }

    /**
     * 복습 자료를 복습창고로 이동
     */
    @Transactional
    public void moveReviewToStorage(Long userId, Long reviewId) {
        Review review = findReview(reviewId);
        Directory storageDirectory = directoryRepository.findStorageDirectoryByUser(userId)
                .orElseThrow(() -> new DirectoryNotFoundException(DirectoryErrorCode.DIRECTORY_NOT_FOUND));

        review.updateDirectory(storageDirectory);
    }

    /**
     * 복습 자료를 휴지통으로 이동
     */
    @Transactional
    public void moveReviewToTrashCan(Long userId, Long reviewId) {
        Review review = findReview(reviewId);
        Directory trashDirectory = directoryRepository.findTrashDirectoryByUser(userId)
                .orElseThrow(() -> new DirectoryNotFoundException(DirectoryErrorCode.DIRECTORY_NOT_FOUND));

        review.updateDirectory(trashDirectory);
        unViewedReviewRepository.deleteByReviewId(reviewId);
    }

    /**
     * 복습 자료를 다른 폴더로 이동
     */
    @Transactional
    public void moveReviewDirectory(ReviewMoveRequest request) {
        Review review = findReview(request.reviewId());
        Directory targetDirectory = directoryRepository.findById(request.targetDirectoryId())
                .orElseThrow(() -> new DirectoryNotFoundException(DirectoryErrorCode.DIRECTORY_NOT_FOUND));

        review.updateDirectory(targetDirectory);
    }

    /**
     * 복습 정보 나중에 입력하기
     */
    @Transactional
    public void uploadReviewLater(Long userId, MultipartFile multipartFile) {
        User user = userService.findUser(userId);
        Directory storageDirectory = directoryRepository.findStorageDirectoryByUser(userId)
                .orElseThrow(() -> new DirectoryNotFoundException(DirectoryErrorCode.DIRECTORY_NOT_FOUND));

        ImageInfo imageInfo = s3Uploader.upload(multipartFile, FOLDER_NAME);

        Review review = Review.builder()
                .imageInfo(imageInfo)
                .user(user)
                .directory(storageDirectory)
                .build();
        reviewRepository.save(review);
    }

    /**
     * 모든 유저의 모든 리뷰의 reviewCnt를 매주 월요일 새벽 1시에 초기화
     */
    @Scheduled(cron = "0 0 1 * * MON") // 매주 월요일 1시에 실행
    @Transactional
    public void initReviewCount() {
        log.info("[Starting Reviewcount Reset]");
        List<Review> reviews = reviewRepository.findAll();

        for (Review review : reviews) {
            review.resetReviewCnt();
        }
    }

    /**
     * 랜덤리뷰 전체 불러오기 (바로복습 누르기)
     */
    @Transactional
    public void loadRandomReviews(Long userId) {
        User user = userService.findUser(userId);
        unViewedReviewRepository.findFirstByUserId(userId)
                .ifPresentOrElse(
                        unviewedReview -> {},
                        () -> {
                            List<Review> shuffledReviews = reviewShuffleStrategy.shuffle(reviewRepository.findActiveReviewsByUserId(userId));
                            saveUnViewedReviews(shuffledReviews, user);
                        }
                );
    }

    @Transactional
    public void saveUnViewedReviews(final List<Review> reviews, final User user) {
        List<UnViewedReview> unViewedReviews = reviews.stream()
                .map(review -> new UnViewedReview(review, user))
                .toList();
        unViewedReviewRepository.saveAll(unViewedReviews);
    }

    /**
     * 랜덤리뷰 하나씩 불러오기 (바로 복습에서 랜덤 리뷰 하나씩)
     */
    @Transactional
    public RandomReviewsResponse getRandomReview(Long userId) {
        return unViewedReviewRepository.findFirstByUserId(userId)
                .map(unviewedReview -> {
                    Long reviewId = unviewedReview.getReview().getId();
                    Review review = findReview(reviewId);
                    unViewedReviewRepository.delete(unviewedReview);
                    return RandomReviewsResponse.from(review);
                })
                .orElseGet(RandomReviewsResponse::emptyResponse);
    }

    /**
     * 매일 새벽 0시에 모든 unviewed 리뷰를 삭제
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 새벽 0시에 실행
    @Transactional
    public void deleteAllUnViewedReviews() {
        unViewedReviewRepository.deleteAll();
    }

    /**
     * 유저의 총 리뷰 개수 계산
     */
    public Long calculateReviewCount(Long userId) {
        return reviewRepository.sumReviewCntByUser(userId);
    }

    public Review findReview(final Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * 놓친 복습 불러오기
     */
    public MissedReviewResponse getMissedReview(Long userId) {

        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);

        List<ReviewSimpleResponse> missedReviews = unreadNotifications.stream()
                .map(Notification::getReview)
                .map(ReviewSimpleResponse::from)
                .toList();

        return MissedReviewResponse.from(missedReviews);
    }
}