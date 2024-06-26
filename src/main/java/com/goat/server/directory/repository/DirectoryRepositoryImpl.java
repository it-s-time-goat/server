package com.goat.server.directory.repository;

import static com.goat.server.directory.application.type.SortType.DIRECTORY_SORT_MAP;
import static com.goat.server.directory.domain.QDirectory.directory;

import com.goat.server.directory.application.type.SortType;
import com.goat.server.directory.domain.Directory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class DirectoryRepositoryImpl implements DirectoryRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Directory> findAllByParentDirectoryId(Long parentDirectoryId, List<SortType> sort) {
        return query
                .selectFrom(directory)
                .where(directory.parentDirectory.id.eq(parentDirectoryId))
                .orderBy(sortExpression(sort))
                .fetch();
    }

    @Override
    public List<Directory> findAllByUserIdAndParentDirectoryIsNull(Long userId, List<SortType> sort) {
        return query
                .selectFrom(directory)
                .where(directory.parentDirectory.id.isNull(), directory.user.userId.eq(userId))
                .orderBy(sortExpression(sort))
                .fetch();
    }

    @Override
    public List<Directory> findAllBySearch(Long userId, String search, List<SortType> sort) {
        return query
                .selectFrom(directory)
                .where(directory.user.userId.eq(userId), searchExpression(search))
                .orderBy(sortExpression(sort))
                .fetch();
    }

    private BooleanExpression searchExpression(String search) {
        if (ObjectUtils.isEmpty(search)) {
            return null;
        }
        return directory.title.contains(search);
    }

    // 기본 정렬 - 이름 오름차순
    private OrderSpecifier<?>[] sortExpression(List<SortType> sort) {
        if (sort == null || sort.isEmpty()) {
            return new OrderSpecifier[]{directory.title.asc()};
        }
        return sort.stream()
                .map(sortType -> DIRECTORY_SORT_MAP.getOrDefault(sortType, dir -> dir.title.asc()).apply(directory))
                .toArray(OrderSpecifier[]::new);
    }
}
