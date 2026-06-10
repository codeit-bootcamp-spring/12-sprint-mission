package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    log.debug("ReadStatus 생성 시작: request={}", request);
    UUID userId = request.userId();
    UUID channelId = request.channelId();
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> {
              log.warn("ReadStatus 생성 실패(사용자 없음): userId={}", userId);
              return UserNotFoundException.withId(userId);
            });
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("ReadStatus 생성 실패(채널 없음): channelId={}", channelId);
              return ChannelNotFoundException.withId(channelId);
            });
    if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
      log.warn("ReadStatus 생성 실패(이미 존재): userId={}, channelId={}", userId, channelId);
      throw ReadStatusAlreadyExistException.withUserIdAndChannelId(userId, channelId);
    }

    ReadStatus newReadStatus = ReadStatus.builder()
        .user(user)
        .channel(channel)
        .lastReadAt(request.lastReadAt())
        .build();

    ReadStatus saved = readStatusRepository.save(newReadStatus);
    log.info("ReadStatus 생성 성공: userId={}, channelId={}", userId, channelId);
    return readStatusMapper.toDto(saved);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.debug("ReadStatus 조회 시작: readStatusId={}", readStatusId);
    ReadStatusDto readStatusDto = readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(
            () -> {
              log.warn("ReadStatus 조회 실패(ReadStatus 없음): readStatusId={}", readStatusId);
              return ReadStatusNotFoundException.withId(readStatusId);
            });
    log.info("ReadStatus 조회 성공: readStatusId={}", readStatusId);
    return readStatusDto;
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("사용자의 ReadStatus 조회 시작: userId={}", userId);
    List<ReadStatusDto> readStatusDtos = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
    log.info("사용자의 ReadStatus 조회 성공: userId={}, count={}", userId, readStatusDtos.size());
    return readStatusDtos;
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.debug("ReadStatus 업데이트 시작: readStatusId={}, request={}", readStatusId, request);
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> {
              log.warn("ReadStatus 업데이트 실패(ReadStatus 없음): readStatusId={}", readStatusId);
              return ReadStatusNotFoundException.withId(readStatusId);
            });
    readStatus.update(newLastReadAt);
    readStatusRepository.save(readStatus);
    log.info("ReadStatus 업데이트 성공: readStatusId={}, newLastReadAt={}", readStatusId, newLastReadAt);
    return readStatusMapper.toDto(readStatus);
  }

  @Override
  @Transactional
  public void delete(UUID readStatusId) {
    log.debug("ReadStatus 삭제 시작: readStatusId={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      log.warn("ReadStatus 삭제 실패(ReadStatus 없음): readStatusId={}", readStatusId);
      throw ReadStatusNotFoundException.withId(readStatusId);
    }
    readStatusRepository.deleteById(readStatusId);
    log.info("ReadStatus 삭제 성공: readStatusId={}", readStatusId);
  }
}
