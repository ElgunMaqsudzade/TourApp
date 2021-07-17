package az.code.tourapp.services;

import az.code.tourapp.cache.interfaces.DictionaryCache;
import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.KeyboardContext;
import az.code.tourapp.components.WebhookBotComponent;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.KeyboardDTO;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.Offer;
import az.code.tourapp.models.Reply;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.models.enums.InputType;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.SubCacheService;
import az.code.tourapp.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SubCacheService cache;
    private final DictionaryCache dCache;
    private final OfferCache oCache;
    private final KeyboardContext context;
    private final WebhookBotComponent sender;
    private final AppUserDAO appUserDAO;
    private final ImageUtil imageUtil;

    @Override
    public void sendNextButton(Long userId, Long chatId) {
        cache.setState(userId, BasicState.NEXT.toString());
        Reply reply = dCache.getReply(userId);

        SendMessage replyToUser = new SendMessage();
        replyToUser.setChatId(chatId.toString());
        replyToUser.setText(reply.getMessage());
        ReplyKeyboard replyKeyboard = context.generateKeyboard(Collections
                .singletonList(KeyboardDTO
                        .builder().text("Next")
                        .callbackData("next")
                        .inputType(InputType.BUTTON)
                        .build()));

        replyToUser.setReplyMarkup(replyKeyboard);

        sender.sendMessage(replyToUser);
    }

    @Override
    public void sendOffer(Offer offer) {
        AppUser user = appUserDAO.findByUUID(offer.getUUID());
        Long userId = user.getUserId();
        Long chatId = user.getChatId();
        String text = offer.getMessage();
        String uuid = offer.getUUID();

        sender.sendPhoto(SendPhoto
                .builder()
                .chatId(String.valueOf(chatId))
                .caption(text)
                .parseMode(ParseMode.HTML)
                .photo(imageUtil.getInputFile(offer.getFileAsBytes()))
                .build());

        OfferCacheDTO next = oCache.increase(uuid);
        if (next.isLocked()) {
            sendNextButton(userId, chatId);
        }
    }
}
