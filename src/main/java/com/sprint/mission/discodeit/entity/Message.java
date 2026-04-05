package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String content;
    private User sender;
    private Channel channel;

    public Message(String content, User sender, Channel channel) {
        super();
        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    public void updateContent(String content) {
        this.content = content;
        touch();
    }

    public String getContent() {
        return content;
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getDisplayMessage() {
        return sender.getUsername() + " : " + content;
    }


    @Override
    public String toString() {
        return "Message{" +
                "id=" + getId() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", content='" + content + '\'' +
                ", sender=" + sender.getUsername() +
                ", channelName=" + channel.getChannelName() +
                '}';
    }




}