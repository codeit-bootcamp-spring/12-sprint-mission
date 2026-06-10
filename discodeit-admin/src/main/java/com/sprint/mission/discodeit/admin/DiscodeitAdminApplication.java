package com.sprint.mission.discodeit.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class DiscodeitAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitAdminApplication.class, args);
        System.out.println("http://localhost:9090/");
    }
}