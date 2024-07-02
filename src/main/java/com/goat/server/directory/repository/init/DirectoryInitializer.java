package com.goat.server.directory.repository.init;

import com.goat.server.global.util.LocalDummyDataInit;
import com.goat.server.directory.domain.Directory;
import com.goat.server.directory.repository.DirectoryRepository;
import com.goat.server.mypage.domain.User;
import com.goat.server.mypage.exception.UserNotFoundException;
import com.goat.server.mypage.exception.errorcode.MypageErrorCode;
import com.goat.server.mypage.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;

@Slf4j
@RequiredArgsConstructor
@LocalDummyDataInit
@Order(2)
public class DirectoryInitializer implements ApplicationRunner {

    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (directoryRepository.count() > 0) {
            log.info("[Directory]더미 데이터 존재");
        } else {
            User admin = userRepository.findByEmail("adminEmail")
                    .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));
            User user = userRepository.findByEmail("userEmail")
                    .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));
            User guest = userRepository.findByEmail("guestEmail")
                    .orElseThrow(() -> new UserNotFoundException(MypageErrorCode.USER_NOT_FOUND));

            List<Directory> directoryList = new ArrayList<>();

            Directory DUMMY_TRASH_DIRECTORY1 = Directory.builder()
                    .title("trash_directory")
                    .directoryColor("#FF00FF")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(admin)
                    .build();
            Directory DUMMY_TRASH_DIRECTORY2 = Directory.builder()
                    .title("trash_directory")
                    .directoryColor("#FF00FF")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(user)
                    .build();
            Directory DUMMY_TRASH_DIRECTORY3 = Directory.builder()
                    .title("trash_directory")
                    .directoryColor("#FF00FF")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(guest)
                    .build();

            Directory DUMMY_PARENT_DIRECTORY1 = Directory.builder()
                    .title("dummyDirectory1")
                    .directoryColor("#FF00FF")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(user)
                    .build();
            Directory DUMMY_PARENT_DIRECTORY2 = Directory.builder()
                    .title("dummyDirectory2")
                    .directoryColor("#FF00FF")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(user)
                    .build();
            Directory DUMMY_PARENT_DIRECTORY3 = Directory.builder()
                    .title("dummyDirectory3")
                    .directoryColor("#FF000F")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(admin)
                    .build();

            Directory DUMMY_CHILD_DIRECTORY1 = Directory.builder()
                    .title("dummyDirectory4")
                    .directoryColor("#FF00FF")
                    .depth(2L)
                    .parentDirectory(DUMMY_PARENT_DIRECTORY1)
                    .user(user)
                    .build();
            Directory DUMMY_CHILD_DIRECTORY2 = Directory.builder()
                    .title("dummyDirectory5")
                    .directoryColor("#FF00FF")
                    .depth(2L)
                    .parentDirectory(DUMMY_PARENT_DIRECTORY1)
                    .user(user)
                    .build();
            Directory DUMMY_CHILD_DIRECTORY3 = Directory.builder()
                    .title("dummyDirectory6")
                    .directoryColor("#FF00FF")
                    .depth(2L)
                    .parentDirectory(DUMMY_CHILD_DIRECTORY2)
                    .user(user)
                    .build();

            Directory DUMMY_STORAGE_DIRECTORY1 = Directory.builder()
                    .title("storage_directory")
                    .directoryColor("#FF00FF")
                    .depth(1L)
                    .parentDirectory(null)
                    .user(user)
                    .build();

            directoryList.add(DUMMY_TRASH_DIRECTORY1);
            directoryList.add(DUMMY_TRASH_DIRECTORY2);
            directoryList.add(DUMMY_TRASH_DIRECTORY3);
            directoryList.add(DUMMY_PARENT_DIRECTORY1);
            directoryList.add(DUMMY_PARENT_DIRECTORY2);
            directoryList.add(DUMMY_PARENT_DIRECTORY3);
            directoryList.add(DUMMY_CHILD_DIRECTORY1);
            directoryList.add(DUMMY_CHILD_DIRECTORY2);
            directoryList.add(DUMMY_CHILD_DIRECTORY3);

            directoryList.add(DUMMY_STORAGE_DIRECTORY1);

            directoryRepository.saveAll(directoryList);
        }
    }
}
