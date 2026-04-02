package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity{

    private String name;

    public Channel(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void update(String name){
        this.name = name;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                '}';
    }
}
