package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    public boolean isNicknameUnique(String nickname) {
        return data.values().stream().noneMatch(user -> nickname.equals(user.getNickname()));
    }

    public User createAndSaveUser(String username, String email, String password, String nickname){
        if(username == null || email == null || password == null || nickname == null || !isNicknameUnique(nickname)){
            return null; // 입력값 오류와 닉네임 중복에 대한 알림은 어떻게 따로 알릴지,
            // 유저 서비스 내에서 객체 식별은 id로 하되 nickname을 유니크하게 만드는게 JavaApp에서 테스트 하기 편할듯함
            // 콘솔은 추천 안한다고 하셔서 테스트 편하게 하기 위한 메소드 추가로 만들어야 할 지 고민중 아니면 id를 따로 app에서 저장해둬야 됨.
        }
        return save(new User(username, email, password, nickname));
    }

    @Override
    public User save(User user) {
        if (user == null) {
            return null;
        }

        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String name, String email, String password, String nickname) {
        User user = findById(id);

        if (user != null) {
            user.update(name, email, password, nickname);
        }

        return user;
    }

    public User delete(UUID id) {
        return data.remove(id);
    }
}
