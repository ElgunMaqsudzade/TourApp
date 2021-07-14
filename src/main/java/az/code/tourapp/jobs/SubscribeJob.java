package az.code.tourapp.jobs;

import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.configs.RabbitMQConfig;
import az.code.tourapp.dtos.TimerInfoDTO;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeJob implements Job {
    private final RabbitTemplate temp;
    private final RabbitMQConfig config;
    private final SubscriptionCache cache;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Long> infoDTO = (TimerInfoDTO<Long>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Long userId = infoDTO.getCallbackData();
        temp.convertAndSend(config.getOffer(), cache.findById(userId));
    }
}
