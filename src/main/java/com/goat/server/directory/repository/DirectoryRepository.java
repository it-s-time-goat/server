package com.goat.server.directory.repository;

import com.goat.server.directory.domain.Directory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long>, DirectoryRepositoryCustom {

    @Query("SELECT d FROM Directory d WHERE d.user.userId = :userId AND d.title = 'trash_directory'")
    Optional<Directory> findTrashDirectoryByUser(Long userId);

    @Query("SELECT d FROM Directory d WHERE d.user.userId = :userId AND d.title = 'storage_directory'")
    Optional<Directory> findStorageDirectoryByUser(Long userId);
}
