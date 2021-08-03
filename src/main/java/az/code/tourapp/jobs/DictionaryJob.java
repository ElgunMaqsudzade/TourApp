package az.code.tourapp.jobs;

import az.code.tourapp.configs.BotConfig;
import az.code.tourapp.daos.interfaces.DictionaryDAO;
import az.code.tourapp.dtos.DictionaryDTO;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Error;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;


public class DictionaryJob implements Job {
    DictionaryDAO dic;
    HashOperations<String, String, DictionaryDTO> hashOperations;
    private final String HASH_KEY;

    public DictionaryJob(DictionaryDAO dictionaryDAO, RedisTemplate<String, Object> template, BotConfig config) {
        this.hashOperations = template.opsForHash();
        this.HASH_KEY = config.getRedis().getDictionary();
        this.dic = dictionaryDAO;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        hashOperations.delete(HASH_KEY, HASH_KEY);
        hashOperations.put(HASH_KEY, HASH_KEY, DictionaryDTO.builder()
                .botStates(dic.getData(BotState.class))
                .replyList(dic.getData(Reply.class))
                .locales(dic.getData(Locale.class))
                .errors(dic.getData(Error.class))
                .build());
    }
}
