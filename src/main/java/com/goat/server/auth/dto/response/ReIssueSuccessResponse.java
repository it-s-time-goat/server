package com.goat.server.auth.dto.response;

import com.goat.server.global.domain.type.Tokens;
import lombok.Builder;

@Builder
public record ReIssueSuccessResponse(
        String accessToken,
        String refreshToken
) {
    public static ReIssueSuccessResponse From(Tokens token) {
        return ReIssueSuccessResponse.builder()
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .build();
    }
}