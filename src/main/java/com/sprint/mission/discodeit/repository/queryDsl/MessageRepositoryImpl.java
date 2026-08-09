package com.sprint.mission.discodeit.repository.queryDsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QMessage m = QMessage.message;
    private static final QUser u= QUser.user;
    private static final QChannel c = QChannel.channel;

    @Override
    public Slice<Message> findAllByChannelIdWithAuthor(
            UUID channelId,
            Instant createdAt,
            Pageable pageable
    ) {
        List<Message> messages = queryFactory
                .selectFrom(m)
                .leftJoin(m.author, u)
                .fetchJoin()
                .leftJoin(m.channel, c)
                .fetchJoin()
                .where(
                        m.channel.id.eq(channelId),
                        m.createdAt.lt(createdAt)
                )
                .orderBy(m.createdAt.desc())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = messages.size() > pageable.getOffset() + pageable.getPageSize();

        if(hasNext){
            messages.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(messages,pageable,hasNext);
    }


    @Override
    public Optional<Instant> findLastMessageAtByChannelId(UUID channelId) {
        Instant lastMessage = queryFactory
                .select(m.createdAt)
                .from(m)
                .where(m.channel.id.eq(channelId))
                .orderBy(m.createdAt.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(lastMessage);
    }
}
