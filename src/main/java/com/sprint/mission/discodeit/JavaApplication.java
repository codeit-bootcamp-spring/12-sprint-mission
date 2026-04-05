package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {

        // 서비스 객체 생성
        UserService userService = new JCFUserService();

        // 등록 (Create)
        User user1 = new User("user1", "user1@email.com", "1234", "닉네임1");
        userService.create(user1);

        System.out.println("=== 등록 완료 ===");
        System.out.println(user1);

        // (단건 조회)
        User foundUser = userService.findById(user1.getId());
        System.out.println("=== 단건 조회 ===");
        System.out.println(foundUser);

        // 조회 (전체 조회)
        System.out.println("=== 전체 조회 ===");
        System.out.println(userService.findAll());

        // 수정
        User updatedUser = new User("user1_updated", "new@email.com", "9999", "새닉네임");

        // 기존 ID 유지하도록 강제로 세팅
        // (실무에서는 update 메소드 다르게 설계하지만 지금은 과제용)
        userService.update(user1.getId(), updatedUser);

        // 수정된 데이터 조회
        System.out.println("=== 수정 후 조회 ===");
        System.out.println(userService.findById(user1.getId()));

        // 삭제
        userService.delete(user1.getId());

        // 삭제 확인
        System.out.println("=== 삭제 후 전체 조회 ===");
        System.out.println(userService.findAll());
    }
}