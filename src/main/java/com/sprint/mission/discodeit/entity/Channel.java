package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    // 채널명, 채널 설명
    private String name;
    private String description;

    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        touch();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
