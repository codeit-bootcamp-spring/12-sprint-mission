package com.sprint.mission.discodeit;

import static com.sprint.mission.discodeit.DiscodeitApplication.*;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {

	public static void main(String[] args) {
		// 레포지토리 초기화
		UserRepository userRepository = new FileUserRepository(".discodeit");
		ChannelRepository channelRepository = new FileChannelRepository(".discodeit");
		MessageRepository messageRepository = new FileMessageRepository(".discodeit");
		BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(".discodeit");
		ReadStatusRepository readStatusRepository = new FileReadStatusRepository(".discodeit");
		UserStatusRepository userStatusRepository = new FileUserStatusRepository(".discodeit");

		// 서비스 초기화
		UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
		ChannelService channelService = new BasicChannelService(channelRepository, readStatusRepository,
			messageRepository);
		MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository,
			binaryContentRepository);

		// 셋업
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		// 테스트
		messageCreateTest(messageService, channel, user);
	}
}
