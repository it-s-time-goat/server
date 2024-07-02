package com.goat.server.mypage.dto.response;

import com.goat.server.mypage.domain.User;
import com.goat.server.mypage.domain.type.Grade;
import lombok.Builder;

import java.util.List;

@Builder
public record MypageHomeResponse (
        String imageUrl,
        String nickname,
        Grade grade,
        String goal,
        Long totalReviewCnt,
        List<String> majorList
) {
    public static MypageHomeResponse of(User user, List<String> majorNames, Long totalReviewCnt) {
        return MypageHomeResponse.builder()
                .imageUrl(user.getProfileImageUrl())
                .nickname(user.getNickname())
                .grade(user.getGrade())
                .goal(user.getGoal())
                .majorList(majorNames)
                .totalReviewCnt(totalReviewCnt)
                .build();
    }
}
