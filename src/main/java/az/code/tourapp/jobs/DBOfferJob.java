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
public class DBOfferJob implements Job {
    private final OfferDAO offerDAO;
    private final OfferCache cache;
    private final MessageService service;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<String> infoDTO = (TimerInfoDTO<String>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        String uuid = infoDTO.getData();
        while (!cache.findById(uuid).isLocked()){
            Optional<Offer> offer = offerDAO.pop(uuid);
            offer.ifPresent(service::sendOffer);
        }
    }
}
