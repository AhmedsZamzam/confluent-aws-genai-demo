package io.confluent.pie.csp_demo.aws.app.controller;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@Component
@Slf4j
public class SocketIOConfig {

    @Value("${socket.host}")
    private String sockedHost;
    @Value("${socket.port}")
    private int socketPort;
    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        if (server == null) {
            initServer();
        }

        return server;
    }

    @PreDestroy
    public void stopSocketIOServer() {
        log.info("Stopping SocketIO server");

        if (this.server != null)
            this.server.stop();
    }

    @Synchronized
    private void initServer() {
        if (server == null) {
            log.info("Starting SocketIO server on {}:{}", sockedHost, socketPort);

            Configuration config = new Configuration();
            config.setHostname(sockedHost);
            config.setPort(socketPort);
            config.setOrigin("*");

            server = new SocketIOServer(config);
        }
    }

}