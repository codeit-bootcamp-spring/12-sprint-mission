package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(channelService, userService);

        System.out.println("=== 1. 데이터 생성 테스트 ===");
        User user = new User("이경훈", "hun@codeit.com", "pass123", "후니");
        userService.save(user);
        System.out.println("저장된 유저: " + userService.findById(user.getId()).getNickname());
        userService.save(new User("송민형", "sims0524@naver.com", "0524", "형"));

        // 채널 생성 및 저장
        Channel channel = new Channel("일반 채널", "모든 멤버가 참여할 수 있는 채널입니다.");
        channelService.save(channel);
        System.out.println("저장된 채널: " + channelService.findById(channel.getId()).getName());

        // 메시지 생성 및 저장
        Message message = new Message(user.getId(), channel.getId(), "안녕하세요! 첫 메시지입니다.");
        messageService.save(message);
        System.out.println("저장된 메시지 내용: " + messageService.findById(message.getId()).getContent());


        System.out.println("\n=== 2. 업데이트 테스트 ===");
        // 유저 정보 업데이트 (닉네임 변경)
        userService.update(user.getId(), null, null, null, "코딩천재");
        System.out.println("수정된 닉네임: " + userService.findById(user.getId()).getNickname());

        // 메시지 내용 업데이트
        messageService.update(message.getId(), "내용을 수정했습니다.", null);
        System.out.println("수정된 메시지: " + messageService.findById(message.getId()).getContent());


        System.out.println("\n=== 3. 전체 조회 테스트 ===");
        System.out.println("현재 등록된 총 유저 수: " + userService.findAll().size());
        System.out.println("현재 등록된 총 메시지 수: " + messageService.findAll().size());


        System.out.println("\n=== 4. 삭제 테스트 ===");
        // 메시지 삭제
        messageService.delete(message.getId());
        Message deletedMsg = messageService.findById(message.getId());
        System.out.println("삭제 후 조회 결과 (null이어야 함): " + deletedMsg);

        System.out.println("\n=== 모든 테스트 완료! ===");
    }
}