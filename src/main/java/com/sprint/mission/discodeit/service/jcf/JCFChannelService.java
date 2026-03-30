package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
//    private final List<Channel> data; // map 형식으로 받아서 iteration에서 시간복잡도를 줄이는게 좋을 것 같음.
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
//        this.data = new ArrayList<>();
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel save(Channel channel) {
        if(userService.findById(channel.getAuthor().getId()) == null){
            System.out.println("Author information is not exist.");
            return null;
        }
        data.put(channel.getId(),channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if(data.containsKey(id)){
            return data.get(id);
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        if(data.containsKey(channel.getId())){
            if(channel.getTitle() != null) data.get(channel.getId()).updateTitle(channel.getTitle());
            if(channel.getCategory() != null) data.get(channel.getId()).updateCategory(channel.getCategory());
            return data.get(channel.getId());
        }   
        return null;
    }

    @Override
    public void delete(UUID id) {
        Channel removeChannel = data.remove(id);
    }
}
