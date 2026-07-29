package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

public interface ChannelService {


  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  ChannelResponse create(PublicChannelCreateRequest request);

  ChannelResponse create(PrivateChannelCreateRequest request);

  ChannelResponse find(UUID channelId);

  List<ChannelResponse> findAllByUserId(UUID userId);

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request);

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  void delete(UUID channelId);
}