package az.code.tourapp.jobs;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.Offer;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.OfferService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DBOffersJob implements Job {
    private final OfferService offerService;
    private final OfferCache cache;
    private final MessageService service;

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<String> infoDTO = (TimerInfoDTO<String>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        String uuid = infoDTO.getData();
        while (!cache.findById(uuid).isLocked()){
            Optional<Offer> offer = offerService.pop(uuid);
            offer.ifPresent(service::sendOffer);
        }
    }
}
