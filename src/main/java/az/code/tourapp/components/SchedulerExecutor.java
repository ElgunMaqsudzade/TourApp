package az.code.tourapp.components;


import az.code.tourapp.dtos.MessageDTO;
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


    public void runSendMessageJob(MessageDTO messageDTO) {
        TimerInfoDTO<MessageDTO> infoDTO = new TimerInfoDTO<>();
        infoDTO.setTotalFireCount(1);
        infoDTO.setCallbackData(messageDTO);
        scheduler.schedule(SendMessageJob.class, infoDTO);
    }

}
