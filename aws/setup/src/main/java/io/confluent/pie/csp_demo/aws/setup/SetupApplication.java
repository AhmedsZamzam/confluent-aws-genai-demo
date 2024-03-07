package io.confluent.pie.csp_demo.aws.setup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication(scanBasePackages = "io.confluent.pie.csp_demo.aws")
@EnableConfigurationProperties
@Slf4j
@CommandScan
public class SetupApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SetupApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Hello!");
    }
}
