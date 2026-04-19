package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPublic(ChannelCreatePublicRequest request) {
        Channel channel = new Channel(
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivate(ChannelCreatePrivateRequest request) {
        Channel channel = new Channel(null, null);

        for (UUID userId : request.getUserIds()) {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("존재하지 않는 유저입니다: " + userId);
            }
            channel.addUser(user);
        }

        channelRepository.save(channel);

        for (UUID userId : request.getUserIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.save(readStatus);
        }

        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, Channel channel) {

        Channel target = channelRepository.findById(id);

        if (target == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        boolean isPrivate = target.getName() == null && target.getDescription() == null;

        if (isPrivate) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        target.update(channel.getName(), channel.getDescription());

        channelRepository.save(target);
        return target;
    }

    @Override
    public void delete(UUID id) {

        // 채널 존재 여부 확인
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
        }

        // 해당 채널 관련 ReadStatus 삭제
        for (User user : channel.getUsers()) {
            ReadStatus readStatus =
                    readStatusRepository.findByUserIdAndChannelId(user.getId(), id);

            if (readStatus != null) {
                readStatusRepository.delete(readStatus.getId());
            }
        }

        // 채널 삭제
        channelRepository.delete(id);
    }
    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> {
                    boolean isPrivate = channel.getName() == null && channel.getDescription() == null;

                    if (!isPrivate) {
                        return true;
                    }

                    return channel.getUsers().stream()
                            .anyMatch(user -> user.getId().equals(userId));
                })
                .toList();
    }
}