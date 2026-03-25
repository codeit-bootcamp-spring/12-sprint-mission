package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new ArrayList<>();
        this.userService = userService;
    }

    @Override
    public Channel save(Channel channel) {
        if(userService.findById(channel.getAuthor().getId()) == null){
            System.out.println("Author information is not exist.");
        }
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        for(Channel channel : data){
            if(channel.getId().equals(id)) return channel;
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return data;
    }

    @Override
    public Channel update(Channel channel) {
        for(Channel updateChannel : data){
            if(updateChannel.getId().equals(channel.getId())){
                updateChannel.updateTitle(channel.getTitle());
                updateChannel.updateCategory(channel.getCategory());
                return updateChannel;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
    }
}
