package baedalmate.baedalmate.recruit.schedule;

import baedalmate.baedalmate.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@EnableScheduling
@RequiredArgsConstructor
public class DynamicScheduler {

    private final RecruitService recruitService;

    @Scheduled(cron = "0/30 * * * * *")
    public void run() {
        System.out.println(LocalDateTime.now());
        recruitService.closeBySchedule();
    }
}
