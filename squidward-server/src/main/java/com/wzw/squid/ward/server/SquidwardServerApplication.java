package com.wzw.squid.ward.server;

import com.wzw.squid.ward.server.init.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SquidwardServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SquidwardServerApplication.class, args);
        new ChatServer(23334);
    }

}
