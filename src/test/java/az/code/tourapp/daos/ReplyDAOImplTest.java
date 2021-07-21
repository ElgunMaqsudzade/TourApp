package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.*;
import az.code.tourapp.repos.BotStateRepo;
import az.code.tourapp.repos.LocaleRepo;
import az.code.tourapp.repos.ReplyRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class ReplyDAOImplTest {

    @Autowired
    private BotStateRepo botStateRepo;
    @Autowired
    private LocaleRepo localeRepo;
    @Autowired
    private ReplyRepo replyRepo;
    @Autowired
    private ReplyDAO replyDAO;
    @Test
    void getReply() {
        //given
        BotState state = BotState.builder().state("START").build();
        Locale locale = Locale.builder().lang("ENGLISH").build();
        Reply reply = Reply.builder().state(state).locale(locale).build();
        botStateRepo.save(state);
        localeRepo.save(locale);
        replyRepo.save(reply);
        //when
        Reply daoReply = replyDAO.getReply(state.getState(),locale.getLang());
        //expected
        assertThat(daoReply.getLocale().getLang()).isEqualTo(locale.getLang());
        assertThat(daoReply.getState().getState()).isEqualTo(state.getState());

    }
    @Test
    void willThrowWhenReplyNotFound() {
        //given
        BotState state = BotState.builder().state("START").build();
        Locale locale = Locale.builder().lang("ENGLISH").build();

        Specification<Reply> specification = Mockito.mock(Specification.class);
        List<Reply> repoAll = replyRepo.findAll(specification);

        //when
        when(spy(repoAll)).then(new Answer<>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return Optional.empty();
            }
        });
        //expected
        assertThatThrownBy(()-> replyDAO.getReply(state.getState(),locale.getLang()))
                .isInstanceOf(NotFound.class)
                .hasMessageContaining("Corresponding reply not found");

    }
}