package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String channelName;
    private List<User> participants;

    public Channel(String channelName, List<User> participants) {
        super();
        this.channelName = channelName;
        this.participants = new ArrayList<>(participants);
    }

    public String getChannelName() {
        return channelName;
    }

    public List<User> getParticipants() {
        return participants;
    }


    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        touch();
    }

    public void addParticipant(User user) {
        this.participants.add(user);
        touch();
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
        touch();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + getId() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", channelName='" + channelName + '\'' +
                ", participantsCount=" + participants.size() +
                '}';
    }




}