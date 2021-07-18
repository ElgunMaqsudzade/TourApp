package az.code.tourapp.components;

import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.jobs.*;
import az.code.tourapp.models.Offer;
import az.code.tourapp.utils.TimerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerExecutor {
    private final Scheduler scheduler;
    private final TimerUtil timerUtil;



    public void runHandleOfferJob(Offer offer) {
        scheduler.schedule(HandleOfferJob.class, TimerInfoDTO.builder().fireCount(1).data(offer).build());
    }

    public void runSubscribeJob(Long userid) {
        scheduler.schedule(SubscribeJob.class, TimerInfoDTO.builder().fireCount(1).data(userid).build());
    }

    public void runOfferReplyJob(Long userid) {
        scheduler.schedule(OfferReplyJob.class, TimerInfoDTO.builder().fireCount(1).data(userid).build());
    }

    public void runDBOffersJobJob(String uuid) {
        scheduler.schedule(DBOffersJob.class, TimerInfoDTO.builder().fireCount(1).data(uuid).build());
    }

    @Bean
    public void runScheduledOfferJob() {
        scheduler.schedule(ScheduledOfferJob.class, TimerInfoDTO.builder().cron(timerUtil.toCron()).build());
    }

//    @Scheduled(cron = "0 0 0 * * ?")
    @Bean
    public void runDictionaryJob() {
        scheduler.schedule(DictionaryJob.class, TimerInfoDTO.builder().fireCount(1).build());
    }

}
