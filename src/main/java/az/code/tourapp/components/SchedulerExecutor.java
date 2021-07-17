package az.code.tourapp.components;

import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.jobs.*;
import az.code.tourapp.models.Offer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SchedulerExecutor {
    Scheduler scheduler;

    public SchedulerExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void runHandleOfferJob(Offer offer) {
        scheduler.schedule(HandleOfferJob.class, TimerInfoDTO.builder().fireCount(1).data(offer).build());
    }

    public void runSubscribeJob(Long userid) {
        scheduler.schedule(SubscribeJob.class, TimerInfoDTO.builder().fireCount(1).data(userid).build());
    }

    public void runDBOfferJob(String uuid) {
        scheduler.schedule(DBOfferJob.class, TimerInfoDTO.builder().fireCount(1).data(uuid).build());
    }

    @Bean
    public void runDictionaryJob() {
        scheduler.schedule(DictionaryJob.class, TimerInfoDTO.builder().intervalMS(60 * 60 * 1000 * 24).forever(true).build());
    }

}
