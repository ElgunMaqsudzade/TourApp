package az.code.tourapp.jobs;

import az.code.tourapp.components.SendMessageComponent;
import az.code.tourapp.components.TelegramWHBot;
import az.code.tourapp.dtos.TimerInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Slf4j
public class SendMessageJob implements Job {
    SendMessageComponent bot;

    public SendMessageJob(SendMessageComponent sendMessage) {
        this.bot = sendMessage;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<SendMessage> infoDTO = (TimerInfoDTO<SendMessage>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        SendMessage message = infoDTO.getCallbackData();
        bot.sendMessage(message);
    }
}
