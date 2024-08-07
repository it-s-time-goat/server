package com.goat.server.directory.fixture;

import com.goat.server.directory.domain.Directory;
import com.goat.server.directory.domain.type.DirectoryColor;
import com.goat.server.directory.domain.type.DirectoryIcon;
import com.goat.server.mypage.fixture.UserFixture;
import org.springframework.test.util.ReflectionTestUtils;

public class DirectoryFixture {

    public static final Directory TRASH_DIRECTORY = Directory.builder()
            .title("trash_directory")
            .directoryColor(DirectoryColor.BLUE)
            .directoryIcon(DirectoryIcon.BOOK)
            .depth(1L)
            .parentDirectory(null)
            .user(UserFixture.USER_USER)
            .build();

    public static final Directory PARENT_DIRECTORY1 = Directory.builder()
            .title("directory1")
            .directoryColor(DirectoryColor.BLUE)
            .directoryIcon(DirectoryIcon.BOOK)
            .parentDirectory(null)
            .depth(1L)
            .user(UserFixture.USER_USER)
            .build();
    public static final Directory PARENT_DIRECTORY2 = Directory.builder()
            .title("directory2")
            .directoryColor(DirectoryColor.BLUE)
            .directoryIcon(DirectoryIcon.BOOK)
            .parentDirectory(null)
            .depth(1L)
            .user(UserFixture.USER_USER)
            .build();

    public static final Directory CHILD_DIRECTORY1 = Directory.builder()
            .title("directory3")
            .directoryColor(DirectoryColor.BLUE)
            .directoryIcon(DirectoryIcon.BOOK)
            .parentDirectory(PARENT_DIRECTORY1)
            .depth(2L)
            .user(UserFixture.USER_USER)
            .build();
    public static final Directory CHILD_DIRECTORY2 = Directory.builder()
            .title("directory3")
            .directoryColor(DirectoryColor.BLUE)
            .directoryIcon(DirectoryIcon.BOOK)
            .parentDirectory(PARENT_DIRECTORY1)
            .depth(2L)
            .user(UserFixture.USER_USER)
            .build();

    static {
        ReflectionTestUtils.setField(TRASH_DIRECTORY, "id", 1L);
        ReflectionTestUtils.setField(PARENT_DIRECTORY1, "id", 2L);
        ReflectionTestUtils.setField(PARENT_DIRECTORY2, "id", 3L);
        ReflectionTestUtils.setField(CHILD_DIRECTORY1, "id", 4L);
        ReflectionTestUtils.setField(CHILD_DIRECTORY2, "id", 5L);
    }
}
