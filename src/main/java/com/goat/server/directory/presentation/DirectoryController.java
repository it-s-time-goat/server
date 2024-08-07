package com.goat.server.directory.presentation;

import com.goat.server.directory.application.type.SortType;
import com.goat.server.directory.dto.request.DirectoryInitRequest;
import com.goat.server.directory.dto.request.DirectoryMoveRequest;
import com.goat.server.directory.dto.response.DirectoryTotalShowResponse;
import com.goat.server.global.dto.ResponseTemplate;
import com.goat.server.directory.application.DirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DirectoryController", description = "DirectoryController 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goat/directory")
public class DirectoryController {

    private final DirectoryService directoryService;

    @Operation(summary = "과목, 폴더 정보 가져 오기",
            description = "directoryId가 0이면 root directory에 속한 폴더, 파일 불러오기. "
                    + "만약 search가 있는 경우 해당 검색어로 검색 - 이때 정렬 조건은 정상적으로 동작하나 directoryId는 무시됨."
                    + "간단히 말하면 전체에서 검색")
    @GetMapping
    public ResponseEntity<ResponseTemplate<Object>> getDirectorySubList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") Long directoryId,
            @RequestParam(required = false) List<SortType> sort,
            @RequestParam(required = false) String search) {

        DirectoryTotalShowResponse directorySubList =
                directoryService.getDirectorySubList(userId, directoryId, sort, search);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.from(directorySubList));
    }

    @Operation(summary = "폴더 생성", description = "폴더 생성")
    @PostMapping
    public ResponseEntity<ResponseTemplate<Object>> initDirectory(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DirectoryInitRequest directoryInitRequest) {

        directoryService.initDirectory(userId, directoryInitRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }

    @Operation(summary = "폴더 임시 삭제", description = "폴더 임시 삭제")
    @DeleteMapping("/temporal/{directoryId}")
    public ResponseEntity<ResponseTemplate<Object>> deleteDirectoryTemporal(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long directoryId) {

        directoryService.deleteDirectoryTemporal(userId, directoryId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }

    @Operation(summary = "폴더 영구 삭제", description = "폴더 영구 삭제")
    @DeleteMapping("/permanent/{directoryId}")
    public ResponseEntity<ResponseTemplate<Object>> deleteDirectoryPermanent(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long directoryId) {

        directoryService.deleteDirectoryPermanent(userId, directoryId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }

    @Operation(summary = "폴더 이동", description = "폴더 이동")
    @PostMapping("/move")
    public ResponseEntity<ResponseTemplate<Object>> moveDirectory(
            @Valid @RequestBody DirectoryMoveRequest directoryMoveRequest) {

        directoryService.moveDirectory(directoryMoveRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseTemplate.EMPTY_RESPONSE);
    }
}
