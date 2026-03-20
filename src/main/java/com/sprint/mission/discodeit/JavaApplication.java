package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
	public static void main(String[] args) {
		// 사용자 테스트 시작!!
		System.out.println("----------------사용자 테스트 시작!!!------------------");
		UserService userService = new JCFUserService();

		User user = new User("test", "test@email.com", "1234", "test", "+821012345678", "https://icon.com/1.png");
		userService.save(user);
		User user2 = new User("test2", "test2@email.com", "12345", "test2", "+821098765432", "https://icon.com/2.png");
		userService.save(user2);
		User user3 = new User("test3", "test3@email.com", "46535621", "test3", "+821045219159", "https://icon.com/3.png");
		userService.save(user3);
		System.out.println("--------단건 조회(test)--------");
		System.out.println(userService.findById(user.getId()));
		System.out.println("--------다건 조회--------");
		userService.findAll().forEach(System.out::println);
		System.out.println("--------수정된 데이터 조회--------");
		System.out.println(userService.update(user3.getId(), user3.getUsername(), user3.getEmail(), "djngklgjlrnek", user3.getNickname(), user3.getPhoneNumber(), user3.getIcon()));
		userService.deleteById(user3.getId());
		System.out.println("--------조회를 통해 삭제되었는지 확인(test3)--------");
		userService.findAll().forEach(System.out::println);
		System.out.println("---------------사용자 테스트 끝!!!---------------------\n");

		System.out.println("----------------메세지 테스트 시작!!!------------------");
		MessageService messageService = new JCFMessageService();

		Message message = new Message(user2.getId(), user2.getUsername(), "메세지1");
		messageService.save(message);
		Message message2 = new Message(user.getId(), user.getUsername(), "메세지2");
		messageService.save(message2);
		System.out.println("--------단건 조회(test2)--------");
		System.out.println(messageService.findById(message.getId()));
		System.out.println("--------다건 조회--------");
		messageService.findAll().forEach(System.out::println);
		System.out.println("--------수정된 데이터 조회(test)--------");
		System.out.println(messageService.update(message2.getId(), user.getId(), "메세지2 수정"));
		messageService.deleteById(message.getId());
		System.out.println("--------조회를 통해 삭제되었는지 확인--------");
		messageService.findAll().forEach(System.out::println);
		System.out.println("---------------메세지 테스트 끝!!!---------------------\n");


	}
}