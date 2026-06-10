package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.PrivateChannelReadStatusForbiddenException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusDuplicateException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatusResponse create(ReadStatusCreateRequest request) {
    getUserOrThrow(request.userId());
    Channel channel = getChannelOrThrow(request.channelId());
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelReadStatusForbiddenException(request.userId(), request.channelId());
    }
    validateReadStatusNotExists(request.userId(), request.channelId());

    User user = getUserOrThrow(request.userId());
    ReadStatus readStatus = new ReadStatus(
        user,
        channel,
        request.lastReadAt()
    );

    ReadStatus savedReadStatus = readStatusRepository.save(readStatus);
    return ReadStatusResponse.from(savedReadStatus);
  }

  @Override
  public ReadStatusResponse find(UUID id) {
    ReadStatus readStatus = getReadStatusOrThrow(id);

    return ReadStatusResponse.from(readStatus);
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    getUserOrThrow(userId);
    List<ReadStatus> readStatuses = readStatusRepository.findByUserId(userId);

    return readStatuses.stream()
        .map(readStatus -> ReadStatusResponse.from(readStatus))
        .toList();
  }

  @Override
  public ReadStatusResponse update(UUID userStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = getReadStatusOrThrow(userStatusId);

    readStatus.changeReadStatus(request.newLastReadAt());
    ReadStatus changedReadStatus = readStatusRepository.save(readStatus);
    return ReadStatusResponse.from(changedReadStatus);
  }

  @Override
  public void delete(UUID id) {
    getReadStatusOrThrow(id);
    readStatusRepository.deleteById(id);
  }

  private ReadStatus getReadStatusOrThrow(UUID id) {
    return readStatusRepository.findById(id).orElseThrow(
        () -> new ReadStatusNotFoundException(id)
    );
  }

  private Channel getChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId).orElseThrow(
        () -> new ChannelNotFoundException(channelId)
    );
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId).orElseThrow(
        () -> new UserNotFoundException(userId)
    );
  }

  private void validateReadStatusNotExists(UUID userId, UUID channelId) {
    readStatusRepository.findByUserIdAndChannelId(userId, channelId)
        .ifPresent(readStatus -> {
          throw ReadStatusDuplicateException.withUserIdAndChannelId(userId, channelId);
        });
  }

}
