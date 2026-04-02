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

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {
	private final Path fileName;
	private final Map<UUID, User> data;

	public FileUserService() {
		Path directory = Path.of(System.getProperty("user.dir"), "data");
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fileName = directory.resolve("user.ser");
		if (!Files.exists(fileName)) {
			data = new HashMap<>();
		} else {
			try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(fileName))) {
				data = (HashMap<UUID, User>)ois.readObject();
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
	public User create(User user) {
		if (!data.containsKey(user.getId())) {
			data.put(user.getId(), user);
			saveToFile();
			return user;
		} else {
			System.err.println("이미 존재하는 사용자입니다.");
			return null;
		}
	}

	@Override
	public User find(UUID id) {
		return data.get(id);
	}

	@Override
	public List<User> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public User update(UUID id, String username, String email, String password, String nickname, String phoneNumber,
		String icon) {
		for (User updateUser : data.values()) {
			if (updateUser.getId().equals(id)) {
				updateUser.update(username, email, password, nickname, phoneNumber, icon);
				saveToFile();
				return updateUser;
			}
		}
		return null;
	}

	@Override
	public void delete(UUID id) {
		User user = find(id);
		if (user != null) {
			data.remove(user.getId());
			saveToFile();
		}
	}
}
