package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("---------------- DISCODEIT 테스트 시작 ------------------");

        Scanner scanner = new Scanner(System.in);
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // [1] 회원가입 (User 등록)
        System.out.println("[1] 회원가입");
        System.out.print("아이디: "); String myId = scanner.nextLine();
        System.out.print("이메일: "); String myEmail = scanner.nextLine();
        System.out.print("비밀번호: "); String myPw = scanner.nextLine();
        System.out.print("닉네임: "); String myNick = scanner.nextLine();

        User me = new User(myId, myEmail, myPw, myNick);
        userService.save(me);
        System.out.println("가입 완료! UUID: " + me.getId());
        System.out.println("가입 시각: " + sdf.format(new Date(me.getCreatedAt())));

        // [2] 채널 생성 및 조회 (Channel 등록/조회)
        System.out.println("\n[2] 채널 생성 및 조회");
        System.out.print("생성할 채널명: "); String cName = scanner.nextLine();
        System.out.print("채널 설명: "); String desc = scanner.nextLine();

        Channel myChan = new Channel(cName, ChannelType.TEXT, desc, me);
        channelService.save(myChan);

        // 다건 조회
        System.out.println("전체 채널 조회 : " + channelService.findAll().size());
        // 단건 조회
        Channel foundChan = channelService.findById(myChan.getId());
        System.out.println("단건 조회 결과: [" + foundChan.getChannelName() + "] UUID: " + foundChan.getId());

        // DM(Message) 전송
        System.out.println("\n[3] DM(Message) 보내기");
        User friend = new User("friend1", "friend@test.com", "pass", "친구");;

        System.out.print(friend.getNickname() + "님에게 보낼 메시지: ");
        String content = scanner.nextLine();
        Message dm = new Message(me, friend, null, content);
        messageService.save(dm);
        System.out.println("메시지 전송 완료!");
        System.out.println("보낸 사람: " + dm.getAuthorId().getNickname());
        System.out.println("전송 시간: " + sdf.format(new Date(dm.getCreatedAt())));

        // [4] 수정 테스트 (채널 이름 수정)
        System.out.println("\n[4] 채널 정보 수정 테스트");
        System.out.print("변경할 채널 이름: "); String newChanName = scanner.nextLine();
        myChan.update(newChanName, myChan.getChannelType(), "수정된 설명입니다.");
        channelService.update(myChan, me.getId());

        Channel updated = channelService.findById(myChan.getId());
        System.out.println("수정 후 이름: " + updated.getChannelName());
        System.out.println("수정 시각: " + sdf.format(new Date(updated.getUpdatedAt())));

        // [5] 삭제 테스트 (단건/다건)
        System.out.println("\n[5] 삭제 테스트");
        // 채널 단건 삭제
        System.out.print("방금 만든 채널을 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            channelService.deleteById(myChan.getId(), me.getId());
            System.out.println("채널 단건 삭제 완료!");
        }

        // 메시지 단건 삭제
        System.out.print("보낸 메시지를 단건 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            try {
                messageService.deleteByID(dm.getId(), me.getId());
                System.out.println("메시지 단건 삭제 완료!");
            } catch (Exception e) {
                System.out.println("삭제 중 에러 발생: " + e.getMessage());
            }
        }

        // 메시지 다건 삭제
        System.out.print("모든 메시지를 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            messageService.deleteAll();
            System.out.println("모든 메시지 삭제 완료!");
        }

        // 유저 삭제(탈퇴)
        System.out.print("내 계정(" + me.getUsername() + ")을 삭제(탈퇴)하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            userService.deleteByID(me.getId(), me.getId());
            System.out.println("내 계정 삭제(탈퇴) 완료!");
        }

        // [6] 최종 결과 확인
        System.out.println("\n[6] 최종 데이터 상태 확인");
        System.out.println("- 유저 전체 정보:" + userService.findAll());
        System.out.println("- 유저 전체 수: " + userService.findAll().size());
        System.out.println("- 채널 전체 수: " + channelService.findAll().size());
        System.out.println("- 메시지 전체 수: " + messageService.findAll().size());

        System.out.println("---------------- DISCODEIT 테스트 종료 ------------------");
    }
}