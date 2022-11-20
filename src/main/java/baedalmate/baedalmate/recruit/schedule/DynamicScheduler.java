package baedalmate.baedalmate.recruit.schedule;

import baedalmate.baedalmate.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
@RequiredArgsConstructor
public class DynamicScheduler {

    private final RecruitService recruitService;

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        recruitService.closeBySchedule();
    }
}
