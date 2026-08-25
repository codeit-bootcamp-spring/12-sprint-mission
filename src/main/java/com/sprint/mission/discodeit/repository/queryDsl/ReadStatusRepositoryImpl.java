package com.sprint.mission.discodeit.repository.queryDsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReadStatusRepositoryImpl implements ReadStatusQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QReadStatus r = QReadStatus.readStatus;
    private static final QUser u = QUser.user;
    private static final QBinaryContent bc = QBinaryContent.binaryContent;

    @Override
    public List<ReadStatus> findAllByChannelIdWithUser(UUID channelId) {
        return queryFactory
                .selectFrom(r)
                .join(r.user, u)
                .fetchJoin()
                .leftJoin(u.profile, bc)
                .fetchJoin()
                .where(r.channel.id.eq(channelId))
                .fetch();
    }
}
