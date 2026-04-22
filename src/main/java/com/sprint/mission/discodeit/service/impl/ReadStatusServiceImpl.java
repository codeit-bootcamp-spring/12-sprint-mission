package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
// 1. request에서 userId, channelId 꺼내기
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        // 2. userId로 사용자 존재 여부 확인
        //    없으면 예외
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
        // 3. channelId로 채널 존재 여부 확인
        //    없으면 예외
        userRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("채널이 없습니다."));

        // 4. 같은 channelId + userId 조합의 ReadStatus가 이미 있는지 검사
        //    있으면 예외
        boolean exists = readStatusRepository.findAllByUserId(userId)
                .stream()
                .anyMatch(r -> r.getChannelId().equals(channelId));

        if (exists) {
            throw new IllegalArgumentException("이미 해당 사용자와 채널의 읽음 상태가 존재합니다.");
        }
        // 5. ReadStatus 생성
        ReadStatus readStatus = ReadStatus.builder()
                .userId(userId)
                .channelId(channelId)
                .build();

        // 6. 저장
        readStatusRepository.save(readStatus);

        // 7. ReadStatusDto로 반환

        return ReadStatusDto.from(readStatus);
    }

    @Override
    public ReadStatusDto findById(UUID id) {
// 1. id로 ReadStatus 조회
        //    없으면 예외
        ReadStatus readStatus = readStatusRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2. DTO로 반환
        return ReadStatusDto.from(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
//        userId로 모든 ReadStatus 목록 조회
//        DTO 리스트로 반환
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusDto::from)
                .toList();
    }


    @Override
    public ReadStatusDto update(ReadStatusUpdateRequest request) {
//        TO(그룹화)로 파라미터 전달
//        id (수정할 객체), 변경 값(예시: lastReadAt 등)
//        id로 ReadStatus 찾기/없으면 예외
//        값 변경 및 save(혹은 update)로 저장
//        변경 결과 DTO로 반환
        return null;
    }

    @Override
    public void delete(UUID id) {
//        id로 존재하지 않으면 예외
//        해당 id의 ReadStatus 삭제

    }
}
