package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        // 사용자 테스트 시작!!
        System.out.println("----------------사용자 테스트 시작!!!------------------");
        UserService userService = new JCFUserService();

        User user = new User("test", "test@email.com", "1234", "test");
        userService.save(user);
        System.out.println(userService.findAll());
        System.out.println("---------------사용자 테스트 끝!!!---------------------\n");
    }
}