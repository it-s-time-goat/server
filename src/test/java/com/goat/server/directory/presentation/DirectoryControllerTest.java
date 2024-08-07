package com.goat.server.directory.presentation;

import static com.goat.server.directory.fixture.DirectoryFixture.CHILD_DIRECTORY1;
import static com.goat.server.directory.fixture.DirectoryFixture.CHILD_DIRECTORY2;
import static com.goat.server.directory.fixture.DirectoryFixture.PARENT_DIRECTORY1;
import static com.goat.server.directory.fixture.DirectoryFixture.PARENT_DIRECTORY2;
import static com.goat.server.review.fixture.ReviewFixture.DUMMY_REVIEW1;
import static com.goat.server.review.fixture.ReviewFixture.DUMMY_REVIEW2;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goat.server.directory.dto.request.DirectoryInitRequest;
import com.goat.server.directory.dto.request.DirectoryMoveRequest;
import com.goat.server.directory.dto.response.DirectoryResponse;
import com.goat.server.directory.dto.response.DirectoryTotalShowResponse;
import com.goat.server.global.CommonControllerTest;
import com.goat.server.directory.application.DirectoryService;
import com.goat.server.review.dto.response.ReviewSimpleResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@WebMvcTest(DirectoryController.class)
class DirectoryControllerTest extends CommonControllerTest {

    @MockBean
    private DirectoryService directoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("과목, 폴더 정보 가져 오기")
    void getDirectoryListTest() throws Exception {
        //given
        List<DirectoryResponse> directoryResponseList =
                List.of(DirectoryResponse.from(CHILD_DIRECTORY1), DirectoryResponse.from(CHILD_DIRECTORY2));
        List<ReviewSimpleResponse> reviewSimpleResponseList =
                List.of(ReviewSimpleResponse.from(DUMMY_REVIEW1), ReviewSimpleResponse.from(DUMMY_REVIEW2));
        DirectoryTotalShowResponse directoryTotalShowResponse =
                DirectoryTotalShowResponse.of(PARENT_DIRECTORY1.getId(), directoryResponseList,
                        reviewSimpleResponseList);

        given(directoryService.getDirectorySubList(anyLong(), eq(PARENT_DIRECTORY1.getId()), any(), any()))
                .willReturn(directoryTotalShowResponse);

        log.info("directoryTotalShowResponse: {}", directoryTotalShowResponse);

        //when
        ResultActions resultActions =
                mockMvc.perform(get("/goat/directory?directoryId=" + PARENT_DIRECTORY1.getId()))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.directoryResponseList[0].directoryId")
                        .value(CHILD_DIRECTORY1.getId()))
                .andExpect(jsonPath("$.results.directoryResponseList[1].directoryId")
                        .value(CHILD_DIRECTORY2.getId()))
                .andExpect(jsonPath("$.results.reviewSimpleResponseList[0].reviewId")
                        .value(DUMMY_REVIEW1.getId()))
                .andExpect(jsonPath("$.results.reviewSimpleResponseList[1].reviewId")
                        .value(DUMMY_REVIEW2.getId()));
    }

    @Test
    @DisplayName("폴더 생성")
    void initDirectoryTest() throws Exception {
        //given
        DirectoryInitRequest request =
                new DirectoryInitRequest("폴더 이름", PARENT_DIRECTORY1.getId(), "BLUE", "PENCIL", "설명");

        //when
        ResultActions resultActions =
                mockMvc.perform(post("/goat/directory")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("폴더 임시 삭제")
    void deleteDirectoryTemporalTest() throws Exception {
        //given

        //when
        ResultActions resultActions =
                mockMvc.perform(delete("/goat/directory/temporal/" + PARENT_DIRECTORY1.getId()))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("폴더 임시 삭제")
    void deleteDirectoryPermanentTest() throws Exception {
        //given

        //when
        ResultActions resultActions =
                mockMvc.perform(delete("/goat/directory/permanent/" + PARENT_DIRECTORY1.getId()))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("폴더 이동")
    void moveDirectoryTest() throws Exception {
        //given
        DirectoryMoveRequest request = new DirectoryMoveRequest(PARENT_DIRECTORY1.getId(), PARENT_DIRECTORY2.getId());

        //when
        ResultActions resultActions =
                mockMvc.perform(post("/goat/directory/move")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        //then
        resultActions
                .andExpect(status().isOk());
    }
}