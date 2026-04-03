package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 실행하고 싶은 테스트 하나만 주석 해제하여 사용하세요.
        // test1(); // JCF 전용 서비스 테스트
        // test2(); // 파일 직렬화 전용 서비스 테스트
        test3(); // Basic 서비스 + JCF 레포지토리 테스트
        // test4(); // Basic 서비스 + File 레포지토리 테스트
    }

    // [Test 1] 기존 JCF 전용 서비스 사용
    public static void test1() {
        System.out.println(">>> 실행 모드: Test 1 (JCF Service 전용)");
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(channelService, userService);
        runTest(userService, channelService, messageService);
    }

    // [Test 2] 기존 파일 직렬화 전용 서비스 사용 (파일 삭제 선행)
    public static void test2() {
        System.out.println(">>> 실행 모드: Test 2 (File Serialization Service 전용)");
        clearDataFiles();
        FileUserService userService = new FileUserService();
        FileChannelService channelService = new FileChannelService();
        FileMessageService messageService = new FileMessageService(userService, channelService);
        runTest(userService, channelService, messageService);
    }

    // [Test 3] Basic 서비스 + JCF 레포지토리 주입
    public static void test3() {
        System.out.println(">>> 실행 모드: Test 3 (Basic Service + JCF Repository)");
        JCFUserRepository userRepository = new JCFUserRepository();
        JCFChannelRepository channelRepository = new JCFChannelRepository();
        JCFMessageRepository messageRepository = new JCFMessageRepository();

        BasicUserService userService = new BasicUserService(userRepository);
        BasicChannelService channelService = new BasicChannelService(channelRepository);
        BasicMessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);
        runTest(userService, channelService, messageService);
    }

    // [Test 4] Basic 서비스 + File 레포지토리 주입 (파일 삭제 선행)
    public static void test4() {
        System.out.println(">>> 실행 모드: Test 4 (Basic Service + File Repository)");
        clearDataFiles();
        FileUserRepository userRepository = new FileUserRepository();
        FileChannelRepository channelRepository = new FileChannelRepository();
        FileMessageRepository messageRepository = new FileMessageRepository();

        BasicUserService userService = new BasicUserService(userRepository);
        BasicChannelService channelService = new BasicChannelService(channelRepository);
        BasicMessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);
        runTest(userService, channelService, messageService);
    }

    // 테스트 실행 전 기존 데이터 파일 삭제
    // 이전 테스트 시 남아 있는 객체에 대해 create를 하면서 중복이 발생하면 null이 리턴되도록 구성 되어 있기 때문에 일단 삭제하는 방식으로 구성
    // 이미 존재하는 객체에 대해 null 대신 User객체를 리턴하도록 전부 변경하면 삭제 해도 될 것 같습니다.
    private static void clearDataFiles() {
        String[] files = {"user.dat", "messages.dat", "channel.dat"};
        for (String fileName : files) {
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
        }
        System.out.println("기존 .dat 파일 삭제 완료.");
    }

    private static void runTest(UserService userService, ChannelService channelService, MessageService messageService) {

        System.out.println("========== [1. 데이터 일괄 생성] ==========");
        User u1 = userService.create("lee", "lee@test.com", "1", "이경훈");
        User u2 = userService.create("song", "song@test.com", "2", "송민형");
        User u3 = userService.create("kim", "kim@test.com", "3", "김철수");

        Channel c1 = new Channel("자바기초", "자바 기본 문법 공부방");
        Channel c2 = new Channel("프로젝트", "최종 프로젝트 협업 전용");
        channelService.save(c1);
        channelService.save(c2);

        System.out.println("유저/채널 생성 완료 (유저: " + userService.findAll().size() + ", 채널: " + channelService.findAll().size() + ")");

        // 채널 1 (자바기초) 메시지 6개
        messageService.save(new Message(u1.getId(), c1.getId(), "안녕하세요, 자바 공부 시작합니다!"));
        messageService.save(new Message(u2.getId(), c1.getId(), "반가워요 경훈님!"));
        messageService.save(new Message(u3.getId(), c1.getId(), "저도 같이 공부해요."));
        messageService.save(new Message(u1.getId(), c1.getId(), "제네릭이 너무 어렵네요ㅠㅠ"));
        messageService.save(new Message(u2.getId(), c1.getId(), "그거 스트림이랑 같이 보면 편해요."));
        messageService.save(new Message(u3.getId(), c1.getId(), "맞아요, 람다도 중요하죠!"));

        // 채널 2 (프로젝트) 메시지 4개
        messageService.save(new Message(u1.getId(), c2.getId(), "프로젝트 주제 정해졌나요?"));
        messageService.save(new Message(u2.getId(), c2.getId(), "채팅 서비스로 하기로 했어요."));
        messageService.save(new Message(u3.getId(), c2.getId(), "저는 백엔드 맡을게요."));
        Message m1 = messageService.save(new Message(u1.getId(), c2.getId(), "그럼 전 프론트엔드 할게요!"));

        System.out.println("전체 메시지 등록 완료: " + messageService.findAll().size() + "개\n");

        List<Message> messages = messageService.findAll();


        System.out.println("\n========== [2-1. 특정 메시지 조회 u1 c2 ] =========="); // 단일 조회시 id 기준? nickname기준 ?


        System.out.println("채널 : " + channelService.findById(m1.getChannelId()).getName()
                + " | 닉네임 : " + userService.findById(m1.getUserId()).getNickname() + "\n"
                + m1.getContent()
                + "\n----------------------------------------------------");

        System.out.println("========== 2-1-1. 전체 메세지 조회 ===========");

        for (Message m : messages) {
            System.out.println("채널 : " + channelService.findById(m.getChannelId()).getName()
                             + " | 닉네임 : " + userService.findById(m.getUserId()).getNickname() + "\n"
                             + m.getContent()
                             + "\n----------------------------------------------------");
        }

        System.out.println("\n========== [2-2. 유저 조회] ==========");

        System.out.println("ID : " + u2.getId()
                + " | 닉네임 : " + u2.getNickname()
                + " | 이메일 : " + u2.getEmail());
        System.out.println("----------------------------------------------------");

        System.out.println("\n========== [2-2-1. 유저 전체 조회] ==========");

        for (User u : userService.findAll()) {
            System.out.println("ID : " + u.getId()
                    + " | 닉네임 : " + u.getNickname()
                    + " | 이메일 : " + u.getEmail());
            System.out.println("----------------------------------------------------");
        }

        System.out.println("\n========== [2-2-1. 채널 조회] ==========");

        System.out.println("채널명 : " + c1.getName());
        System.out.println("설명 : " + c1.getDescription());
        System.out.println("ID : " + c1.getId());
        System.out.println("----------------------------------------------------");

        System.out.println("\n========== [2-2-1. 채널 전체 조회] ==========");

        for (Channel c : channelService.findAll()) {
            System.out.println("채널명 : " + c.getName());
            System.out.println("설명 : " + c.getDescription());
            System.out.println("ID : " + c.getId());
            System.out.println("----------------------------------------------------");
        }


        System.out.println("\n========== [3. 수정 및 재조회 검증] ==========");
        // 유저 2의 닉네임 변경 및 확인
        System.out.println("유저2 수정된 정보 (기존 닉네임): " + u2.getNickname());
        userService.update(u2.getId(), null, null, null, "민형마스터");

        System.out.print("수정된 닉네임으로 재조회 - [");

        u2 = userService.findById(u2.getId());

        System.out.println("ID : " + u2.getId()
                + " | 닉네임 : " + u2.getNickname()
                + " | 이메일 : " + u2.getEmail()
                + "]");
        System.out.println("----------------------------------------------------");

        // 마지막 메시지 내용 수정 및 확인
        messageService.update(m1.getId(), "전 풀스택 할래요!", null);
        System.out.println("수정된 마지막 메시지: ");
        System.out.println("채널 : " + channelService.findById(m1.getChannelId()).getName()
                + " | 닉네임 : " + userService.findById(m1.getUserId()).getNickname() + "\n"
                + m1.getContent()
                + "\n----------------------------------------------------");

        System.out.println("\n========== [4. 특정 데이터 삭제 검증] ==========");

        System.out.println("삭제 전 메시지 수: " + messageService.findAll().size());
        messageService.delete(m1.getId());
        System.out.println("삭제 후 메시지 수: " + messageService.findAll().size() + " (기대값: 9)");
        System.out.println("삭제 확인 (null): " + messageService.findById(m1.getId()));

        System.out.println("\n========== [5. 채널 삭제] ==========");
        // '자바-기초' 채널 삭제 전 상황: 메시지 9개 중 5개(하나 지움)가 이 채널 것
        System.out.println("'자바-기초' 삭제 전 채널 수: " + channelService.findAll().size());

        channelService.delete(c1.getId());

        System.out.println("'자바-기초' 삭제 후 채널 수: " + channelService.findAll().size() + " (기대값: 1)"
                            + " \n===========모든 채널 출력===========");

        for (Channel c : channelService.findAll()) {
            System.out.println("채널명 : " + c.getName());
            System.out.println("설명 : " + c.getDescription());
            System.out.println("ID : " + c.getId());
            System.out.println("----------------------------------------------------");
        }

        System.out.println("\n========= 모든 테스트 시나리오 종료 =========");
    }
}