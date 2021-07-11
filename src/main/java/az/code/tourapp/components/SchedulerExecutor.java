package az.code.tourapp.components;


import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.jobs.SendMessageJob;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class SchedulerExecutor {
    Scheduler scheduler;

    public SchedulerExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void runSendMessageJob(SendMessage sendMessage) {
        TimerInfoDTO<SendMessage> infoDTO = new TimerInfoDTO<>();
        infoDTO.setTotalFireCount(1);
        infoDTO.setCallbackData(sendMessage);
        scheduler.schedule(SendMessageJob.class, infoDTO);
    }

}
