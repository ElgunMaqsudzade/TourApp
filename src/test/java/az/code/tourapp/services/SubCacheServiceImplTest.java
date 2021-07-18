package az.code.tourapp.services;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.dtos.DictionaryDTO;
import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;
import az.code.tourapp.services.interfaces.SubCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataRedisTest
class SubCacheServiceImplTest {

    private final Long userId = 123L;
    private final Long chatId = 123L;

    private final String HASH_KEY = "DICTIONARY";



    @Test
    void generateData() {
        List<BotState> botStates = Collections.singletonList(BotState.builder().state("ERROR").build());
        List<Locale> locales = Collections.singletonList(Locale.builder().lang("ENGLISH").build());
        List<Reply> replies = Collections.singletonList(Reply.builder().state(botStates.get(0)).locale(locales.get(0)).message("ads").build());
        DictionaryDTO dictionaryDTO = DictionaryDTO.builder().botStates(botStates).locales(locales).replyList(replies).build();

    }
    @Test
    void saveData() {
    }

    @Test
    void getBotState() {
    }

    @Test
    void getLocale() {
    }

    @Test
    void getMainState() {
    }

    @Test
    void existsById() {
    }

    @Test
    void existsSubById() {
    }

    @Test
    void findById() {
    }
}