package com.goat.server.directory.fixture;

import com.goat.server.directory.domain.Directory;
import com.goat.server.mypage.fixture.UserFixture;
import org.springframework.test.util.ReflectionTestUtils;

public class DirectoryFixture {

    public static final Directory PARENT_DIRECTORY1 = Directory.builder()
            .directoryName("directory1")
            .directoryColor("#FF00FF")
            .parentDirectory(null)
            .user(UserFixture.USER_USER)
            .build();

    public static final Directory PARENT_DIRECTORY2 = Directory.builder()
            .directoryName("directory2")
            .directoryColor("#FF00FF")
            .parentDirectory(null)
            .user(UserFixture.USER_USER)
            .build();

    public static final Directory CHILD_DIRECTORY1 = Directory.builder()
            .directoryName("directory3")
            .directoryColor("#FF00FF")
            .parentDirectory(PARENT_DIRECTORY1)
            .user(UserFixture.USER_USER)
            .build();

    static {
        ReflectionTestUtils.setField(PARENT_DIRECTORY1, "directoryId", 1L);
        ReflectionTestUtils.setField(PARENT_DIRECTORY2, "directoryId", 2L);
        ReflectionTestUtils.setField(CHILD_DIRECTORY1, "directoryId", 3L);
    }
}
