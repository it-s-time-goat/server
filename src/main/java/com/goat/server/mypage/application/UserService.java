package com.goat.server.mypage.application;

import com.goat.server.auth.dto.response.KakaoUserResponse;
import com.goat.server.directory.domain.Directory;
import com.goat.server.directory.repository.DirectoryRepository;
import com.goat.server.global.application.S3Uploader;
import com.goat.server.global.domain.ImageInfo;
import com.goat.server.mypage.domain.User;
import com.goat.server.mypage.domain.type.Role;
import com.goat.server.mypage.exception.UserNotFoundException;
import com.goat.server.mypage.exception.errorcode.MypageErrorCode;
import com.goat.server.mypage.repository.UserRepository;
import com.goat.server.notification.domain.NotificationSetting;
import com.goat.server.notification.repository.NotificationSettingRepository;
import com.goat.server.review.domain.Review;
import com.goat.server.review.dto.request.ReviewUpdateRequest;
import com.goat.server.review.exception.ReviewNotFoundException;
import com.goat.server.review.exception.errorcode.ReviewErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final NotificationSettingRepository notificationSettingRepository;

    /**
     * 유저 회원가입
     */
    @Transactional
    public User createUser(final KakaoUserResponse userResponse) {
        User user = User.builder()
                .socialId(userResponse.id().toString())
                .nickname(userResponse.getNickname())
                .role(Role.GUEST)
                .build();

        Directory trashDirectory = Directory.builder()
                .user(user)
                .title("trash_directory")
                .depth(1L)
                .build();

        Directory storageDirectory = Directory.builder()
                .user(user)
                .title("storage_directory")
                .depth(1L)
                .build();

        NotificationSetting notificationSetting = NotificationSetting.builder()
                .user(user)
                .isCommentNoti(false)
                .isPostNoti(false)
                .isReviewNoti(false)
                .build();


        notificationSettingRepository.save(notificationSetting);
        directoryRepository.save(trashDirectory);
        directoryRepository.save(storageDirectory);

        return userRepository.save(user);
    }

    public User findUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));
    }

    /**
     * 프로필 이미지 업데이트
     */
    @Transactional
    public void updateProfileImage(Long userId, MultipartFile multipartFile) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));

        ImageInfo imageInfo = user.getImageInfo();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            if (imageInfo != null) {
                s3Uploader.deleteImage(user.getImageInfo());
                String folderName = "profile";
                imageInfo = s3Uploader.upload(multipartFile, folderName);
            } else {
                String folderName = "profile";
                imageInfo = s3Uploader.upload(multipartFile, folderName);
            }
        }

        user.updateProfileImage(imageInfo);
    }
}
