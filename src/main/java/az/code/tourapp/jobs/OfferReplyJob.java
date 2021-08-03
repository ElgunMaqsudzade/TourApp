package az.code.tourapp.jobs;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.configs.RabbitMQConfig;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.services.interfaces.SubCacheService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;


@RequiredArgsConstructor
public class OfferReplyJob implements Job {
    private final AppUserDAO appUserDAO;
    private final OfferCache offerCache;
    private final RabbitTemplate temp;
    private final SubCacheService cache;


    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Long> infoDTO = (TimerInfoDTO<Long>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Long userId = infoDTO.getData();
        AppUser appUser = appUserDAO.findById(userId);
        Map<String, String> offerReply = cache.findById(userId).getSubscription();
        if (offerReply.containsKey("messageId")) {
            Integer messageId = Integer.valueOf(offerReply.get("messageId"));
            offerReply.put("offerId", String.valueOf(offerCache.findById(appUser.getUuid(), messageId)));
        }
        offerReply.put("userId", String.valueOf(userId));
        offerReply.put("chatId", String.valueOf(appUser.getChatId()));
        offerReply.put("uuid", String.valueOf(appUser.getUuid()));
        temp.convertAndSend(RabbitMQConfig.offerReply, offerReply);
        cache.setMainState(userId, BasicState.IDLE);
        cache.setState(userId, BasicState.IDLE.toString());
        cache.deleteSubscription(userId);
    }
}