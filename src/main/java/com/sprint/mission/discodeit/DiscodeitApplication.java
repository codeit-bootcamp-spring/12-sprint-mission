package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {

    static Optional<BinaryContentCreateRequest> loadProfile(
            String imagePath,
            String fileName,
            String contentType
    ) {
        try {
            byte[] bytes = new ClassPathResource(imagePath).getInputStream().readAllBytes();
            return Optional.of(new BinaryContentCreateRequest(fileName, contentType, bytes));
        } catch (IOException e) {
            System.out.println("프로필 이미지 로드 실패: " + imagePath);
            return Optional.empty();
        }
    }

    static void createUserIfAbsent(
            UserService userService,
            String username,
            String email,
            String password,
            String imagePath,
            String fileName,
            String contentType
    ) {
        try {
            UserCreateRequest request = new UserCreateRequest(username, email, password);
            Optional<BinaryContentCreateRequest> profile =
                    loadProfile(imagePath, fileName, contentType);

            userService.create(request, profile);
            System.out.println(username + " 생성 완료");
        } catch (IllegalArgumentException e) {
            System.out.println(username + " 이미 존재해서 생성 생략");
        }
    }

    static void setupUser(UserService userService) {
        createUserIfAbsent(
                userService,
                "jessie",
                "jessie@codeit.com",
                "jessie1234",
                "jessie.jpeg",
                "jessie.jpeg",
                "image/jpeg"
        );

        createUserIfAbsent(
                userService,
                "rex",
                "rex@codeit.com",
                "rex1234",
                "rex.png",
                "rex.png",
                "image/png"
        );

        createUserIfAbsent(
                userService,
                "buzz",
                "buzz@codeit.com",
                "buzz1234",
                "buzz.jpeg",
                "buzz.jpeg",
                "image/jpeg"
        );

        createUserIfAbsent(
                userService,
                "woody",
                "woody@codeit.com",
                "woody1234",
                "woody.jpeg",
                "woody.jpeg",
                "image/jpeg"
        );
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);

        setupUser(userService);
    }
}