package io.confluent.pie.csp_demo.aws.app.controller;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import io.confluent.pie.csp_demo.aws.app.cache.SubmissionCache;
import io.confluent.pie.csp_demo.aws.app.cache.UserCache;
import io.confluent.pie.csp_demo.aws.app.controller.responses.*;
import io.confluent.pie.csp_demo.aws.common.models.User;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SocketIOService {

    private final ChatReceiver chatReceiver;
    private final SubmissionCache submissionCache;
    private final ObfuscationService obfuscationService;
    private final UserCache userCache;

    public SocketIOService(@Autowired SocketIOServer socketServer,
                           @Autowired ChatReceiver chatReceiver,
                           @Autowired SubmissionCache submissionCache,
                           @Autowired ObfuscationService obfuscationService,
                           @Autowired UserCache userCache) {
        this.chatReceiver = chatReceiver;
        this.submissionCache = submissionCache;
        this.obfuscationService = obfuscationService;
        this.userCache = userCache;

        socketServer.addListeners(this);
        socketServer.start();
    }

    @OnEvent("get_submission")
    public void onGetSubmission(SocketIOClient client, AckRequest ackRequest, SubmissionRequest submissionRequest) {
        log.info("Client requested submission {}", submissionRequest.getSubmissionId());
        final Map<String, Object> submission = submissionCache.get(submissionRequest.getSubmissionId());
        if (submission == null) {
            ackRequest.sendAckData(new SubmissionResponse("Submission not found"));
        } else if (submissionRequest.getUserId().equals("employee1")) {
            final Map<String, Object> obfuscatedSubmission = obfuscationService.obfuscate(submission);
            ackRequest.sendAckData(new SubmissionResponse(obfuscatedSubmission));
        } else {
            ackRequest.sendAckData(new SubmissionResponse(submission));
        }
    }

    public List<Submission> getSubmissions() {
        return submissionCache.getAll()
                .stream()
                .map(entry -> Submission.newSubmission(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .toList();
    }

    @OnEvent("start_session")
    public void onStartSession(SocketIOClient client, AckRequest ackRequest, StartSession session) {
        log.info("New session for user '{}' session '{}'", session.getUsername(), client.getSessionId());
        client.joinRoom(client.getSessionId().toString());
        ackRequest.sendAckData(new ServerResponse("Connected to server", client.getSessionId().toString()));
    }

    @OnEvent("client_message")
    public void onClientMessage(SocketIOClient client, ClientMessage message) {
        log.info("Client message: {} for user {}", message.getSession_id(), message.getUsername());
        chatReceiver.onMessage(client.getSessionId(), message);
    }

    @OnEvent("login")
    public void onLogin(SocketIOClient client, AckRequest ackRequest, LoginRequest loginRequest) {
        log.info("User logged in: {}", loginRequest.getUsername());
        User user = userCache.get(loginRequest.getUsername());
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            ackRequest.sendAckData(new LoginResponse("client", true, "Login successful", null));
        } else {
            final String username = loginRequest.getUsername();
            if ((StringUtils.equals(username, "employee1") || StringUtils.equals(username, "employee2")) &&
                    StringUtils.equals(loginRequest.getPassword(), "password")) {
                final List<Submission> submissions = submissionCache.getAll()
                        .stream()
                        .map(entry -> Submission.newSubmission(entry.getKey(), entry.getValue()))
                        .filter(Objects::nonNull)
                        .toList();
                
                ackRequest.sendAckData(new LoginResponse("employee", true, "Login successful", Map.of("submissions", submissions)));
            } else {
                ackRequest.sendAckData(new LoginResponse("client", false, "Login failed", null));
            }
        }
    }
}
