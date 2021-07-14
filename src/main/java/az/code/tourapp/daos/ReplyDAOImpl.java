package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.ReplyDAO;
import az.code.tourapp.exceptions.NotFound;
import az.code.tourapp.models.*;
import az.code.tourapp.repos.ReplyRepo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReplyDAOImpl implements ReplyDAO {
    ReplyRepo replyRepo;

    public ReplyDAOImpl(ReplyRepo replyRepo) {
        this.replyRepo = replyRepo;
    }

    @Override
    public Reply getReply(String state, String locale) {
        Specification<Reply> replySpecification = Specification.where((rt, q, cb) -> cb
                .and(cb.equal(rt.get(Reply_.STATE).get(BotState_.STATE), state),
                        cb.equal(rt.get(Reply_.LOCALE).get(Locale_.LANG), locale)));

        Optional<Reply> reply = replyRepo.findAll(replySpecification).stream().findFirst();
        if (reply.isEmpty()) throw new NotFound("Corresponding reply not found");

        return reply.get();
    }


}
