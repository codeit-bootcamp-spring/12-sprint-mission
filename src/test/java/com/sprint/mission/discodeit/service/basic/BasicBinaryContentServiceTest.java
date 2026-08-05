package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @InjectMocks
  private BasicBinaryContentService binaryContentService;

  private UUID contentId;
  private BinaryContent binaryContent;
  private BinaryContentDto binaryContentDto;

  @BeforeEach
  void setUp() {
    contentId = UuidCreator.getTimeOrderedEpoch();
    binaryContent = new BinaryContent("test.png", 100L, "image/png");
    ReflectionTestUtils.setField(binaryContent, "id", contentId);
    binaryContentDto = new BinaryContentDto(contentId, "test.png", 100L, "image/png");
  }

  @Test
  @DisplayName("바이너리 컨텐츠 생성 성공")
  void create() {
    // given
    byte[] bytes = "data".getBytes();
    BinaryContentCreateRequest request =
        new BinaryContentCreateRequest("test.png", "image/png", bytes);
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
    given(binaryContentMapper.toDto(binaryContent)).willReturn(binaryContentDto);

    // when
    BinaryContentDto result = binaryContentService.create(request);

    // then
    assertThat(result).isEqualTo(binaryContentDto);
    verify(binaryContentStorage).put(contentId, bytes);
  }

  @Test
  @DisplayName("존재하지 않는 바이너리 컨텐츠 조회 시 예외")
  void find_NotFound() {
    // given
    given(binaryContentRepository.findById(contentId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> binaryContentService.find(contentId))
        .isInstanceOf(BinaryContentNotFoundException.class);
  }


}