package com.eirs.duplicate;

import com.eirs.duplicate.monitors.HashMapMonitorTask;
import com.eirs.duplicate.orchestrator.RecordDuplicateProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableScheduling
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        ApplicationContext context = SpringApplication.run(Application.class, args);
        new Thread(context.getBean(HashMapMonitorTask.class)).start();
        LocalDate date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        context.getBean(RecordDuplicateProcessor.class).process(date);
        System.exit(0);
    }

}
