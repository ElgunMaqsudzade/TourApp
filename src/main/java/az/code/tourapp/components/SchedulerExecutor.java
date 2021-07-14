package az.code.tourapp.components;


import az.code.tourapp.dtos.OfferDTO;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.jobs.SendMessageJob;
import org.springframework.stereotype.Component;

@Component
public class SchedulerExecutor {
    Scheduler scheduler;

    public SchedulerExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void runSendMessageJob(OfferDTO offerDTO) {
        TimerInfoDTO<OfferDTO> infoDTO = new TimerInfoDTO<>();
        infoDTO.setTotalFireCount(1);
        infoDTO.setCallbackData(offerDTO);
        scheduler.schedule(SendMessageJob.class, infoDTO);
    }

}
