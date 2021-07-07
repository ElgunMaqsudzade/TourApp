package az.code.tourapp.daos.interfaces;

import az.code.tourapp.models.BotState;
import az.code.tourapp.models.Locale;
import az.code.tourapp.models.Reply;

public interface ReplyDAO {

    Reply getReply(String state, String locale);
}
