package io.confluent.pie.csp_demo.aws.app.controller;

import io.confluent.pie.csp_demo.aws.app.controller.responses.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApplicationController {

    private final SocketIOService socketIOService;

    public ApplicationController(@Autowired SocketIOService socketIOService) {
        this.socketIOService = socketIOService;
    }

    @GetMapping("/submissions")
    public List<Submission> getSubmission() {
        return socketIOService.getSubmissions();
    }
}
