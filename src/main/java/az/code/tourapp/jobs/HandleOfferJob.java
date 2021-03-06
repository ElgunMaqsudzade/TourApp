package az.code.tourapp.jobs;

import az.code.tourapp.cache.interfaces.OfferCapCache;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.Offer;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.OfferService;
import az.code.tourapp.utils.TimerUtil;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@RequiredArgsConstructor
public class HandleOfferJob implements Job {
    private final OfferService offerService;
    private final OfferCapCache cache;
    private final MessageService service;
    private final TimerUtil timer;


    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Offer> infoDTO = (TimerInfoDTO<Offer>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Offer offer = infoDTO.getData();
        String uuid = offer.getUUID();
        cache.create(uuid);
        OfferCacheDTO current = cache.findById(uuid);
        if (current.isLocked() || !timer.isAppropriate()) {
            offerService.save(offer);
        } else{
            if (offerService.existsByUUID(uuid)) {
                offerService.save(offer);
            } else {
                service.sendOffer(offer);
            }
        }
    }
}
