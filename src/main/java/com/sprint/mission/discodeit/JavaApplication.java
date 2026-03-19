package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        // 사용자 테스트 시작!!
        System.out.println("-------------사용자 테스트 시작!!!--------------");
        User user = new User(8a463aa4-b1dc-4f27-9c3f-53b94dc45e7, "test.com", "1234", "kim", "kk");
        UserService userService = new JCFUserService();
        userService.save(user);
        System.out.println(userService.findAll());
        System.out.println("-------------사용자 테스트 끝!!!----------------\n");
    }
}
