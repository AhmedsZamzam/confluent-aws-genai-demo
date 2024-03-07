package io.confluent.pie.csp_demo.aws.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages =
        {"io.confluent.pie.csp_demo.aws.app",
                "io.confluent.pie.csp_demo.aws.common.configs",
                "io.confluent.pie.csp_demo.aws.common.utils"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
