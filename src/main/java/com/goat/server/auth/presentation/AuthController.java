package com.goat.server.auth.presentation;

import com.goat.server.auth.application.AuthService;
import com.goat.server.auth.dto.OnBoardingRequest;
import com.goat.server.auth.dto.response.ReIssueSuccessResponse;
import com.goat.server.global.dto.ResponseTemplate;
import com.goat.server.auth.application.KakaoSocialService;
import com.goat.server.auth.dto.response.SignUpSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AuthController", description = "AuthController 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goat/auth")
public class AuthController {

    private final KakaoSocialService kakaoSocialService;
    private final AuthService authService;

    @Operation(summary = "소셜로그인", description = "소셜 로그인/ 회원가입")
    @GetMapping("/login/{provider}")
    public ResponseEntity<ResponseTemplate<Object>> socialLogin(@PathVariable(value = "provider") String provider,
                                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String kakaoAccessToken) {

        log.info("[AuthController.signup] provider: {}, kakaoAccessToken: {}", provider, kakaoAccessToken);

        SignUpSuccessResponse response = kakaoSocialService.socialLogin(kakaoAccessToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(response));
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @GetMapping("/refresh-token")
    public ResponseEntity<ResponseTemplate<Object>> reIssueToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String refreshToken) {

        log.info("[AuthController.reIssueToken] refreshToken: {}", refreshToken);

        ReIssueSuccessResponse response = authService.reIssueToken(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(response));
    }

    @Operation(summary = "테스트용 토큰발급", description = "테스트용 토큰발급")
    @GetMapping("/test-token")
    public String testToken(@RequestParam Long userId) {

        log.info("[AuthController.testToken]");

        return authService.getTestToken(userId);
    }

    @Operation(summary = "온보딩 회원정보 입력", description = "온보딩 회원정보 입력")
    @PatchMapping("/info")
    public ResponseEntity<ResponseTemplate<Object>> saveOnBoardingInfo(@AuthenticationPrincipal Long userId,
                                                               @RequestBody OnBoardingRequest onBoardingRequest) {

        log.info("[AuthController.saveOnBoardingInfo]");

        authService.saveOnBoardingInfo(userId, onBoardingRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(ResponseTemplate.EMPTY_RESPONSE));
    }
}
