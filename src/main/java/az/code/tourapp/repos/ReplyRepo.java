package az.code.tourapp.repos;

import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReplyRepo extends JpaRepository<Reply, Long> , JpaSpecificationExecutor<Reply> {
}
