package com.sprint.mission.discodeit;

public class JavaApplication {
//    static UserCreateRequestDto setupUser(UserService userService) {
//
//        return userService.create(new UserCreateRequestDto("홍길동", "dong@codeit.com", "dongdong"));
//
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널");
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.create("메시지1.", channel.getId(), author.getId());
//        System.out.println("메시지 Id: " + message.getId() + ", 내용 : " + message.getContent() + ", 보낸 사람 : " + message.getAuthorId()  );
//    }
//
//    public static void main(String[] args) {
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//
//        UserService userService = new BasicUserService(userRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);
//
//        User user = setupUser(userService);
//        Channel channel = setupChannel(channelService);
//
//        messageCreateTest(messageService, channel, user);
//
//        userService.delete(user.getId());
//    }
}
