package com.sprint.mission.discodeit.repository.queryDsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.QBinaryContent;
import com.sprint.mission.discodeit.entity.QUser;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sprint.mission.discodeit.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QUser u = QUser.user;
    private static final QBinaryContent bc = QBinaryContent.binaryContent;

    @Override
    public List<User> findAllWithProfile() {
        return queryFactory
                .selectFrom(u)
                .leftJoin(user.profile, bc)
                .fetchJoin()
                .fetch();
    }
}
