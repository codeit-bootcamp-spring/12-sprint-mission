package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.CreateChannelRequest;
import com.sprint.mission.discodeit.DTO.CreateUserRequest;
import com.sprint.mission.discodeit.DTO.MessageData;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {

        //jctTest();
        //fileTest();
        //basicFileTest();
        basicJCFTest();
    }

    //region jct
    public static void jctTest() {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        jctUserTest(userService);
        jctChannelTest(userService, channelService);
        jctMessageTest(userService, channelService, messageService);
    }

    public static void jctUserTest(UserService userService) {
        System.out.println();
        System.out.println("==================== JCF TEST (User) ====================");
        System.out.println("데이터 검증 및 등록");

        //데이터 검증
        runSafely("유저 생성", () -> {
            userService.createUser(new CreateUserRequest("", "", "", ""));
        });

        //데이터 검증
        runSafely("유저 생성", () -> {
            userService.createUser(new CreateUserRequest("장현우", "1234", "gusdn6763gmail.com", "발렌"));
        });

        //유저 생성 -> 유저 닉네임 찾기 -> 찾은 닉네임 바꾸기 -> 모든 유저 조회 -> 유저 삭제 -> 모든 유저 조회
        runSafely("", () -> {
            System.out.println("\n등록한 유저:");
            User user1 = userService.createUser(new CreateUserRequest("장", "1", "abc123@gmail.com", "abc123"));
            User user2 = userService.createUser(new CreateUserRequest("장", "1", "abc456@gmail.com", "abc456"));
            System.out.println(user1.toString() + "\n" + user2.toString());

            System.out.println("\n데이터 조회");
            User currentUser = userService.findUserByNickname(user1.getNickname());
            System.out.println("찾은 유저: " + currentUser.toString());

            System.out.println("\n조회된 데이터 수정");
            User changedUser = userService.changeUserNickname(currentUser.getId(), "ChangedUser");
            System.out.println("수정된 유저: " + changedUser.toString());

            System.out.println("\n모든 유저: ");
            List<User> userList = userService.findAllUsers();
            for (User user : userList) {
                System.out.println(user.toString());
            }

            System.out.println("\n데이터 삭제");
            User deletedUser = userService.deleteUser(changedUser.getId());
            System.out.println("삭제된 유저: " + deletedUser.toString());

            System.out.println("\n조회를 통해 삭제되었는지 확인");
            System.out.println("모든 유저: ");
            userList.clear();
            userList = userService.findAllUsers();
            for (User user : userList) {
                System.out.println(user.toString());
            }
        });
    }

    public static void jctChannelTest(UserService userService, ChannelService channelService) {
        System.out.println();
        System.out.println("==================== JCF TEST (Channel) ====================");
        System.out.println("데이터 검증 및 등록");

        User user1 = userService.createUser(new CreateUserRequest("채널 주인", "1", "test1@gmail.com", "주인"));

        //데이터 검증
        runSafely("채널 생성", () -> {
            channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "", false));
        });

        //채널 생성 -> 채널 이름 찾기 -> 채널 이름 바꾸기 -> 모든 채널 조회 -> 채널 삭제 -> 모든 채널 조회
        runSafely("", () -> {
            System.out.println("\n등록한 채널:");
            Channel ch1 = channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "깡통", false));
            Channel ch2 = channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "깡통2", true));
            System.out.println(ch1.toString() + "\n" + ch2.toString());

            System.out.println("\n데이터 조회");
            Channel currentChannel = channelService.findChannelByName(ch1.getName());
            System.out.println("찾은 채널: " + currentChannel.toString());

            System.out.println("\n조회된 데이터 수정");
            Channel changedChannel = channelService.changeChannelName(currentChannel.getId(), "깡통2");
            System.out.println("수정된 채널: " + changedChannel.toString());

            System.out.println("\n모든 채널: ");
            List<Channel> channelList = channelService.findAllChannels();
            for (Channel ch : channelList) {
                System.out.println(ch.toString());
            }

            System.out.println("\n데이터 삭제");
            Channel deletedChannel = channelService.deleteChannel(changedChannel.getId());
            System.out.println("삭제된 채널: " + deletedChannel.toString());

            System.out.println("\n조회를 통해 삭제되었는지 확인");
            System.out.println("모든 채널: ");
            channelList.clear();
            channelList = channelService.findAllChannels();
            for (Channel ch : channelList) {
                System.out.println(ch.toString());
            }
        });
    }

    public static void jctMessageTest(UserService userService, ChannelService channelService, MessageService messageService) {
        System.out.println();
        System.out.println("==================== JCF TEST (Message) ====================");
        System.out.println("데이터 검증 및 등록");

        // 사전 데이터 준비 (유저 + 채널)
        User user1 = userService.createUser(new CreateUserRequest("작성자", "1", "msg@test.com", "작성자"));
        Channel ch1 = channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "메시지채널", false));

        // 데이터 검증
        runSafely("메시지 생성", () -> {
            messageService.createMessage(new MessageData(null, ch1.getId(), "내용"));
        });

        // 데이터 검증
        runSafely("메시지 생성", () -> {
            messageService.createMessage(new MessageData(user1.getId(), null, "내용"));
        });

        // 데이터 검증
        runSafely("메시지 생성", () -> {
            messageService.createMessage(new MessageData(user1.getId(), ch1.getId(), ""));
        });

        // 메시지 생성 → 조회 → 수정 → 전체 조회 → 삭제 → 검증
        runSafely("", () -> {
            System.out.println("\n등록한 메시지:");
            Message msg1 = messageService.createMessage(new MessageData(user1.getId(), ch1.getId(), "첫 번째 메시지"));
            Message msg2 = messageService.createMessage(new MessageData(user1.getId(), ch1.getId(), "두 번째 메시지"));
            System.out.println(msg1.toString() + "\n" + msg2.toString());

            System.out.println("\n데이터 조회");
            Message currentMessage = messageService.findMessageByContent(msg1.getContent());
            System.out.println("찾은 메시지: " + currentMessage.toString());

            System.out.println("\n조회된 데이터 수정");
            Message changedMessage = messageService.changeMessageContent(currentMessage.getId(), "수정된 메시지");
            System.out.println("수정된 메시지: " + changedMessage.toString());

            System.out.println("\n모든 메시지:");
            List<Message> messageList = messageService.findAllMessage();
            for (Object msg : messageList) {
                System.out.println(msg.toString());
            }

            System.out.println("\n데이터 삭제");
            Message deletedMessage = messageService.deleteMessage(changedMessage.getId());
            System.out.println("삭제된 메시지: " + deletedMessage.toString());

            System.out.println("\n조회를 통해 삭제되었는지 확인");
            messageList.clear();
            messageList = messageService.findAllMessage();
            for (Object msg : messageList) {
                System.out.println(msg.toString());
            }
        });
    }
    //endregion

    //region file
    public static void fileTest() {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        fileUserTest(userService);
        fileChannelTest(userService, channelService);
        fileMessageTest(userService, channelService, messageService);
    }

    public static void fileUserTest(UserService userService) {
        System.out.println();
        System.out.println("==================== File TEST (User) ====================");

        //불러오기 -> 만약 데이터가 없으면 최초 생성하고 저장 -> 모든 유저 조회
        runSafely("", () -> {
            if (userService.findAllUsers().isEmpty()) {
                System.out.println("\n데이터가 존재하지 않아 생성");
                User user1 = userService.createUser(new CreateUserRequest("장", "1", "abc123@gmail.com", "abc123"));
                User user2 = userService.createUser(new CreateUserRequest("장", "1", "abc456@gmail.com", "abc456"));

                System.out.println(user1.toString());
                System.out.println(user2.toString());
            }

            System.out.println("\n불러온 데이터");
            List<User> userList = userService.findAllUsers();
            for (User user : userList) {
                System.out.println(user.toString());
            }
        });
    }

    public static void fileChannelTest(UserService userService, ChannelService channelService) {
        System.out.println();
        System.out.println("==================== File TEST (Channel) ====================");
        System.out.println("데이터 검증 및 등록");

        runSafely("", () -> {
            if (channelService.findAllChannels().isEmpty()) {
                System.out.println("\n데이터가 존재하지 않아 생성");
                User user1 = userService.createUser(new CreateUserRequest("채널 주인", "1", "test1@gmail.com", "주인"));

                channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "깡깡", false));
                channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "깡깡깡", false));
            }

            System.out.println("\n불러온 데이터");
            List<Channel> channelList = channelService.findAllChannels();
            for (Channel ch : channelList) {
                System.out.println(ch.toString());
            }
        });
    }

    public static void fileMessageTest(UserService userService, ChannelService channelService, MessageService messageService) {
        System.out.println();
        System.out.println("==================== File TEST (Message) ====================");
        System.out.println("데이터 검증 및 등록");

        if (messageService.findAllMessage().isEmpty()) {
            System.out.println("\n데이터가 존재하지 않아 생성");
            User user1 = userService.createUser(new CreateUserRequest("작성자", "1", "msg@test.com", "작성자"));
            Channel ch1 = channelService.createChannel(new CreateChannelRequest(user1.getId(), ChannelType.FORM, "메시지채널", false));

            Message msg1 = messageService.createMessage(new MessageData(user1.getId(), ch1.getId(), "첫 번째 메시지"));
            Message msg2 = messageService.createMessage(new MessageData(user1.getId(), ch1.getId(), "두 번째 메시지"));

            System.out.println(user1.toString());
            System.out.println(ch1.toString());
            System.out.println(msg1.toString());
            System.out.println(msg2.toString());
        };

        System.out.println("\n불러온 데이터");
        List<Message> messageList = messageService.findAllMessage();
        for (Object msg : messageList) {
            System.out.println(msg.toString());
        }
    }
    //endregion

    //region basic
    public static void basicFileTest() {
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository);

        fileUserTest(userService);
        fileChannelTest(userService, channelService);
        fileMessageTest(userService, channelService, messageService);
    }

    public static void basicJCFTest() {
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository);

        jctUserTest(userService);
        jctChannelTest(userService, channelService);
        jctMessageTest(userService, channelService, messageService);
    }
    //endregion

    //함수형 인터페이스
    private static void runSafely(String actionName, Runnable action) {
        try {
            action.run();
            System.out.println(actionName + "성공");
        } catch (Exception e) {
            System.out.println(actionName + "실패: " + e.getMessage());
        }
    }
}