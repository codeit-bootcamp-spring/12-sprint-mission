package com.sprint.mission.discodeit.service.file;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

public class FileMessageService implements MessageService {
	private final Path fileName;
	private final Map<UUID, Message> data;

	public FileMessageService() {
		Path directory = Path.of(System.getProperty("user.dir"), "data");
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fileName = directory.resolve("message.ser");
		if (!Files.exists(fileName)) {
			data = new HashMap<>();
		} else {
			try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(fileName))) {
				data = (HashMap<UUID, Message>)ois.readObject();
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
	public Message create(Channel channel, Message message) {
		if (channel == null || message == null) {
			return null;
		}
		if (!channel.getId().equals(message.getChannelId())) {
			return null;
		}
		if (!data.containsKey(message.getId())) {
			data.put(message.getId(), message);
			saveToFile();
			return message;
		} else {
			System.err.println("메세지가 이미 존재합니다.");
			return null;
		}
	}

	@Override
	public Message find(UUID id) {
		return data.get(id);
	}

	@Override
	public List<Message> findAll() {
		return data.values().stream().sorted(Comparator.comparing(Message::getCreatedAt)).toList();
	}

	@Override
	public Message update(UUID id, UUID userid, String content) {
		for (Message message : data.values()) {
			if (message.getId().equals(id)) {
				message.update(content);
				saveToFile();
				return message;
			}
		}
		return null;
	}

	@Override
	public void delete(UUID id, UUID userId) {
		Message message = find(id);
		if (message != null && message.getUserId().equals(userId)) {
			data.remove(message.getId());
			saveToFile();
		}
	}
}
