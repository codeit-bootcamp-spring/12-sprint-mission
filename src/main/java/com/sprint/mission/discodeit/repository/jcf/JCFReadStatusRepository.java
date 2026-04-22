package com.sprint.mission.discodeit.repository.jcf;

import java.util.*;
import com.sprint.mission.discodeit.entity.channel.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
        private final Map<UUID, ReadStatus> data;

        public JCFReadStatusRepository() {
            this.data = new HashMap<>();
        }

        @Override
        public ReadStatus save(ReadStatus readStatus) {
            data.put(readStatus.getId(), readStatus);
            return readStatus;
        }

        @Override
        public Optional<ReadStatus> findById(UUID id) {
            return Optional.ofNullable(data.get(id));
        }

        @Override
        public List<ReadStatus> findAllByUserId(UUID userId) {
            List<ReadStatus> result = new ArrayList<>();

            for (ReadStatus readStatus : data.values()) {
                if (readStatus.getUserId().equals(userId)) {
                    result.add(readStatus);
                }
            }

            return result;
        }

        @Override
        public List<ReadStatus> findAllByChannelId(UUID channelId) {
            List<ReadStatus> result = new ArrayList<>();

            for (ReadStatus readStatus : data.values()) {
                if (readStatus.getChannelId().equals(channelId)) {
                    result.add(readStatus);
                }
            }

            return result;
        }

        @Override
        public List<ReadStatus> findAll() {
            return new ArrayList<>(data.values());
        }

        @Override
        public ReadStatus deleteById(UUID id) {
            ReadStatus removed = data.remove(id);

            if (removed == null) {
                throw new IllegalArgumentException("해당 id를 가진 ReadStatus 없음.");
            }

            return removed;
        }

        @Override
        public ReadStatus deleteByUserId(UUID userId) {
            List<UUID> targetIds = new ArrayList<>();
            ReadStatus firstRemoved = null;

            for (ReadStatus readStatus : data.values()) {
                if (readStatus.getUserId().equals(userId)) {
                    targetIds.add(readStatus.getId());

                    if (firstRemoved == null) {
                        firstRemoved = readStatus;
                    }
                }
            }

            if (firstRemoved == null) {
                throw new IllegalArgumentException("해당 userId를 가진 ReadStatus 없음.");
            }

            for (UUID id : targetIds) {
                data.remove(id);
            }

            return firstRemoved;
        }

        @Override
        public ReadStatus deleteByChannelId(UUID channelId) {
            List<UUID> targetIds = new ArrayList<>();
            ReadStatus firstRemoved = null;

            for (ReadStatus readStatus : data.values()) {
                if (readStatus.getChannelId().equals(channelId)) {
                    targetIds.add(readStatus.getId());

                    if (firstRemoved == null) {
                        firstRemoved = readStatus;
                    }
                }
            }

            if (firstRemoved == null) {
                throw new IllegalArgumentException("해당 channelId를 가진 ReadStatus 없음.");
            }

            for (UUID id : targetIds) {
                data.remove(id);
            }

            return firstRemoved;
        }
    }