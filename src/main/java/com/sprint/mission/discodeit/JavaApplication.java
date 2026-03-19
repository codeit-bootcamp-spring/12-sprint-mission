package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
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
		System.out.println("단건 조회: " + userService.findById(user.getId()));
		System.out.print("다건 조회: ");
		userService.findAll().forEach(System.out::println);
		userService.update(user3.getId(), user3.getUsername(), user3.getEmail(), "gsdfnglsd", user3.getNickname(), user3.getPhoneNumber(), user3.getIcon());
		System.out.println("수정된 데이터 조회: " + userService.findById(user3.getId()));
		userService.deleteById(user3.getId());
		System.out.print("조회를 통해 삭제되었는지 확인: ");
		userService.findAll().forEach(System.out::println);
		System.out.println("---------------사용자 테스트 끝!!!---------------------\n");
	}
}