package com.goat.server.subject.presentation;

import com.goat.server.global.dto.ResponseTemplate;
import com.goat.server.subject.application.SubjectService;
import com.goat.server.subject.dto.response.SubjectResponseList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SubjectController", description = "SubjectController 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/subject")
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "과목, 폴더 정보 가져 오기", description = "과목, 폴더 정보 가져 오기")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseTemplate<Object>> getSubjectsAndDirectories(@PathVariable Long userId) {

        SubjectResponseList subjectsAndDirectories = subjectService.getSubjectsAndDirectories(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(subjectsAndDirectories));
    }
}