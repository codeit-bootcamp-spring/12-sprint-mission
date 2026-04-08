package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Objects;
import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

//  등록
        System.out.println("=============== 등록 ===============");
        // 유저 이름 메일
        User user1 = userService.save(new User("홍길동", "hong@example.com"));
        User user2 = userService.save(new User("김철수", "kim@example.com"));
        // 채널 이름 소개
        Channel channel1 = channelService.save((new Channel("공지방", "공지방입니다.")));
        Channel channel2 = channelService.save((new Channel("일반 과제방", "일반 과제 제출방입니다.")));
        // 메세지
        Message message1 = messageService.save(new Message("공지합니다.", user1.getId(), channel1.getId()));
        Message message2 = messageService.save(new Message("변수 과제 제출합니다.", user2.getId(), channel2.getId()));

        System.out.println("user1" + user1);
        System.out.println("user2" + user2);
        System.out.println("channel1: " + channel1);
        System.out.println("channel2: " + channel2);
        System.out.println("message1: " + message1);
        System.out.println("message2: " + message2);

//  조회(단건, 다건)
        System.out.println("\n============= 단건 조회 =============");
        System.out.println("User1: " + userService.findById(user1.getId()).map(Objects::toString).orElse("존재하지 않음"));
        System.out.println("User2: " + userService.findById(user2.getId()).map(Objects::toString).orElse("존재하지 않음"));
        System.out.println("Channel1: " + channelService.findById(channel1.getId()).map(Objects::toString).orElse("존재하지 않음"));
        System.out.println("Channel2: " + channelService.findById(channel2.getId()).map(Objects::toString).orElse("존재하지 않음"));
        System.out.println("Message1: " + messageService.findById(message1.getId()).map(Objects::toString).orElse("존재하지 않음"));
        System.out.println("Message2: " + messageService.findById(message2.getId()).map(Objects::toString).orElse("존재하지 않음"));

        System.out.println("\n============= 다건 조회 =============");
        System.out.println("User: " + userService.findAll());
        System.out.println("Channel: " + channelService.findAll());
        System.out.println("Message: " + messageService.findAll());

//  수정
        System.out.println("\n========== 수정 및 데이터 조회==========");
        userService.update(user2.getId(), "이철수", "lee@example.com");
        channelService.update(channel2.getId(), "과제방", "과제 제출방입니다.");
        messageService.update(message2.getId(), "배열 과제 제출합니다.", user2.getId(), channel2.getId());

        System.out.println("User: " + userService.findById(user2.getId()).orElse(null));
        System.out.println("Channel: " + channelService.findById(channel2.getId()).orElse(null));
        System.out.println("Message: " + messageService.findById(message2.getId()).orElse(null));

//  삭제
//  조회를 통해 삭제되었는지 확인
        System.out.println("\n========== 삭제 및 데이터 조회==========");
        boolean user2Deleted =  userService.deleteById(user2.getId());
        boolean channel2Deleted = channelService.deleteById(user2.getId());
        boolean message2Deleted = messageService.deleteById(user2.getId());

        System.out.println("삭제 후 User2 조회: " + userService.findById(user2.getId()).isPresent());
        System.out.println("삭제 후 Channel2 조회: " + channelService.findById(user2.getId()).isPresent());
        System.out.println("삭제 후 Message2 조회: " + messageService.findById(user2.getId()).isPresent());










    }
}










