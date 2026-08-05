package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.service.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {
    private final InitService initService;

    @Override
    public void run(ApplicationArguments args) {
        initService.initAdmin();
    }
}
