package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct: 컴파일 타임에 구현체를 자동 생성 → 런타임 리플렉션 없이 동작
// componentModel="spring" → 생성된 구현체를 Spring Bean으로 등록
@Mapper(componentModel = "spring")
public interface BinaryContentMapper {

  // bytes는 entity에 없으므로 ignore → null로 세팅 (다운로드 API에서만 채워짐)
  @Mapping(target = "bytes", ignore = true)
  BinaryContentDto toDto(BinaryContent binaryContent);
}
