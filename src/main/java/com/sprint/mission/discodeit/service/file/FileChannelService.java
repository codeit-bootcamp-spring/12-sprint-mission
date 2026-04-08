package com.sprint.mission.discodeit.service.file;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

public class FileChannelService implements ChannelService {
	private final Path fileName;
	private final Map<UUID, Channel> data;

	public FileChannelService() {
		Path directory = Path.of(System.getProperty("user.dir"), "data");
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fileName = directory.resolve("channels.ser");
		if (!Files.exists(fileName)) {
			data = new HashMap<>();
		} else {
			try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(fileName))) {
				data = (HashMap<UUID, Channel>)ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void saveToFile() {
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(fileName))) {
			oos.writeObject(data);
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 실패", e);
		}
	}

	@Override
	public Channel create(Channel channel) {
		if (!data.containsKey(channel.getId())) {
			data.put(channel.getId(), channel);
			saveToFile();
			return channel;
		}
		System.err.println("이미 존재하는 채널입니다.");
		return data.get(channel.getId());
	}

	@Override
	public Channel find(UUID id) {
		return data.get(id);
	}

	@Override
	public List<Channel> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public Channel update(UUID id, User creator, String name, String description) {
		Channel channel = find(id);
		if (channel != null && channel.getCreator().equals(creator.getId())) {
			channel.update(name, description);
			saveToFile();
			return channel;
		}
		System.err.println("채널을 찾을 수 없거나, 권한이 없습니다.");
		return null;
	}

	@Override
	public void delete(UUID id, User creator, MessageService messageService) {
		Channel channel = find(id);
		if (channel != null && channel.getCreator().equals(creator.getId())) {
			data.remove(channel.getId());
			saveToFile();
			for (Message message : messageService.findAll()) {
				if (message.getChannelId().equals(id)) {
					messageService.delete(message.getId(), creator.getId());
				}
			}
		} else {
			System.err.println("채널을 찾을 수 없거나, 권한이 없습니다.");
		}
	}
}
