package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("바이너리 콘텐츠 생성 성공")
    void create_success() {
        // given
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "file-data".getBytes(),
                "test.txt",
                "text/plain"
        );

        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContent savedBinaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContentResponse expectedResponse = new BinaryContentResponse(
                binaryContentId,
                "test.txt",
                9L,
                "text/plain"
        );

        given(binaryContentMapper.toEntity(request)).willReturn(binaryContent);
        given(binaryContentRepository.save(binaryContent)).willReturn(savedBinaryContent);
        given(binaryContentStorage.put(binaryContentId, request.data())).willReturn(binaryContentId);
        given(binaryContentMapper.toResponse(savedBinaryContent)).willReturn(expectedResponse);


        // when
        BinaryContentResponse result = binaryContentService.create(request);


        // then
        assertThat(result).isEqualTo(expectedResponse);

        then(binaryContentMapper).should().toEntity(request);
        then(binaryContentRepository).should().save(binaryContent);
        then(binaryContentStorage).should().put(binaryContentId, request.data());
        then(binaryContentMapper).should().toResponse(savedBinaryContent);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 엔티티 생성 성공")
    void createBinaryContent_success() {
        // given
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "file-data".getBytes(),
                "test.txt",
                "text/plain"
        );

        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContent savedBinaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        given(binaryContentMapper.toEntity(request)).willReturn(binaryContent);
        given(binaryContentRepository.save(binaryContent)).willReturn(savedBinaryContent);
        given(binaryContentStorage.put(binaryContentId, request.data())).willReturn(binaryContentId);


        // when
        BinaryContent result = binaryContentService.createBinaryContent(request);


        // then
        assertThat(result).isEqualTo(savedBinaryContent);

        then(binaryContentMapper).should().toEntity(request);
        then(binaryContentRepository).should().save(binaryContent);
        then(binaryContentStorage).should().put(binaryContentId, request.data());
    }

    @Test
    @DisplayName("바이너리 콘텐츠 단건 조회 성공")
    void findById_success() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        BinaryContentResponse expectedResponse = new BinaryContentResponse(
                binaryContentId,
                "test.txt",
                9L,
                "text/plain"
        );

        given(binaryContentRepository.findById(binaryContentId))
                .willReturn(Optional.of(binaryContent));
        given(binaryContentMapper.toResponse(binaryContent))
                .willReturn(expectedResponse);


        // when
        BinaryContentResponse result = binaryContentService.findById(binaryContentId);


        // then
        assertThat(result).isEqualTo(expectedResponse);

        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentMapper).should().toResponse(binaryContent);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 단건 조회 실패 - 없음")
    void findById_fail_notFound() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        given(binaryContentRepository.findById(binaryContentId))
                .willReturn(Optional.empty());


        // when & then
        assertThatThrownBy(() -> binaryContentService.findById(binaryContentId))
                .isInstanceOf(BinaryContentNotFoundException.class);

        then(binaryContentRepository).should().findById(binaryContentId);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 목록 조회 성공")
    void findAllByIdIn_success() {
        // given
        UUID binaryContentId1 = UUID.randomUUID();
        UUID binaryContentId2 = UUID.randomUUID();

        BinaryContent binaryContent1 = BinaryContent.builder()
                .id(binaryContentId1)
                .fileName("a.txt")
                .size(1L)
                .contentType("text/plain")
                .build();

        BinaryContent binaryContent2 = BinaryContent.builder()
                .id(binaryContentId2)
                .fileName("b.txt")
                .size(1L)
                .contentType("text/plain")
                .build();

        BinaryContentResponse response1 = new BinaryContentResponse(
                binaryContentId1,
                "a.txt",
                1L,
                "text/plain"
        );

        BinaryContentResponse response2 = new BinaryContentResponse(
                binaryContentId2,
                "b.txt",
                1L,
                "text/plain"
        );

        List<UUID> ids = List.of(binaryContentId1, binaryContentId2);

        given(binaryContentRepository.findAllById(ids))
                .willReturn(List.of(binaryContent1, binaryContent2));
        given(binaryContentMapper.toResponse(binaryContent1)).willReturn(response1);
        given(binaryContentMapper.toResponse(binaryContent2)).willReturn(response2);


        // when
        List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(ids);


        // then
        assertThat(result).containsExactly(response1, response2);

        then(binaryContentRepository).should().findAllById(ids);
        then(binaryContentMapper).should().toResponse(binaryContent1);
        then(binaryContentMapper).should().toResponse(binaryContent2);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 삭제 성공")
    void delete_success() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        BinaryContent binaryContent = BinaryContent.builder()
                .id(binaryContentId)
                .fileName("test.txt")
                .size(9L)
                .contentType("text/plain")
                .build();

        given(binaryContentRepository.findById(binaryContentId))
                .willReturn(Optional.of(binaryContent));


        // when
        binaryContentService.delete(binaryContentId);


        // then
        then(binaryContentRepository).should().findById(binaryContentId);
        then(binaryContentRepository).should().delete(binaryContent);
        then(binaryContentStorage).should().delete(binaryContentId);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 삭제 실패 - 없음")
    void delete_fail_notFound() {
        // given
        UUID binaryContentId = UUID.randomUUID();

        given(binaryContentRepository.findById(binaryContentId))
                .willReturn(Optional.empty());


        // when & then
        assertThatThrownBy(() -> binaryContentService.delete(binaryContentId))
                .isInstanceOf(BinaryContentNotFoundException.class);

        then(binaryContentRepository).should().findById(binaryContentId);
    }
}
