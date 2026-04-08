package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String channelName;
    private String description;
    private User owner;
    private List<User> userList;
    private List<Message> messageList;

    public Channel(String channelName, String description, User owner) {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.description = description;
        this.owner = owner;
        userList = new ArrayList<>(List.of(owner));
        messageList = new ArrayList<>();
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getChannelName() { return channelName; }
    public String getDescription() { return description; }
    public User getOwner() { return owner; }
    public List<User> getUserList() { return userList; }
    public List<Message> getMessageList() { return messageList; }

    public void update(String channelName, String description, User owner) {
        this.channelName = channelName;
        this.description = description;
        this.owner = owner;
        updatedAt = System.currentTimeMillis();
    }

    public void addUser(User user) {
        if (userList.contains(user)) {
            return;
        }
        userList.add(user);
    }

    public void deleteUser(User user) {
        if (userList.contains(user)) {
            userList.remove(user);
        } else {
            System.out.println("\t삭제실패 : 입력된 user(" + user + ")는 해당 Channel에 없습니다");
        }
    }

    public void addMessage(Message message) {
        if (!userList.contains(message.getSendUser())) {
            return;
        }
        messageList.add(message);
    }

    public void deleteMessage(Message message) {
        if (messageList.contains(message)) {
            messageList.remove(message);
        } else {
            System.out.println("\t삭제실패 : 입력된 message(" + message + ")는 해당 Channel에 없습니다");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Channel channel = (Channel) object;
        return Objects.equals(id, channel.id) && Objects.equals(createdAt, channel.createdAt) && Objects.equals(updatedAt, channel.updatedAt) && Objects.equals(channelName, channel.channelName) && Objects.equals(description, channel.description) && Objects.equals(owner, channel.owner) && Objects.equals(userList, channel.userList) && Objects.equals(messageList, channel.messageList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, channelName, description, owner, userList, messageList);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", description='" + description + '\'' +
                ", owner=" + owner +
                ", userList=" + userList +
                ", messageList=" + messageList +
                '}';
    }
}
