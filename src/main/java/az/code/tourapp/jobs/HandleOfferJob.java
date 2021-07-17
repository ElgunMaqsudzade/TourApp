package az.code.tourapp.jobs;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.daos.interfaces.OfferDAO;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.Offer;
import az.code.tourapp.services.interfaces.MessageService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HandleOfferJob implements Job {
    private final OfferDAO offerDAO;
    private final OfferCache cache;
    private final MessageService service;


    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Offer> infoDTO = (TimerInfoDTO<Offer>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Offer offer = infoDTO.getData();
        String uuid = offer.getUUID();
        cache.create(uuid);
        OfferCacheDTO current = cache.findById(uuid);
        if (current.isLocked()) {
            offerDAO.save(offer);
        } else {
            if (offerDAO.exists(uuid)) {
                offerDAO.save(offer);
            } else {
                service.sendOffer(offer);
            }
        }
    }
}
