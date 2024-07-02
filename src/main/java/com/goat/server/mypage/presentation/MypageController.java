package com.goat.server.mypage.presentation;

import com.goat.server.global.dto.ResponseTemplate;
import com.goat.server.mypage.application.MypageService;
import com.goat.server.mypage.application.UserService;
import com.goat.server.mypage.dto.request.GoalRequest;
import com.goat.server.mypage.dto.request.MypageDetailsRequest;
import com.goat.server.mypage.dto.response.MypageDetailsResponse;
import com.goat.server.mypage.dto.response.MypageHomeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "MypageController", description = "MypageController 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goat/mypage")
public class MypageController {

    private final MypageService mypageService;
    private final UserService userService;

    @Operation(summary = "마이페이지 정보 보기", description = "마이페이지 정보 보기")
    @GetMapping("/info")
    public ResponseEntity<ResponseTemplate<Object>> getMypageAllDetail(@AuthenticationPrincipal Long userId) {

        MypageHomeResponse mypageHomeResponse = mypageService.getUserWithMajors(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(mypageHomeResponse));
    }

    @Operation(summary = "마이페이지 세부 정보 보기", description = "마이페이지에서 프로필이미지, 닉네임, 전공, 학년 보기")
    @GetMapping("/info/details")
    public ResponseEntity<ResponseTemplate<Object>> getMypageDetails(@AuthenticationPrincipal Long userId) {

        MypageDetailsResponse mypageDetailsResponse = mypageService.getMypageDetails(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(mypageDetailsResponse));
    }

    @Operation(summary = "마이페이지 세부 정보 수정하기", description = "마이페이지에서 닉네임, 전공, 학년 수정하기")
    @PutMapping("/info/details")
    public ResponseEntity<ResponseTemplate<Object>> updateUserDetails(
            @AuthenticationPrincipal Long userId,
            @RequestBody MypageDetailsRequest request) {

        mypageService.updateMypageDetails(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }

    @Operation(summary = "마이페이지 프로필 이미지 수정하기", description = "마이페이지 프로필 이미지 수정하기")
    @PutMapping (consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, value ="/info/profile")
    public ResponseEntity<ResponseTemplate<Object>> updateProfileImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart MultipartFile multipartFile) {

        userService.updateProfileImage(userId, multipartFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }

    @Operation(summary = "마이페이지 목표 수정하기", description = "마이페이지 목표 수정하기")
    @PutMapping("/goal")
    public ResponseEntity<ResponseTemplate<Object>> updateGoal(
            @AuthenticationPrincipal Long userId,
            @RequestBody GoalRequest goalRequest) {

        mypageService.updateGoal(userId, goalRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }
}
