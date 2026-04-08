package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService() { channelRepository = new FileChannelRepository(); }

    @Override
    public UUID create(Channel channel) {
        channelRepository.save(channel);
        return channel.getId();
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public Optional<List<Channel>> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void updateById(UUID id, String channelName, String description, User owner) {
        channelRepository.findById(id).ifPresentOrElse(
                channel -> {
                    channel.update(channelName, description, owner);
                    channelRepository.save(channel);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void addUser(UUID id, User user) {
        channelRepository.findById(id).ifPresentOrElse(
                channel -> {
                    channel.addUser(user);
                    channelRepository.save(channel);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteUser(UUID id, User user) {
        channelRepository.findById(id).ifPresentOrElse(
                channel -> {
                    channel.deleteUser(user);
                    channelRepository.save(channel);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void addMessage(UUID id, Message message) {
        channelRepository.findById(id).ifPresentOrElse(
                channel -> {
                    channel.addMessage(message);
                    channelRepository.save(channel);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteMessage(UUID id, Message message) {
        channelRepository.findById(id).ifPresentOrElse(
                channel -> {
                    channel.deleteMessage(message);
                    channelRepository.save(channel);
                },
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        channelRepository.deleteById(id);
    }
}
