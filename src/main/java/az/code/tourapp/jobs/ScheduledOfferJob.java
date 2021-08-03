package az.code.tourapp.jobs;


import az.code.tourapp.cache.interfaces.OfferCapCache;
import az.code.tourapp.models.Offer;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.OfferService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class ScheduledOfferJob implements Job {
    private final OfferCapCache cache;
    private final OfferService offerService;
    private final MessageService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Set<String> uuidList = cache.getUUIDList();
        uuidList.stream().parallel().forEach(this::sendDBOffers);
    }

    private void sendDBOffers(String uuid) {
        while (!cache.findById(uuid).isLocked()) {
            Optional<Offer> offer = offerService.pop(uuid);
            offer.ifPresent(service::sendOffer);
        }
    }
}