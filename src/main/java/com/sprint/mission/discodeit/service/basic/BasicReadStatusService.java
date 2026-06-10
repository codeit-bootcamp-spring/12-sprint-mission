package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    log.info("ReadStatus 생성 요청: userId={}, channelId={}", request.userId(), request.channelId());
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("ReadStatus 생성 실패 - {}: userId={}", ErrorCode.USER_NOT_FOUND.getMessage(),
              userId);
          return new UserNotFoundException();
        });

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> {
          log.warn("ReadStatus 생성 실패 - {}: channelId={}", ErrorCode.CHANNEL_NOT_FOUND.getMessage(),
              channelId);
          return new ChannelNotFoundException();
        });

    if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
      log.warn("ReadStatus 생성 실패 - {}: userId={}, channelId={}",
          ErrorCode.DUPLICATE_READ_STATUS.getMessage(), userId, channelId);
      throw new DuplicateReadStatusException();
    }

    ReadStatus readStatus = readStatusRepository.save(
        new ReadStatus(user, channel, request.lastReadAt()));
    log.info("ReadStatus 생성 완료: id={}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> {
          log.warn("ReadStatus 조회 실패 - {}: id={}", ErrorCode.READ_STATUS_NOT_FOUND.getMessage(),
              readStatusId);
          return new ReadStatusNotFoundException();
        });
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.info("ReadStatus 수정 요청: id={}", readStatusId);
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("ReadStatus 수정 실패 - {}: id={}", ErrorCode.READ_STATUS_NOT_FOUND.getMessage(),
              readStatusId);
          return new ReadStatusNotFoundException();
        });

    readStatus.update(newLastReadAt);
    log.info("ReadStatus 수정 완료: id={}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.info("ReadStatus 삭제 요청: id={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      log.warn("ReadStatus 삭제 실패 - {}: id={}", ErrorCode.READ_STATUS_NOT_FOUND.getMessage(),
          readStatusId);
      throw new ReadStatusNotFoundException();
    }

    log.info("ReadStatus 삭제 완료: id={}", readStatusId);
    readStatusRepository.deleteById(readStatusId);
  }
}