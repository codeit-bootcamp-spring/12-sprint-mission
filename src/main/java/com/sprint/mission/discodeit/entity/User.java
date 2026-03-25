package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class User {
    // 필드 (데이터 영역)
    private UUID id; // 객체를 식별하기 위한 id로 UUID 타입으로 선언
    private String username;
    private String email;
    private String password;
    private String nickname;
    // 각각 객체의 생성, 수정 시간을 유닉스 타임스탬프로 나타내기 위한 필드로 Long 타입으로 선언
    private Long createdAt;
    private Long updatedAt;

    // 생성자 (객체 생성)
    public User(String username, String email, String password, String nickname) {
        // 고유 id 자동 생성
        this.id = UUID.randomUUID(); // 조건 ) id는 생성자에서 초기화

        // 외부에서 받은 값 저장
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;


        // 현재 시간 저장
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // getter(조회), 각 필드를 반환하는 Getter 함수를 정의
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    //  update (수정 기능), 필드를 수정하는 update 함수를 정의
    public void update(String username, String email, String password, String nickname){
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;

        // 수정시간 갱신
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime created = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault());
        LocalDateTime updated = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault());

        return "[User]" +
                "\n- ID: " + id +
                "\n- Username: " + username +
                "\n- Email: " + email +
                "\n- Nickname: " + nickname +
                "\n- CreatedAt: " + created.format(formatter) +
                "\n- UpdatedAt: " + updated.format(formatter) +
                "\n";
    }

}