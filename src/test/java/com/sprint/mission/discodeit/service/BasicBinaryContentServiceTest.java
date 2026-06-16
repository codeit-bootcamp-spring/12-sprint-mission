package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicBinaryContentServiceTest {

  @Mock BinaryContentRepository binaryContentRepository;
  @Mock BinaryContentStorage binaryContentStorage;
  @Mock BinaryContentMapper binaryContentMapper;

  @InjectMocks BasicBinaryContentService binaryContentService;

  private UUID contentId;
  private BinaryContent binaryContent;
  private BinaryContentDto binaryContentDto;

  @BeforeEach
  void setUp() {
    contentId = UUID.randomUUID();
    binaryContent = new BinaryContent("file.png", 100L, "image/png");
    binaryContentDto = new BinaryContentDto(contentId, "file.png", 100L, "image/png", null);
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
    then(binaryContentStorage).should().put(eq(binaryContent.getId()), eq(bytes));
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
