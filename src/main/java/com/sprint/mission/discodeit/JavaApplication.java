package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

// 메인 메소드가 선언된 JavaApplication 클래스를 선언하고, 도메인 별 서비스 구현체를 테스트해보세요.
//[O] 등록
//[O] 조회(단건, 다건)
//[O] 수정
//[O] 수정된 데이터 조회
//[O] 삭제
//[O] 조회를 통해 삭제되었는지 확인
//[O] 메시지 검증 되었는지 (id랑 채널 유무에 따라 메시지 검증)
//[O] 싱글톤 구현 (getInstance())
//[O] stream api 구현
//public class JavaApplication {
//    public static void main(String[] args) {
//
//        UserService userService =  JCFUserService.getInstance();
//        ChannelService channelService = JCFChannelService.getInstance();
//        MessageService messageService = JCFMessageService.getInstance(userService, channelService);
//
//        System.out.println("============= USER 테스트 시작 =============");
//
//        // 1. 등록
//        User user1 = new User("test1", "test1@email.com", "1234", "홍길동");
//        User user2 = new User("test2", "test2@email.com", "1234", "고길동");
//        userService.save(user1);
//        userService.save(user2);
//
//        // 2. 조회(단건)
//        System.out.println("[User 단건 조회]");
//        System.out.println(userService.findById(user1.getId()));
//
//        // 3. 조회(다건)
//        System.out.println("[User 다건 조회]");
//        for (User user : userService.findAll()) {
//            System.out.println(user);
//        }
//
//
//        // 4. 수정
//        user1.update("updateTest1", "update1@email.com", "1111", "홍홍홍홍");
//        userService.update(user1);
//
//        // 5. 수정된 데이터 조회
//        System.out.println("[User 수정 후 조회]");
//        System.out.println(userService.findById(user1.getId()));
//
//
//        // 6. 삭제
//        userService.delete(user2.getId());
//
//        // 7. 조회를 통해 삭제되었는지 확인
//        System.out.println("[User 삭제 후 조회]");
//        System.out.println(userService.findById(user2.getId())); // null 기대
//
//        System.out.println("============= USER 테스트 끝 =============\n");
//
//
//        System.out.println("============= CHANNEL 테스트 시작 =============");
//
//        // 1. 등록
//        Channel channel1 = new Channel("공지방", "공지사항 채널");
//        Channel channel2 = new Channel("자유방", "자유 대화 채널");
//        channelService.save(channel1);
//        channelService.save(channel2);
//
//        // 2. 조회(단건)
//        System.out.println("[Channel 단건 조회]");
//        System.out.println(channelService.findById(channel1.getId()));
//
//        // 3. 조회(다건)
//        System.out.println("[Channel 다건 조회]");
//        for (Channel channel : channelService.findAll()) {
//            System.out.println(channel);
//        }
//
//        // 4. 수정
//        channel1.update("수정된공지방", "수정된 공지사항 채널");
//        channelService.update(channel1);
//
//        // 5. 수정된 데이터 조회
//        System.out.println("[Channel 수정 후 조회]");
//        System.out.println(channelService.findById(channel1.getId()));
//
//        // 6. 삭제
//        channelService.delete(channel2.getId());
//
//        // 7. 조회를 통해 삭제되었는지 확인
//        System.out.println("[Channel 삭제 후 조회]");
//        System.out.println(channelService.findById(channel2.getId())); // null 기대
//
//        System.out.println("============= CHANNEL 테스트 끝 =============\n");
//
//
//        System.out.println("============= MESSAGE 테스트 시작 =============");
//
//        // 1. 등록
//        Message message1 = new Message("안녕하세요", user1.getId(), channel1.getId());
//        Message message2 = new Message("반갑습니다", user1.getId(), channel1.getId());
//        messageService.save(message1);
//        messageService.save(message2);
//
//        // 2. 조회(단건)
//        System.out.println("[Message 단건 조회]");
//        System.out.println(messageService.findById(message1.getId()));
//
//        // 3. 조회(다건)
//        System.out.println("[Message 다건 조회]");
//        for (Message message : messageService.findAll()) {
//            System.out.println(message);
//        }
//
//        // 4. 수정
//        message1.update("수정된 메시지입니다.");
//        messageService.update(message1);
//
//        // 5. 수정된 데이터 조회
//        System.out.println("[Message 수정 후 조회]");
//        System.out.println(messageService.findById(message1.getId()));
//
//        // 6. 삭제
//        messageService.delete(message2.getId());
//
//        // 7. 조회를 통해 삭제되었는지 확인
//        System.out.println("[Message 삭제 후 조회]");
//        System.out.println(messageService.findById(message2.getId())); // null 기대
//
//        System.out.println("============= MESSAGE 테스트 끝 =============");
//
//        System.out.println("============= MESSAGE 검증 테스트 시작 =============");
//
//        // 정상 케이스
//        Message validMessage = new Message("정상 메시지", user1.getId(), channel1.getId());
//        System.out.println("[정상 저장]");
//        System.out.println(messageService.save(validMessage));
//
//        // 없는 사용자
//        Message invalidUserMessage = new Message("없는 사용자 메시지", UUID.randomUUID(), channel1.getId());
//        System.out.println("[없는 사용자 저장 시도]");
//        System.out.println(messageService.save(invalidUserMessage)); // null 기대
//
//        // 없는 채널
//        Message invalidChannelMessage = new Message("없는 채널 메시지", user1.getId(), UUID.randomUUID());
//        System.out.println("[없는 채널 저장 시도]");
//        System.out.println(messageService.save(invalidChannelMessage)); // null 기대
//
//        System.out.println("============= MESSAGE 검증 테스트 끝 =============");
//    }
//}

//[ ]  JCF*Repository  구현체를 활용하여 테스트해보세요.

public class JavaApplication {

    static User setupUser(UserService userService) {
        User user = new User("woody", "woody@codeit.com", "woody1234", "우디");
        return userService.save(user);
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = new Channel("공지", "공지 채널입니다.");
        return channelService.save(channel);
    }

    static void messageCreateTest(MessageService messageService, User author, Channel channel) {
        Message message = new Message("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: " + messageService.save(message).getId());
    }

    public static void main(String[] args) {
        // Repository 초기화
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        // Service 초기화
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository);

        // 셋업
        User user = setupUser(userService);
        System.out.println(user);

        Channel channel = setupChannel(channelService);
        System.out.println(channel);

        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}

// [ ]  File*Repository 구현체를 활용하여 테스트해보세요.
//public class JavaApplication {
//
//    static User setupUser(UserService userService) {
//        User user = new User("woody", "woody@codeit.com", "woody1234", "우디");
//        return userService.save(user);
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = new Channel("공지", "공지 채널입니다.");
//        return channelService.save(channel);
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = new Message("안녕하세요.", channel.getId(), author.getId());
//        message = messageService.save(message);
//        System.out.println("메시지 생성: " + message.getId());
//    }
//
//    public static void main(String[] args) {
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//
//        UserService userService = new BasicUserService(userRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository);
//        MessageService messageService = new BasicMessageService(messageRepository);
//
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//        messageCreateTest(messageService, channel, user);
//    }
//}