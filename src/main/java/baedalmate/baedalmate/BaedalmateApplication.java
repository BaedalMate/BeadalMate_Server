package baedalmate.baedalmate;

import baedalmate.baedalmate.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class BaedalmateApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BaedalmateApplication.class, args);
    }

}
