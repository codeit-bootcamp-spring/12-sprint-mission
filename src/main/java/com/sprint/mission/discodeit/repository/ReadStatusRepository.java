package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ReadStatusService 구현
// create
// [ ] DTO를 활용해 파라미터를 그룹화합니다.
// [ ] 관련된 Channel이나 User가 존재하지 않으면 예외를 발생시킵니다.
// [ ] 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
// find
// [ ] id로 조회합니다.
// findAllByUserId
// [ ] userId를 조건으로 조회합니다.
// update
// [ ] DTO를 활용해 파라미터를 그룹화합니다.
// 수정 대상 객체의 id 파라미터, 수정할 값 파라미터
// delete
// [ ] id로 삭제합니다.
public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAll();

    List<ReadStatus> findAllByUserId(UUID id);

    Optional<ReadStatus> update(ReadStatus readStatus);

    ReadStatus delete(UUID id);
}
