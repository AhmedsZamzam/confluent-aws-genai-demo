package io.confluent.pie.csp_demo.aws.app.controller;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import io.confluent.pie.csp_demo.aws.app.controller.responses.ClientMessage;
import io.confluent.pie.csp_demo.aws.common.models.ChatInput;
import io.confluent.pie.csp_demo.aws.common.models.ChatOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.*;

@Controller
@Slf4j
public class ChatReceiver {

    private final SocketIOServer socketIOServer;
    private final KafkaTemplate<String, ChatInput> kafkaTemplate;

    public ChatReceiver(@Autowired SocketIOServer socketIOServer,
                        @Autowired KafkaTemplate<String, ChatInput> kafkaTemplate) {
        this.socketIOServer = socketIOServer;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Called when a client sends a message to the server
     *
     * @param clientId the client id
     * @param message  the message
     */
    public void onMessage(UUID clientId, ClientMessage message) {
        final String username = (StringUtils.isEmpty(message.getUsername()))
                ? message.getSession_id()
                : message.getUsername();

        final ChatInput chatInput = ChatInput.builder()
                .session_id(clientId.toString())
                .user_id(username)
                .input(message.getMessage())
                .datetime(Date.from(Instant.now()).toString())
                .build();

        kafkaTemplate.send("chat_input",
                message.getSession_id(),
                chatInput);
        kafkaTemplate.flush();
    }

    /**
     * Called when a message is received from the chat_output topic
     *
     * @param chatOutput the chat output
     */
    @KafkaListener(topics = "chat_output")
    public void onChatOutput(ChatOutput chatOutput) {
        log.info("Received chat output: {}", chatOutput);

        final SocketIOClient client = socketIOServer.getClient(UUID.fromString(chatOutput.getSession_id()));
        if (client != null) {
            client.sendEvent("chat_response", chatOutput);
        }
    }
}
