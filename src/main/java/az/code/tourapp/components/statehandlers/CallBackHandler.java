package az.code.tourapp.components.statehandlers;


import az.code.tourapp.components.ProcessReplyComponent;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;


@Component
public class CallBackHandler {
    ProcessReplyComponent replyUtil;

    public CallBackHandler(ProcessReplyComponent replyUtil) {
        this.replyUtil = replyUtil;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();
        String usersAnswer = buttonQuery.getData();



        return replyUtil.processReply(userId,chatId,usersAnswer);
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
