package com.sprint.mission.discodeit;

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

public class JavaApplication {

    public static void runTest(UserService us, ChannelService cs, MessageService ms) {

        System.out.println("------------ 사용자 테스트 시작---------------\n");
        System.out.println("------------ 사용자 생성 시작---------------\n");
        User user = us.create("김태오", "teoh1234@1234.com", "teoh1234");
        User user1 = us.create("주찬빈", "chan1234@1234.com", "chan1234");
        User user2 = us.create("음정윤", "eum1234@1234.com", "eum1234");
        User user3 = us.create("김인제", "inje@1234.com", "inje1234");
        System.out.println(user);
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
        System.out.println("");
        System.out.println("------------ 사용자 생성 끝---------------\n");

        System.out.println("------------ 사용자 전체 및 단건 조회 시작---------------\n");
        System.out.println(us.findAll());
        System.out.println(us.findById(user.getUserId()));
        System.out.println("");
        System.out.println("------------ 사용자 전체 및 단건 조회 끝---------------\n");

        System.out.println("------------ 사용자 정보 수정 후 조회 시작---------------\n");
        System.out.println(us.updateUsername(user.getUserId(), "김태오1"));
        System.out.println(us.updateEmail(user1.getUserId(), "joo1234@1234.com"));
        System.out.println(us.updatePassword(user2.getUserId(), "eum12345"));
        System.out.println("");
        System.out.println("------------ 사용자 정보 수정 후 조회 끝---------------\n");

        System.out.println("------------ 사용자 삭제 후 사용자 리스트 조회 시작---------------\n");
        us.deleteById(user3.getUserId());
        System.out.println(us.findAll());
        System.out.println("");
        System.out.println("------------ 사용자 삭제 후 사용자 리스트 조회 끝---------------\n");

        System.out.println("----------------------채널 테스트 시작--------------------------\n");
        System.out.println("------------ 채널 생성 시작---------------\n");
        Channel channel = cs.create(ChannelType.PUBLIC, "김태오의 채널", "김태오 채널입니다.");
        Channel channel1 = cs.create(ChannelType.PUBLIC, "주찬빈의 채널", "주찬빈 채널입니다.");
        Channel channel2 = cs.create(ChannelType.PRIVATE, "음정윤의 채널", "음정윤 채널입니다.");
        System.out.println(channel);
        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println("");
        System.out.println("------------ 채널 생성 끝---------------\n");

        System.out.println("------------ 채널 전체 및 단건 조회 시작---------------\n");
        System.out.println(cs.findAll());
        System.out.println(cs.findById(channel.getChannelId()));
        System.out.println("");
        System.out.println("------------ 채널 전체 및 단건 조회 끝---------------\n");

        System.out.println("------------ 채널 정보 수정 후 조회 시작---------------\n");
        System.out.println(cs.updateChannelName(channel.getChannelId(), "김태오의 채널1"));
        System.out.println("");
        System.out.println("------------ 채널 정보 수정 후 조회 끝---------------\n");

        System.out.println("------------ 채널 삭제 후 채널 리스트 조회 시작---------------\n");
        cs.deleteById(channel2.getChannelId());
        System.out.println(cs.findAll());
        System.out.println("");
        System.out.println("------------ 채널 삭제 후 채널 리스트 조회 끝---------------\n");

        System.out.println("---------------------- 메세지 테스트 시작--------------------------\n");
        System.out.println("------------ 메세지 생성 시작---------------\n");

        Message message = ms.create("안녕하세요 태오입니다", user.getUserId(), channel.getChannelId());
        Message message1 = ms.create("안녕하세요 찬빈입니다", user1.getUserId(), channel.getChannelId());
        Message message2 = ms.create("안녕하세요 정윤입니다", user2.getUserId(), channel1.getChannelId());

        System.out.println(message);
        System.out.println(message1);
        System.out.println(message2);
        System.out.println("");
        System.out.println("------------ 메세지 생성 끝---------------\n");

        System.out.println("------------ 메세지 전체 및 단건 조회 시작---------------\n");
        System.out.println(ms.findAll());
        System.out.println(ms.findById(message2.getMessageId()));
        System.out.println("");
        System.out.println("------------ 메세지 전체 및 단건 조회 끝---------------\n");

        System.out.println("------------ 메세지 정보 수정 후 조회 시작---------------\n");
        System.out.println(ms.updateContent(message1.getMessageId(), "안녕하세요 주찬빈입니다"));
        System.out.println("");
        System.out.println("------------ 메세지 정보 수정 후 조회 끝---------------\n");

        System.out.println("------------ 메세지 삭제 후 메세지 리스트 조회 시작---------------\n");
        ms.deleteById(message1.getMessageId());
        System.out.println(ms.findAll());
        System.out.println("");
        System.out.println("------------ 메세지 삭제 후 메세지 리스트 조회 끝---------------\n");

        System.out.println("------------ 삭제된 채널 / 사용자로 메세지 생성 예외 확인 시작---------------\n");
        try {
            ms.create("안녕하세요 음정윤입니다", user2.getUserId(), channel2.getChannelId());
        } catch (IllegalArgumentException e) {
            System.out.println("메세지 저장 실패 : " + e.getMessage());
        }

        try {
            ms.create("안녕하세요 인제입니다", user3.getUserId(), channel1.getChannelId());
        } catch (IllegalArgumentException e) {
            System.out.println("메세지 저장 실패 : " + e.getMessage());
        }
        System.out.println("------------ 삭제된 채널 / 사용자로 메세지 생성 예외 확인 끝---------------\n");
    }

    public static void main(String[] args) {

        System.out.println("==================== JCF 테스트 시작 ====================\n");

        UserRepository jcfUserRepository = new JCFUserRepository();
        ChannelRepository jcfChannelRepository = new JCFChannelRepository();
        MessageRepository jcfMessageRepository = new JCFMessageRepository();

        UserService jcfUserService = new BasicUserService(jcfUserRepository);
        ChannelService jcfChannelService = new BasicChannelService(jcfChannelRepository);
        MessageService jcfMessageService = new BasicMessageService(
                jcfMessageRepository, jcfUserRepository, jcfChannelRepository
        );

        runTest(jcfUserService, jcfChannelService, jcfMessageService);

        System.out.println("==================== JCF 테스트 끝 ====================\n");

        System.out.println("==================== FILE 테스트 시작 ====================\n");

        UserRepository fileUserRepository = new FileUserRepository();
        ChannelRepository fileChannelRepository = new FileChannelRepository();
        MessageRepository fileMessageRepository = new FileMessageRepository();

        UserService fileUserService = new BasicUserService(fileUserRepository);
        ChannelService fileChannelService = new BasicChannelService(fileChannelRepository);
        MessageService fileMessageService = new BasicMessageService(
                fileMessageRepository, fileUserRepository, fileChannelRepository
        );

        runTest(fileUserService, fileChannelService, fileMessageService);

        System.out.println("==================== FILE 테스트 끝 ====================\n");
    }
}