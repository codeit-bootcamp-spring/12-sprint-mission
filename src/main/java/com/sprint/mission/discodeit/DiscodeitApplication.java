package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.binaryContent.MessageImageCreateRequestDto;
import com.sprint.mission.discodeit.dto.binaryContent.ProfileImageCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    static UserResponseDto setupUser(UserService userService) {

        byte[] dummyImage = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        return userService.create(new UserCreateRequestDto("홍길동", "dong@codeit.com", "dongdong", dummyImage));

    }

//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.create(ChannelType.PUBLIC, "공지1", "공지 채널");
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, UserResponseDto author) {
//        Message message = messageService.create("메시지1", channel.getId(), author.id());
//        System.out.println("메시지 Id: " + message.getId() + ", 내용 : " + message.getContent() + ", 보낸 사람 : " + message.getAuthorId());
//    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
        BasicAuthService basicAuthService = context.getBean(BasicAuthService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

//
//        UserResponseDto user = setupUser(userService);
//        System.out.println(user);
//        Channel channel = setupChannel(channelService);
//
//        messageCreateTest(messageService, channel, user);
        try {
            // 1. Create
            System.out.println("유저 생성 (이미지 포함)");
            byte[] initialImage = new byte[]{1, 2, 3, 4, 5};
            UserResponseDto user1 = userService.create(new UserCreateRequestDto(
                    "홍길동", "hong@codeit.com", "password123", initialImage
            ));
            System.out.println("생성 성공: " + user1.username() + " (이미지ID: " + user1.profileImageId() + ")");

            // 2. Create (추가 생성 - 이미지 없음)
            userService.create(new UserCreateRequestDto(
                    "이순신", "lee@codeit.com", "pw456", null
            ));
            System.out.println("생성 성공: 이순신 (이미지 없음)");

            // 3. FindAll
            System.out.println("전체 조회");
            List<UserResponseDto> allUsers = userService.findAll();
            allUsers.forEach(u -> System.out.println(
                    String.format(" - [%s] 이메일: %s | 온라인: %b | 이미지보유: %b",
                            u.username(), u.email(), u.isOnline(), u.profileImageId() != null)
            ));

            // 4. Update (선택적 수정 테스트 - 이미지 교체)
            System.out.println("유저 수정 (프로필 이미지 대체)");
            UUID oldImageId = user1.profileImageId();
            byte[] newImage = new byte[]{9, 8, 7};
            UserResponseDto updatedUser = userService.update( new UserUpdateRequestDto(
                    user1.id(), "홍길동_수정", null,"1234" ,newImage
            ));

            System.out.println("수정 완료: " + updatedUser.username());
            System.out.println("기존 이미지ID: " + oldImageId);
            System.out.println("신규 이미지ID: " + updatedUser.profileImageId());
            System.out.println("결과: " + (!updatedUser.profileImageId().equals(oldImageId) ? "이미지 교체 성공!" : "교체 실패"));

            // 5. Delete
            System.out.println("유저 삭제 (Cascade 삭제 검증)");
            userService.delete(user1.id());
            System.out.println("삭제 완료: " + user1.id());

            // 삭제 확인 조회
            try {
                userService.find(user1.id());
                System.out.println("오류: 삭제된 유저가 여전히 조회됩니다.");
            } catch (Exception e) {
                System.out.println("검증 성공: 삭제된 유저는 더 이상 조회되지 않습니다. (" + e.getMessage() + ")");
            }

            System.out.println("=USER CRUD 모든 테스트가 성공적으로 종료되었습니다.=");

        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생");
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("=== ChannelService 고도화 테스트 시작 ===");

        byte[] dummyImage = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        UserResponseDto user1 = userService.create(new UserCreateRequestDto("홍길동", "dong@codeit.com", "dongdong", dummyImage));
        UserResponseDto user2 = userService.create(new UserCreateRequestDto("홍재동", "jae@codeit.com", "jaejae", dummyImage));
        UUID u1Id = user1.id();
        UUID u2Id = user2.id();

        ChannelResponseDto publicChannel = channelService.publicChannelCreate(
                new PublicChannelCreateRequestDto(ChannelType.PUBLIC, "자유게시판", "누구나 참여 가능")
        );
        System.out.println("PUBLIC 채널 생성 완료: " + publicChannel.name());

        ChannelResponseDto privateChannel = channelService.privateChannelCreate(
                new PrivateChannelCreateRequestDto(ChannelType.PRIVATE, List.of(u1Id))
        );
        System.out.println("PRIVATE 채널 생성 완료 (참여자: user1)");

        List<ChannelResponseDto> u1Channels = channelService.findAllByUserId(u1Id);
        System.out.println("User1 채널 목록 개수 (예상: 2): " + u1Channels.size());

        List<ChannelResponseDto> u2Channels = channelService.findAllByUserId(u2Id);
        System.out.println("User2 채널 목록 개수 (예상: 1): " + u2Channels.size());

        ChannelResponseDto foundChannel = channelService.find(publicChannel.id());
        System.out.println("채널 조회 시 최신 메시지 시간 존재 여부: " + (foundChannel.lastMessageAt() != null));

        // 6. UPDATE 제한 테스트 (PRIVATE 채널 수정 시도)
        try {
            channelService.update(new ChannelUpdateRequestDto(privateChannel.id(), ChannelType.PUBLIC, "비밀방수정", "수정시도"));
            System.out.println("FAIL: PRIVATE 채널이 수정되었습니다. (로직 오류)");
        } catch (IllegalArgumentException e) {
            System.out.println("SUCCESS: PRIVATE 채널 수정 제한 확인 - " + e.getMessage());
        }

        // 7. DELETE 연관 도메인 삭제 테스트
        channelService.delete(publicChannel.id());
        try {
            channelService.find(publicChannel.id());
        } catch (NoSuchElementException e) {
            System.out.println("SUCCESS: 채널 삭제 확인");
        }
        System.out.println("=== ChannelService 고도화 테스트 종료 ===");

        System.out.println("\n=== MessageService 고도화 테스트 시작 ===");

        UserResponseDto user = userService.create(new UserCreateRequestDto("홍영동", "yeong@codeit.com", "yeong", dummyImage));
        ChannelResponseDto channel = channelService.publicChannelCreate(
                new PublicChannelCreateRequestDto(ChannelType.PUBLIC, "테스트채널", "설명")
        );

        byte[] image1 = "fake-image-1".getBytes();
        byte[] image2 = "fake-image-2".getBytes();

        MessageCreateRequestDto createDto = new MessageCreateRequestDto(
                "사진 두 장 첨부합니다.",
                channel.id(),
                user.id(),
                List.of(image1, image2)
        );
        MessageResponseDto savedMessage = messageService.create(createDto);
        System.out.println("메시지 생성 완료. 첨부파일 개수(예상 2): " + savedMessage.binaryContentIds().size());

        List<MessageResponseDto> channelMessages = messageService.findAllByChannelId(channel.id());
        System.out.println("채널 메시지 목록 조회 성공. 첫 메시지 파일 ID 존재 여부: " + !channelMessages.get(0).binaryContentIds().isEmpty());

        byte[] newImage = "new-single-image".getBytes();
        MessageUpdateRequestDto updateDto = new MessageUpdateRequestDto(
                savedMessage.id(),
                savedMessage.channelId(),
                savedMessage.authorId(),
                "내용을 수정하고 사진을 한 장으로 바꿈",
                List.of(newImage)
        );
        MessageResponseDto updatedMessage = messageService.update(updateDto);
        System.out.println("메시지 수정 완료. 새 첨부파일 개수(예상 1): " + updatedMessage.binaryContentIds().size());

        List<UUID> remainingFiles = binaryContentRepository.findByMessageId(savedMessage.id());
        System.out.println("저장소 내 이전 파일 삭제 확인(남은 개수 예상 1): " + remainingFiles.size());

        MessageUpdateRequestDto updateTextOnlyDto = new MessageUpdateRequestDto(
                savedMessage.id(),
                savedMessage.channelId(),
                savedMessage.authorId(),
                "내용만 한 번 더 수정!",
                null
        );
        MessageResponseDto textUpdatedMessage = messageService.update(updateTextOnlyDto);
        System.out.println("내용만 수정 후 파일 유지 확인(예상 1): " + textUpdatedMessage.binaryContentIds().size());

        messageService.delete(savedMessage.id());
        List<UUID> filesAfterDelete = binaryContentRepository.findByMessageId(savedMessage.id());
        System.out.println("메시지 삭제 후 연관 파일 삭제 확인(예상 0): " + filesAfterDelete.size());

        try {
            messageService.findAllByChannelId(UUID.randomUUID()); // 존재하지 않는 채널
        } catch (NoSuchElementException e) {
            System.out.println("예외 처리 확인: " + e.getMessage());
        }

        System.out.println("=== MessageService 고도화 테스트 종료 ===\n");

        System.out.println("=== ReadStatus 기능 테스트 시작 ===");

        try {

            // --- 1. Create 테스트 ---
            System.out.println("[1] Create 테스트");
            ReadStatusCreateRequestDto createDto1 = new ReadStatusCreateRequestDto(user.id(), channel.id());
            ReadStatusResponseDto created = readStatusService.create(createDto1);
            System.out.println("생성 완료: " + created.id());

            // --- 2. 중복 생성 테스트 (예외 발생 확인) ---
            System.out.println("[2] 중복 생성 테스트");
            try {
                readStatusService.create(createDto1);
            } catch (IllegalArgumentException e) {
                System.out.println("중복 예외 포착 (정상): " + e.getMessage());
            }

            // --- 3. Find (단건 조회) 테스트 ---
            System.out.println("[3] Find 테스트");
            ReadStatusResponseDto found = readStatusService.find(created.id());
            System.out.println("조회 결과 ID: " + found.id());

            // --- 4. FindAllByUserId 테스트 ---
            System.out.println("[4] User별 목록 조회 테스트");
            List<ReadStatusResponseDto> userList = readStatusService.findAllByUserId(user.id());
            System.out.println("해당 유저의 읽음 상태 개수: " + userList.size());

            // --- 5. Update 테스트 ---
            System.out.println("[5] Update 테스트");
            Instant now = Instant.now();
            ReadStatusUpdateRequestDto updateDto1 = new ReadStatusUpdateRequestDto(created.id(), now);
            ReadStatusResponseDto updated = readStatusService.update(updateDto1);
            System.out.println("업데이트 시각 확인: " + updated.updatedAt());

            // --- 6. Delete 테스트 ---
            System.out.println("[6] Delete 테스트");
            readStatusService.delete(created.id());
            System.out.println("삭제 완료");

            // 삭제 후 다시 조회 시도
            try {
                readStatusService.find(created.id());
            } catch (NoSuchElementException e) {
                System.out.println("삭제 확인 완료 (정상): " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=== 테스트 종료 ===");
        System.out.println("\n=== UserStatusService 요구사항 검증 테스트 시작 ===");

// [준비] 테스트용 신규 유저 생성 (이때 BasicUserService 내부 로직에 의해 UserStatus도 자동 생성됨)
        UserResponseDto testUser = userService.create(
                new UserCreateRequestDto("상태검증", "status@test.com", "pw123", dummyImage)
        );
        UUID userId = testUser.id();

        try {
            // 1. [create] 관련 User가 존재하지 않으면 예외 발생 여부
            System.out.println("[테스트 1] 존재하지 않는 유저로 상태 생성 시도");
            try {
                userStatusService.create(new UserStatusCreateRequestDto(UUID.randomUUID()));
            } catch (NoSuchElementException e) {
                System.out.println("SUCCESS: 존재하지 않는 유저 예외 처리 확인");
            }

            // 2. [create] 이미 존재하는 유저의 상태 생성 시 예외 발생 여부 (중복 방지)
            System.out.println("[테스트 2] 이미 존재하는 유저의 상태 중복 생성 시도");
            try {
                // 이미 testUser 생성 시점에 내부적으로 UserStatus가 생성되었으므로 에러가 나야 함
                userStatusService.create(new UserStatusCreateRequestDto(userId));
            } catch (IllegalArgumentException e) {
                System.out.println("SUCCESS: 중복 생성 방지 예외 처리 확인");
            }

            // 3. [findAll] 모든 객체 조회 및 개수 확인
            System.out.println("[테스트 3] findAll - 전체 상태 목록 조회");
            List<UserStatusResponseDto> allStatuses = userStatusService.findAll();
            System.out.println("현재 등록된 상태 개수: " + allStatuses.size());

            // 4. [updateByUserId] userId로 특정 유저의 객체 업데이트
            System.out.println("[테스트 4] updateByUserId - 유저 ID 기반 접속 시간 업데이트");
            LocalDateTime updateTime = LocalDateTime.now().plusHours(1);
            UserStatusResponseDto updatedByUserId = userStatusService.updateByUserId(userId, updateTime);
            System.out.println("업데이트 성공 여부: " + updatedByUserId.lastConnectedAt().equals(updateTime));

            // 5. [find] id(PK)로 조회
            System.out.println("[테스트 5] find - 상태 객체 고유 ID(PK)로 조회");
            UserStatusResponseDto foundStatus = userStatusService.find(updatedByUserId.id());
            System.out.println("PK 조회 성공: " + (foundStatus.id().equals(updatedByUserId.id())));

            // 6. [update] DTO를 활용한 업데이트 (id 파라미터 + 수정할 값 파라미터 그룹화)
            System.out.println("[테스트 6] update - DTO 기반 업데이트");
            LocalDateTime finalTime = LocalDateTime.now().plusDays(1);
            UserStatusUpdateRequestDto updateDto1 = new UserStatusUpdateRequestDto(foundStatus.id(), finalTime);
            UserStatusResponseDto finalUpdated = userStatusService.update(updateDto1);
            System.out.println("DTO 업데이트 성공: " + finalUpdated.lastConnectedAt().equals(finalTime));

            // 7. [delete] id로 삭제
            System.out.println("[테스트 7] delete - 상태 객체 삭제");
            userStatusService.delete(foundStatus.id());
            try {
                userStatusService.find(foundStatus.id());
            } catch (NoSuchElementException e) {
                System.out.println("SUCCESS: 삭제 후 조회 불가 확인");
            }

        } catch (Exception e) {
            System.err.println("테스트 도중 예상치 못한 에러 발생!");
            e.printStackTrace();
        }

        System.out.println("=== UserStatusService 모든 테스트 종료 ===\n");

        System.out.println("\n=== BinaryContentService 요구사항 검증 테스트 시작 ===");

// 1. 기초 데이터 준비 (유저 및 메시지 ID 가상 생성)
        UUID testUserId = UUID.randomUUID();
        UUID testAuthorId = UUID.randomUUID();
        UUID testMessageId = UUID.randomUUID();
        byte[] dummyImage2 = new byte[]{10, 20, 30, 40};

        try {
            // --- [테스트 1] createProfileImage (DTO 활용 그룹화) ---
            System.out.println("[테스트 1] 프로필 이미지 생성");
            ProfileImageCreateRequestDto profileDto = new ProfileImageCreateRequestDto(testUserId, dummyImage2);
            BinaryContentResponseDto profileRes = binaryContentService.createProfileImage(profileDto);
            System.out.println("프로필 이미지 생성 완료. ID: " + profileRes.id());

            // --- [테스트 2] createMessageImage (DTO 활용 그룹화) ---
            System.out.println("[테스트 2] 메시지 첨부 이미지 생성 (2개)");
            MessageImageCreateRequestDto msgDto1 = new MessageImageCreateRequestDto(testAuthorId, testMessageId, new byte[]{1, 2, 3});
            MessageImageCreateRequestDto msgDto2 = new MessageImageCreateRequestDto(testAuthorId, testMessageId, new byte[]{4, 5, 6});

            BinaryContentResponseDto msgRes1 = binaryContentService.createMessageImage(msgDto1);
            BinaryContentResponseDto msgRes2 = binaryContentService.createMessageImage(msgDto2);
            System.out.println("메시지 이미지 1 생성 ID: " + msgRes1.id());
            System.out.println("메시지 이미지 2 생성 ID: " + msgRes2.id());

            // --- [테스트 3] find (ID로 단건 조회) ---
            System.out.println("[테스트 3] 단건 조회 테스트");
            BinaryContentResponseDto foundOne = binaryContentService.find(profileRes.id());
            System.out.println("조회 성공 여부: " + foundOne.id().equals(profileRes.id()));

            // --- [테스트 4] findAllByIdIn (ID 목록으로 대량 조회) ---
            System.out.println("[테스트 4] findAllByIdIn (멀티 조회)");
            // 위에서 만든 3개의 이미지 ID를 리스트로 묶음
            List<UUID> idsToFind = List.of(profileRes.id(), msgRes1.id(), msgRes2.id());

            List<BinaryContentResponseDto> allFound = binaryContentService.findAllByIdIn(idsToFind);
            System.out.println("조회 요청 개수: 3, 실제 결과 개수: " + allFound.size());

            for (BinaryContentResponseDto dto : allFound) {
                System.out.println(" - 조회된 파일 ID: " + dto.id());
            }

            // --- [테스트 5] delete (ID로 삭제) ---
            System.out.println("[테스트 5] 삭제 테스트");
            binaryContentService.delete(profileRes.id());

            try {
                binaryContentService.find(profileRes.id());
                System.out.println("FAIL: 삭제된 데이터가 여전히 조회됩니다.");
            } catch (NoSuchElementException e) {
                System.out.println("SUCCESS: 삭제 확인 완료 (조회 불가)");
            }

        } catch (Exception e) {
            System.err.println("테스트 중 에러 발생!");
            e.printStackTrace();
        }

        System.out.println("=== BinaryContentService 모든 테스트 종료 ===\n");
    }
}



