package az.code.tourapp.utils;

import az.code.tourapp.dtos.TimerInfoDTO;
import lombok.NoArgsConstructor;
import org.quartz.*;

import java.util.Date;

@NoArgsConstructor
public class TimerUtil {

    public static <T> JobDetail buildJobDetail(Class<? extends Job> jobClass, TimerInfoDTO<T> infoDTO) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobClass.getSimpleName(), infoDTO);

        return JobBuilder
                .newJob(jobClass)
                .ofType(jobClass)
                .withIdentity(jobClass.getSimpleName())
                .setJobData(jobDataMap)
                .build();
    }

    public static <T> Trigger buildTrigger(Class<? extends Job> jobClass, TimerInfoDTO<T> infoDTO) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMilliseconds(infoDTO.getRepeatIntervalMS());

        if(infoDTO.isRunForever()){
            builder = builder.repeatForever();
        }else {
            builder = builder.withRepeatCount(infoDTO.getTotalFireCount() - 1);
        }

        return TriggerBuilder.newTrigger()
                .withIdentity(jobClass.getSimpleName())
                .withSchedule(builder)
                .startAt(new Date(System.currentTimeMillis() + infoDTO.getInitialOffsetMS()))
                .build();
    }
}
