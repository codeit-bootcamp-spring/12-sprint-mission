package com.sprint.mission.discodeit.repository.file;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class FileUserRepository implements UserRepository {
	private final Path fileName;
	private final Map<UUID, User> data;

	public FileUserRepository() {
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
	public User save(User user) {
		data.put(user.getId(), user);
		saveToFile();
		return user;
	}

	@Override
	public Optional<User> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<User> findAll() {
		return data.values().stream().toList();
	}

	@Override
	public Long count() {
		return (long)data.size();
	}

	@Override
	public void delete(UUID id) {
		data.remove(id);
		saveToFile();
	}

	@Override
	public boolean existsById(UUID id) {
		return data.containsKey(id);
	}
}
