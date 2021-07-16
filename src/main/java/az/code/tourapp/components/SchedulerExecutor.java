package az.code.tourapp.components;


import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.jobs.DictionaryJob;
import az.code.tourapp.jobs.HandleOfferJob;
import az.code.tourapp.jobs.SendOfferJob;
import az.code.tourapp.jobs.SubscribeJob;
import az.code.tourapp.models.Offer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SchedulerExecutor {
    Scheduler scheduler;

    public SchedulerExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void runSendOfferJob(Offer offer) {
        scheduler.schedule(SendOfferJob.class, TimerInfoDTO.builder().fireCount(1).data(offer).build());
    }
    public void runHandleOfferJob(Offer offer) {
        scheduler.schedule(HandleOfferJob.class, TimerInfoDTO.builder().fireCount(1).data(offer).build());
    }

    public void runSubscribeJob(Long userid) {
        scheduler.schedule(SubscribeJob.class, TimerInfoDTO.builder().fireCount(1).data(userid).build());
    }

    @Bean
    public void runDictionaryJob() {
        scheduler.schedule(DictionaryJob.class, TimerInfoDTO.builder().intervalMS(60 * 60 * 1000 * 24).forever(true).build());
    }

}
