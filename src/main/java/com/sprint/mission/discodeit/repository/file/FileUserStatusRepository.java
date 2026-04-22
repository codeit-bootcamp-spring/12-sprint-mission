package com.sprint.mission.discodeit.repository.file;



import java.util.*;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
    private static final String FILE_PATH  = "UserStatus.ser";

    private final Map<UUID, UserStatus> data;

    public FileUserStatusRepository() {
        this.data = new HashMap<>();

        List<UserStatus> userStatusList = FileSerialization.loadData(FILE_PATH);
        for (UserStatus ch : userStatusList) {
            data.put(ch.getId(), ch);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus userStatus : data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return Optional.of(userStatus);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public UserStatus deleteById(UUID id) {
        UserStatus removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 UserStatus 없음.");
        }

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return removed;
    }

    @Override
    public UserStatus deleteByUserId(UUID userId) {
        UserStatus removed = null;
        for (UserStatus userStatus : data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                removed = data.remove(userStatus.getId());
            }
        }

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 UserStatus 없음.");
        }

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return removed;
    }
}