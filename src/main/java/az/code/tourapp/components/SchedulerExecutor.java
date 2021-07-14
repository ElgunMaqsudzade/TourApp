package az.code.tourapp.components;


import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.jobs.SendMessageJob;
import az.code.tourapp.jobs.SubscribeJob;
import az.code.tourapp.models.Offer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SchedulerExecutor {
    Scheduler scheduler;

    public SchedulerExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void runSendMessageJob(Offer offer) {
        TimerInfoDTO<Offer> infoDTO = new TimerInfoDTO<>();
        infoDTO.setTotalFireCount(1);
        infoDTO.setCallbackData(offer);
        scheduler.schedule(SendMessageJob.class, infoDTO);
    }

    public void runSubscribeJob(Long userid) {
        scheduler.schedule(SubscribeJob.class, TimerInfoDTO.builder().callbackData(userid).build());
    }

}
