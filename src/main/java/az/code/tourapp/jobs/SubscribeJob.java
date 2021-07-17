package az.code.tourapp.jobs;

import az.code.tourapp.configs.RabbitMQConfig;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.services.interfaces.SubCacheService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@RequiredArgsConstructor
public class SubscribeJob implements Job {
    private final RabbitTemplate temp;
    private final RabbitMQConfig config;
    private final SubCacheService cache;


    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Long> infoDTO = (TimerInfoDTO<Long>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Long userId = infoDTO.getData();
        temp.convertAndSend(config.getSubscription(), cache.findById(userId).getSubscription());
        cache.setMainState(userId, BasicState.IDLE);
        cache.setState(userId, BasicState.IDLE.toString());
        cache.deleteSubscription(userId);
    }
}
