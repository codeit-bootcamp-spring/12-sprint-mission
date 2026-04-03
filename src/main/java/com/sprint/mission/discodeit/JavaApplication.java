package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {

	public static void Test(UserService userService, ChannelService channelService, MessageService messageService) {
		System.out.println("┏━━━━━━━━━━━━━━━━━━━ 사용자 테스트 시작!!! ━━━━━━━━━━━━━━━━━━━━");

		User user1 = userService.create(
			new User("user1", "test@email.com", "1234", "user", "+821012345678", "https://icon.com/1.png"));
		User user2 = userService.create(
			new User("user2", "test2@email.com", "12345", "user2", "+821098765432", "https://icon.com/2.png"));
		User user3 = userService.create(
			new User("user3", "test3@email.com", "46535621", "user3", "+821045219159", "https://icon.com/3.png"));

		System.out.println("┣───────────── 단건 조회(test) ─────────────");
		System.out.println("┃ "+userService.find(user1.getId()));

		System.out.println("┃\n┣───────────── 다건 조회("+ userService.findAll().size() +"개) ─────────────");
		userService.findAll().forEach(u -> System.out.println("┃ "+u));

		System.out.println("┃\n┣───────────── 수정된 데이터 조회(user3 비밀번호 수정) ─────────────");
		System.out.println("┃ "+userService.update(user3.getId(), user3.getUsername(), user3.getEmail(), "djngklgjlrnek",
			user3.getNickname(), user3.getPhoneNumber(), user3.getIcon()));

		System.out.println("┃\n┣───────────── 조회를 통해 삭제되었는지 확인(user3 삭제) ─────────────");
		userService.delete(user3.getId());
		userService.findAll().forEach(u -> System.out.println("┃ "+u));
		System.out.println("┗━━━━━━━━━━━━━━━━━━━ 사용자 테스트 끝!!! ━━━━━━━━━━━━━━━━━━━━\n");

		System.out.println("┏━━━━━━━━━━━━━━━━━━━ 채널 테스트 시작!!! ━━━━━━━━━━━━━━━━━━━━");
		Channel channel = new Channel(ChannelType.TEXT, "공지 채널", "공지 채널입니다.", user1.getId());
		channelService.create(channel);

		Channel channel2 = new Channel(ChannelType.VOICE, "음성 채널", "음성 채널입니다.", user2.getId());
		channelService.create(channel2);

		Channel channel3 = new Channel(ChannelType.TEXT, "채팅 채널", "채팅 채널입니다.", user1.getId());
		channel3 = channelService.create(channel3);

		System.out.println("┣───────────── 단건 조회(channel) ─────────────");
		System.out.println("┃ "+channelService.find(channel.getId()));

		System.out.println("┃\n┣───────────── 다건 조회("+ channelService.findAll().size() +"개) ─────────────");
		channelService.findAll().forEach(c -> System.out.println("┃ "+c));

		System.out.println("┃\n┣───────────── 수정된 데이터 조회(공지 채널 이름과 설명 수정) ─────────────");
		System.out.println("┃ "+channelService.update(channel.getId(), user1, "공지 채널 수정", "공지 채널 수정"));

		System.out.println("┃\n┣───────────── 조회를 통해 삭제되었는지 확인(음성 채널 삭제) ─────────────");
		channelService.delete(channel2.getId(), user2, messageService);
		channelService.findAll().forEach(c -> System.out.println("┃ "+c));
		System.out.println("┗━━━━━━━━━━━━━━━━━━━ 채널 테스트 끝!!! ━━━━━━━━━━━━━━━━━━━━\n");

		System.out.println("┏━━━━━━━━━━━━━━━━━━━ 메세지 테스트 시작!!! ━━━━━━━━━━━━━━━━━━━━");

		Message message = new Message(user2.getId(), channel.getId(), "공지 메세지1");
		messageService.create(channelService.find(channel.getId()), message);

		Message message2 = new Message(user1.getId(), channel3.getId(), "메세지2");
		messageService.create(channelService.find(channel3.getId()), message2);

		Message message3 = new Message(user1.getId(), channel3.getId(), "메세지3");
		messageService.create(channelService.find(channel3.getId()), message3);

		System.out.println("┣───────────── 단건 조회(message) ─────────────");
		System.out.println("┃ "+messageService.find(message.getId()));

		System.out.println("┃\n┣───────────── 다건 조회("+messageService.findAll().size()+"개) ─────────────");
		messageService.findAll().forEach(m ->System.out.println("┃ "+m));

		System.out.println("┃\n┣───────────── 수정된 데이터 조회(댓글 수정) ─────────────");
		System.out.println("┃"+ messageService.update(message2.getId(), user1.getId(), "메세지2 수정"));

		System.out.println("┃\n┣───────────── 조회를 통해 삭제되었는지 확인(message 삭제) ─────────────");
		messageService.delete(message.getId(), user2.getId());
		messageService.findAll().forEach(m ->System.out.println("┃ "+m));

		System.out.println("┗━━━━━━━━━━━━━━━━━━━ 메세지 테스트 끝!!! ━━━━━━━━━━━━━━━━━━━━\n");
	}

	public static void main(String[] args) {
		System.out.println("════════════════════════ JCF 기반 서비스 테스트 시작!!! ════════════════════════");
		UserService JCFUserService = new BasicUserService(new JCFUserRepository());
		ChannelService JCFChannelService = new BasicChannelService(new JCFChannelRepository());
		MessageService JCFMessageService = new BasicMessageService(new JCFMessageRepository());

		Test(JCFUserService, JCFChannelService, JCFMessageService);
		System.out.println("════════════════════════ JCF 기반 서비스 테스트 끝!!! ════════════════════════\n");

		System.out.println("════════════════════════ 파일 기반 서비스 테스트 시작!!! ════════════════════════");
		UserService FileUserService = new BasicUserService(new FileUserRepository());
		ChannelService FileChannelService = new BasicChannelService(new FileChannelRepository());
		MessageService FileMessageService = new BasicMessageService(new FileMessageRepository());

		Test(FileUserService, FileChannelService, FileMessageService);
		System.out.println("════════════════════════ 파일 기반 서비스 테스트 끝!!! ════════════════════════\n");
	}

}