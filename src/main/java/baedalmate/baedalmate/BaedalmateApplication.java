package baedalmate.baedalmate;

import baedalmate.baedalmate.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class BaedalmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaedalmateApplication.class, args);
	}

}
