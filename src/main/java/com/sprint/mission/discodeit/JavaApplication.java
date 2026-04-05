package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
//import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
//import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {

//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
////        MessageService messageService = new JCFMessageService();
//        // 의존성 주입 후 -> 아래 코드로 수정
//        MessageService messageService =
//                new JCFMessageService(userService, channelService);
//
//        User user1 = new User("yesungyoo", "yesungyoo@example.com", "1234");
//        User user2 = new User("taktak", "taktak@example.com", "5678");
//        User user3 = new User("parkjihae", "parkjihae@example.com", "9999");
//
//        userService.create(user1);
//        userService.create(user2);
//        userService.create(user3);
//
//        List<User> participants = new ArrayList<>();
//        participants.add(user1);
//        participants.add(user2);
//        participants.add(user3);
//
//        Channel channel1 = new Channel("general", participants);
//        channelService.create(channel1);
//
//        Message message1 = new Message("안녕하세요", user1, channel1);
//        Message message2 = new Message("반가워요", user2, channel1);
//        Message message3 = new Message("저도 왔어요!", user3, channel1);
//
//        messageService.send(message1);
//        messageService.send(message2);
//        messageService.send(message3);
//
//        System.out.println("\n==============================");
//        System.out.println("         등록 완료");
//        System.out.println("==============================");
//
//        System.out.println("\n[User 단건 조회]");
//        System.out.println(userService.getUser(user1.getId()));
//
//        System.out.println("\n[User 전체 조회]");
//        System.out.println(userService.findAll());
//
//        System.out.println("\n[Channel 단건 조회]");
//        System.out.println(channelService.getChannel(channel1.getId()));
//
//        System.out.println("\n[채널 참가자 목록]");
//        for (User user : channel1.getParticipants()) {
//            System.out.println(user.getUsername());
//        }
//
//        System.out.println("\n[Channel 전체 조회]");
//        System.out.println(channelService.findAll());
//
//        System.out.println("\n[Message 단건 조회]");
//        System.out.println(messageService.getMessage(message1.getId()));
//
//        System.out.println("\n[채팅방 화면]");
//        System.out.println("[" + channel1.getChannelName() + "]");
//        for (Message message : messageService.findAllByChannelId(channel1.getId())) {
//            System.out.println(message.getDisplayMessage());
//        }
//
//        userService.update(user1.getId(), "yesungyoo_new", "new_yesungyoo@example.com", "1111");
//        channelService.update(channel1.getId(), "free-talk");
//        messageService.edit(message1.getId(), "안녕하세요!! 수정된 메시지입니다.");
//
//        System.out.println("\n==============================");
//        System.out.println("         수정 완료");
//        System.out.println("==============================");
//
//        System.out.println("\n[수정된 User 조회]");
//        System.out.println(userService.getUser(user1.getId()));
//
//        System.out.println("\n[수정된 채널 참가자 목록]");
//        for (User user : channel1.getParticipants()) {
//            System.out.println(user.getUsername());
//        }
//
//        System.out.println("\n[수정된 Channel 조회]");
//        System.out.println(channelService.getChannel(channel1.getId()));
//
//
//        System.out.println("\n[수정된 Message 조회]");
//        System.out.println(messageService.getMessage(message1.getId()));
//
//        System.out.println("\n[수정 후 채팅방 화면]");
//        System.out.println("[" + channel1.getChannelName() + "]");
//        for (Message message : messageService.findAllByChannelId(channel1.getId())) {
//            System.out.println(message.getDisplayMessage());
//        }
//
//        userService.delete(user2.getId());
//        messageService.remove(message2.getId());
//        channelService.delete(channel1.getId());
//
//        System.out.println("\n==============================");
//        System.out.println("         삭제 완료");
//        System.out.println("==============================");
//
//        System.out.println("\n[user 삭제 확인]");
//        System.out.println("삭제된 user2 조회: " + userService.getUser(user2.getId()));
//        channel1.removeParticipant(user2);
//
//        System.out.println("\n[삭제 후 참가자 목록]");
//        for (User user : channel1.getParticipants()) {
//            System.out.println(user.getUsername());
//        }
//        System.out.println("\n[message 삭제 확인]");
//        System.out.println("삭제된 message2 조회: " + messageService.getMessage(message2.getId()));
//
//        System.out.println("\n[삭제 후 채팅방 화면]");
//        System.out.println("[" + channel1.getChannelName() + "]");
//        for (Message message : messageService.findAllByChannelId(channel1.getId())) {
//            System.out.println(message.getDisplayMessage());
//        }
//        System.out.println("\n[channel 삭제 확인]");
//        System.out.println("삭제된 channel1 조회: " + channelService.getChannel(channel1.getId()));

        // 2차
        deleteFile("users.ser");
        deleteFile("channels.ser");
        deleteFile("messages.ser");

        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(userService, channelService);

        // -------------------- 등록 --------------------
        System.out.println("=== 등록 ===");

        User user1 = new User("YesungYoo", "yys03838@gmail.com", "1234");
        User user2 = new User("yababab", "parlwodkfjk@gmail.com", "5678");
        userService.create(user1);
        userService.create(user2);

        Channel channel1 = new Channel("공지방", new ArrayList<>());
        Channel channel2 = new Channel("잡담방", new ArrayList<>());
        channelService.create(channel1);
        channelService.create(channel2);

        channelService.join(channel1.getId(), user1);
        channelService.join(channel1.getId(), user2);

        Message message1 = new Message("안녕하세요", user1, channel1);
        Message message2 = new Message("반갑습니다", user2, channel1);
        messageService.send(message1);
        messageService.send(message2);

        System.out.println("등록 완료");
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println(message1);
        System.out.println(message2);

        // -------------------- 조회(단건) --------------------
        System.out.println("\n=== 조회(단건) ===");
        System.out.println("user1 조회: " + userService.getUser(user1.getId()));
        System.out.println("channel1 조회: " + channelService.getChannel(channel1.getId()));
        System.out.println("message1 조회: " + messageService.getMessage(message1.getId()));

        // -------------------- 조회(다건) --------------------
        System.out.println("\n=== 조회(다건) ===");
        System.out.println("[전체 유저]");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        System.out.println("\n[전체 채널]");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel);
        }

        System.out.println("\n[channel1의 전체 메시지]");
        for (Message message : messageService.findAllByChannelId(channel1.getId())) {
            System.out.println(message.getDisplayMessage());
        }

        // -------------------- 수정 --------------------
        System.out.println("\n=== 수정 ===");
        userService.update(user1.getId(), "user(수정됨)", "updated@gmail.com", "9999");
        channelService.update(channel1.getId(), "공지방(수정됨)");
        messageService.edit(message1.getId(), "메시지(수정됨)");

        System.out.println("수정 완료");

        // -------------------- 수정된 데이터 조회 --------------------
        System.out.println("\n=== 수정된 데이터 조회 ===");
        System.out.println("수정된 user1 조회: " + userService.getUser(user1.getId()));
        System.out.println("수정된 channel1 조회: " + channelService.getChannel(channel1.getId()));
        System.out.println("수정된 message1 조회: " + messageService.getMessage(message1.getId()));

        // -------------------- 삭제 --------------------
        System.out.println("\n=== 삭제 ===");
        userService.delete(user2.getId());
        messageService.remove(message2.getId());
        channelService.delete(channel2.getId());

        System.out.println("삭제 완료");

        // -------------------- 삭제 확인 --------------------
        System.out.println("\n=== 삭제 확인 ===");
        System.out.println("삭제된 user2 조회: " + userService.getUser(user2.getId()));
        System.out.println("삭제된 message2 조회: " + messageService.getMessage(message2.getId()));
        System.out.println("삭제된 channel2 조회: " + channelService.getChannel(channel2.getId()));

        // -------------------- 최종 다건 조회 --------------------
        System.out.println("\n=== 최종 전체 조회 ===");
        System.out.println("[전체 유저]");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        System.out.println("\n[전체 채널]");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel);
        }

        System.out.println("\n[남아있는 channel1 메시지]");
        for (Message message : messageService.findAllByChannelId(channel1.getId())) {
            System.out.println(message.getDisplayMessage());
        }
    }

    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
   }
}
