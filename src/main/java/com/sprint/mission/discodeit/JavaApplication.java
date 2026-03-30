package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.*;
import java.util.UUID;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        // 서비스 초기화
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(channelService, userService);

        // ---------------------------------------------------------
        // 1. 각 엔티티 4개씩 생성하여 초기화
        // ---------------------------------------------------------
        System.out.println("======= [1] 초기 데이터 4개씩 생성 =======");
        for (int i = 1; i <= 4; i++) {
            User u = userService.save(new User("User" + i, "u" + i + "@test.com", "Nick" + i, "pw" + i));
            Channel c = channelService.save(new Channel("Channel" + i, u, "Category" + i));
            messageService.save(new Message(c, u, "Title" + i, "Content" + i));
        }
        System.out.println("데이터 생성 완료.\n");

        // ---------------------------------------------------------
        // 2. 모든 객체 조회 (Find All)
        // ---------------------------------------------------------
        System.out.println("======= [2] 모든 서비스 객체 전체 조회 =======");
        System.out.println("전체 유저: " + userService.findAll());
        System.out.println("전체 채널: " + channelService.findAll());
        System.out.println("전체 메시지: " + messageService.findAll());
        System.out.println();

        // ---------------------------------------------------------
        // 3. 특정 객체 1개씩 골라 'Service.update()'로 한 요소만 수정
        // ---------------------------------------------------------
        System.out.println("======= [3] 서비스 update를 통한 부분 수정 =======");

        // (A) User 수정: 첫 번째 유저의 닉네임만 변경
        User targetUser = userService.findAll().get(0);
        final UUID targetUserId = targetUser.getId();
        User userUpdateReq = new User(null, null, "★슈퍼닉네임★", null) {
            @Override public UUID getId() { return targetUserId; }
        };
        userService.update(userUpdateReq);
        System.out.println("User 수정 완료 (닉네임 변경 확인): " + userService.findById(targetUserId));

        // (B) Channel 수정: 두 번째 채널의 타이틀만 변경
        Channel targetChannel = channelService.findAll().get(1);
        final UUID targetChannelId = targetChannel.getId();
        Channel channelUpdateReq = new Channel("업데이트된_비밀채널", null, null) {
            @Override public UUID getId() { return targetChannelId; }
        };
        channelService.update(channelUpdateReq);
        System.out.println("Channel 수정 완료 (타이틀 변경 확인): " + channelService.findById(targetChannelId));

        // (C) Message 수정: 세 번째 메시지의 내용(Content)만 변경
        Message targetMsg = messageService.findAll().get(2);
        final UUID targetMsgId = targetMsg.getId();
        Message msgUpdateReq = new Message(null, null, null, "내용이 완전히 바뀌었습니다!") {
            @Override public UUID getId() { return targetMsgId; }
        };
        messageService.update(msgUpdateReq);
        System.out.println("Message 수정 완료 (내용 변경 확인): " + messageService.findById(targetMsgId));
        System.out.println();

        // ---------------------------------------------------------
        // 4. 데이터 삭제 후 전체 조회로 확인
        // ---------------------------------------------------------
        System.out.println("======= [4] 데이터 삭제 및 결과 확인 =======");

        // 각 서비스에서 마지막 데이터(index 3) 삭제 시도
        UUID lastUserId = userService.findAll().get(3).getId();
        UUID lastChannelId = channelService.findAll().get(3).getId();
        UUID lastMsgId = messageService.findAll().get(3).getId();

        System.out.println("삭제 시도 대상 ID들: User(" + lastUserId + "), Channel(" + lastChannelId + "), Msg(" + lastMsgId + ")");

        userService.delete(lastUserId);
        channelService.delete(lastChannelId);
        messageService.delete(lastMsgId);

        System.out.println("\n--- 최종 삭제 결과 (전체 리스트) ---");
        System.out.println("남은 유저 수 (3개 예상): " + userService.findAll().size());
        System.out.println("남은 채널 수 (3개 예상): " + channelService.findAll().size());
        System.out.println("남은 메시지 수 (3개 예상): " + messageService.findAll().size());

        // 개별 조회 확인
        if (userService.findById(lastUserId) == null) System.out.println("User 삭제 성공!");
        if (channelService.findById(lastChannelId) == null) System.out.println("Channel 삭제 성공!");
        if (messageService.findById(lastMsgId) == null) System.out.println("Message 삭제 성공!");
    }
}