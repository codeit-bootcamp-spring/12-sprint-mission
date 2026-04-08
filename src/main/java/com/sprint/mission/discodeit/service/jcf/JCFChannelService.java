package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() { data = new ArrayList<>(); }

    @Override
    public UUID create(Channel channel) {
        data.add(channel);
        addUser(channel.getId(), channel.getOwner());
        return channel.getId();
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Channel>> findAll() {
        if (!data.isEmpty()) {
            return Optional.of(data);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void updateById(UUID id, String channelName, String description, User owner) {
        findById(id).ifPresentOrElse(
                (channel) -> channel.update(channelName, description, owner),
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void addUser(UUID id, User user) {
        findById(id).ifPresentOrElse(
                (channel) -> channel.addUser(user),
                () -> System.out.println("\t추가실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteUser(UUID id, User user) {
        findById(id).ifPresentOrElse(
                (channel) -> {
                    if (channel.getOwner() == user) {
                        channel.deleteUser(user);
                        List<User> userList = channel.getUserList();
                        if (!userList.isEmpty()) {
                            updateById(id, channel.getChannelName(), channel.getDescription(), userList.get(0));
                            return;
                        } else {
                            deleteById(id);
                            System.out.println("\t삭제성공 : Channel 구성원이 없어 모두 삭제되었습니다");
                            return;
                        }
                    }
                    channel.deleteUser(user);
                },
                () -> System.out.println("\t삭제실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void addMessage(UUID id, Message message) {
        findById(id).ifPresentOrElse(
                (channel) -> channel.addMessage(message),
                () -> System.out.println("\t추가실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteMessage(UUID id, Message message) {
        findById(id).ifPresentOrElse(
                (channel) -> channel.deleteMessage(message),
                () -> System.out.println("삭제실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresentOrElse(
                data::remove,
                () -> System.out.println("삭제실패 : 입력된 id(" + id + ")에 해당하는 Channel이 없습니다")
        );
    }
}
