package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

  @Mock BinaryContentRepository binaryContentRepository;
  @Mock BinaryContentMapper binaryContentMapper;
  @Mock ApplicationEventPublisher eventPublisher;

  @InjectMocks BasicBinaryContentService binaryContentService;

  private UUID contentId;
  private BinaryContent binaryContent;
  private BinaryContentDto binaryContentDto;

  @BeforeEach
  void setUp() {
    contentId = UUID.randomUUID();
    binaryContent = new BinaryContent("file.png", 100L, "image/png");
    binaryContentDto = new BinaryContentDto(contentId, "file.png", 100L, "image/png",
        BinaryContentStatus.PROCESSING, null);
  }

  @Test
  @DisplayName("BinaryContent 생성 성공")
  void create_success() {
    byte[] bytes = new byte[]{1, 2, 3};
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
    given(binaryContentMapper.toDto(binaryContent)).willReturn(binaryContentDto);

    BinaryContentDto result = binaryContentService.create(
        new BinaryContentCreateRequest("file.png", "image/png", bytes));

    assertThat(result.fileName()).isEqualTo("file.png");
    // 저장소를 직접 호출하지 않고 이벤트만 발행한다
    ArgumentCaptor<BinaryContentCreatedEvent> captor =
        ArgumentCaptor.forClass(BinaryContentCreatedEvent.class);
    then(eventPublisher).should().publishEvent(captor.capture());
    assertThat(captor.getValue().binaryContentId()).isEqualTo(binaryContent.getId());
    assertThat(captor.getValue().bytes()).isEqualTo(bytes);
  }

  @Test
  @DisplayName("업로드 결과를 status에 반영")
  void updateStatus_success() {
    given(binaryContentRepository.findById(contentId)).willReturn(Optional.of(binaryContent));
    given(binaryContentMapper.toDto(binaryContent)).willReturn(binaryContentDto);

    binaryContentService.updateStatus(contentId, BinaryContentStatus.SUCCESS);

    assertThat(binaryContent.getStatus()).isEqualTo(BinaryContentStatus.SUCCESS);
  }

  @Test
  @DisplayName("존재하지 않는 BinaryContent의 status 변경 시 예외 발생")
  void updateStatus_notFound_throwsException() {
    given(binaryContentRepository.findById(contentId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> binaryContentService.updateStatus(contentId,
        BinaryContentStatus.FAIL))
        .isInstanceOf(BinaryContentNotFoundException.class);
  }

  @Test
  @DisplayName("BinaryContent 단건 조회 성공")
  void find_success() {
    given(binaryContentRepository.findById(contentId)).willReturn(Optional.of(binaryContent));
    given(binaryContentMapper.toDto(binaryContent)).willReturn(binaryContentDto);

    BinaryContentDto result = binaryContentService.find(contentId);

    assertThat(result.id()).isEqualTo(contentId);
  }

  @Test
  @DisplayName("존재하지 않는 BinaryContent 조회 시 예외 발생")
  void find_notFound_throwsException() {
    given(binaryContentRepository.findById(contentId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> binaryContentService.find(contentId))
        .isInstanceOf(BinaryContentNotFoundException.class);
  }

  @Test
  @DisplayName("ID 목록으로 BinaryContent 조회 성공")
  void findAllByIdIn_success() {
    List<UUID> ids = List.of(contentId);
    given(binaryContentRepository.findAllByIdIn(ids)).willReturn(List.of(binaryContent));
    given(binaryContentMapper.toDto(binaryContent)).willReturn(binaryContentDto);

    List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("BinaryContent 삭제 성공")
  void delete_success() {
    given(binaryContentRepository.existsById(contentId)).willReturn(true);

    binaryContentService.delete(contentId);

    then(binaryContentRepository).should().deleteById(contentId);
  }

  @Test
  @DisplayName("존재하지 않는 BinaryContent 삭제 시 예외 발생")
  void delete_notFound_throwsException() {
    given(binaryContentRepository.existsById(contentId)).willReturn(false);

    assertThatThrownBy(() -> binaryContentService.delete(contentId))
        .isInstanceOf(BinaryContentNotFoundException.class);
  }
}
