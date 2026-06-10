package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.channel.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@Import({GlobalExceptionHandler.class, MDCLoggingInterceptor.class})
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublic_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "public-channel",
                "public-description"
        );

        ChannelResponse response = new ChannelResponse(
                channelId,
                ChannelType.PUBLIC,
                "public-channel",
                "public-description",
                List.of(),
                null
        );

        when(channelService.createPublicChannel(eq(request))).thenReturn(response);

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("public-channel"))
                .andExpect(jsonPath("$.description").value("public-description"));

        verify(channelService).createPublicChannel(eq(request));
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 실패 - 요청 값 검증 실패")
    void createPublic_fail_invalidRequest() throws Exception {
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "",
                "public-description"
        );

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 성공")
    void createPrivate_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of(userId1, userId2)
        );

        UserResponse participant1 = new UserResponse(
                userId1,
                "user1",
                "user1@test.com",
                null,
                true
        );

        UserResponse participant2 = new UserResponse(
                userId2,
                "user2",
                "user2@test.com",
                null,
                false
        );

        ChannelResponse response = new ChannelResponse(
                channelId,
                ChannelType.PRIVATE,
                null,
                null,
                List.of(participant1, participant2),
                null
        );

        when(channelService.createPrivateChannel(eq(request))).thenReturn(response);

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.participants[0].id").value(userId1.toString()))
                .andExpect(jsonPath("$.participants[0].username").value("user1"))
                .andExpect(jsonPath("$.participants[1].id").value(userId2.toString()))
                .andExpect(jsonPath("$.participants[1].username").value("user2"));

        verify(channelService).createPrivateChannel(eq(request));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 실패 - 참여자 없음")
    void createPrivate_fail_invalidRequest() throws Exception {
        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(
                List.of()
        );

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("채널 수정 성공")
    void update_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest(
                "new-name",
                "new-description"
        );

        ChannelResponse response = new ChannelResponse(
                channelId,
                ChannelType.PUBLIC,
                "new-name",
                "new-description",
                List.of(),
                Instant.now()
        );

        when(channelService.update(eq(channelId), eq(request))).thenReturn(response);

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("new-name"))
                .andExpect(jsonPath("$.description").value("new-description"));

        verify(channelService).update(eq(channelId), eq(request));
    }

    @Test
    @DisplayName("채널 수정 실패 - PRIVATE 채널 수정 불가")
    void update_fail_privateChannel() throws Exception {
        UUID channelId = UUID.randomUUID();

        ChannelUpdateRequest request = new ChannelUpdateRequest(
                "new-name",
                "new-description"
        );

        when(channelService.update(eq(channelId), eq(request)))
                .thenThrow(new PrivateChannelUpdateNotAllowedException(channelId));

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.exceptionType").value("PrivateChannelUpdateNotAllowedException"));

        verify(channelService).update(eq(channelId), eq(request));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        verify(channelService).delete(channelId);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 채널 없음")
    void delete_fail_channelNotFound() throws Exception {
        UUID channelId = UUID.randomUUID();

        org.mockito.Mockito.doThrow(new ChannelNotFoundException(channelId))
                .when(channelService)
                .delete(channelId);

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.exceptionType").value("ChannelNotFoundException"));

        verify(channelService).delete(channelId);
    }

    @Test
    @DisplayName("사용자 ID로 채널 목록 조회 성공")
    void findAllByUserId_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        ChannelResponse response = new ChannelResponse(
                channelId,
                ChannelType.PUBLIC,
                "public-channel",
                "public-description",
                List.of(),
                null
        );

        when(channelService.findAllByUserId(userId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(channelId.toString()))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"))
                .andExpect(jsonPath("$[0].name").value("public-channel"))
                .andExpect(jsonPath("$[0].description").value("public-description"));

        verify(channelService).findAllByUserId(userId);
    }

    @Test
    @DisplayName("사용자 ID로 채널 목록 조회 실패 - userId 파라미터 누락")
    void findAllByUserId_fail_missingUserId() throws Exception {
        mockMvc.perform(get("/api/channels"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.status").value(500));
    }
}