package az.code.tourapp.jobs;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.SchedulerExecutor;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.Offer;
import az.code.tourapp.repos.OfferRepo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HandleOfferJob implements Job {
    OfferRepo offerRepo;
    OfferCache cache;
    SchedulerExecutor sch;

    public HandleOfferJob(OfferRepo offerRepo, OfferCache cache, SchedulerExecutor sch) {
        this.offerRepo = offerRepo;
        this.cache = cache;
        this.sch = sch;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Offer> infoDTO = (TimerInfoDTO<Offer>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Offer offer = infoDTO.getData();
        String uuid = offer.getUUID();
        cache.create(uuid);
        OfferCacheDTO current = cache.findById(uuid);
        if (current.isLocked()) {
            offerRepo.save(offer);
        } else {
            sch.runSendOfferJob(offer);
            OfferCacheDTO next = cache.increase(uuid);
            if (next.getOfferCount() % 5 == 0) {
                next.setLocked(true);
                cache.save(uuid, next);
            }
        }
    }
}
