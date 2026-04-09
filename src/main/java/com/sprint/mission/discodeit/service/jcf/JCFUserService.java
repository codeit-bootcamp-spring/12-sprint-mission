package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
//[O] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
//[O] 클래스 네이밍 규칙: JCF[인터페이스 이름]
//[O] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
//[O] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
public class JCFUserService implements UserService {

    private final List<User> data;

    private JCFUserService() {
        data = new ArrayList<>();
    }

    private static JCFUserService instance = new JCFUserService();

    public static JCFUserService getInstance() {
        return instance;
    }


    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
//        for(User user : data){
//            if(user.getId().equals(id)) return user;
//        }
//        return null;
        return data.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public User update(User user) {
        User findUser = findById(user.getId());
        if (findUser != null) {
            findUser.update(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getNickname()
            );
            return findUser;
        }
        return null;
    }

    @Override
    public User delete(UUID id) {
        User findUser = findById(id);
        if (findUser != null) {
            data.remove(findUser);
            return findUser;
        }
        return null;
    }
}