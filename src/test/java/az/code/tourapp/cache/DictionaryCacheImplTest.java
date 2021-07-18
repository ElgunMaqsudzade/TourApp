package az.code.tourapp.cache;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.cache.interfaces.SubscriptionCache;
import az.code.tourapp.configs.RedisConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.cdi.RedisRepositoryExtension;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
class DictionaryCacheImplTest {

    @Test
    void getReply() {
        //given
        String state = "ERROR";
        String lang = "ENGLISH";
        //when
//        System.out.println(cache.getReply(state,lang));;
        //expected
    }

    @Test
    void getState() {
    }

    @Test
    void getLocale() {
    }
}