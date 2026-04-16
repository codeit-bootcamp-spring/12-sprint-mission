package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class Channel extends BaseEntity implements Comparable<Channel> {
	private final ChannelType type;
	private String name;
	private String description;

	@Builder
	public Channel(ChannelType type, String name, String description) {
		super();
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public void update(String newName, String newDescription) {
		boolean anyValueUpdated = false;
		if (newName != null && !newName.equals(this.name)) {
			this.name = newName;
			anyValueUpdated = true;
		}
		if (newDescription != null && !newDescription.equals(this.description)) {
			this.description = newDescription;
			anyValueUpdated = true;
		}

		if (anyValueUpdated) {
			updateAtUpdate();
		}
	}

	@Override
	public int compareTo(Channel o) {
		return this.getCreatedAt().compareTo(o.getCreatedAt());
	}
}
