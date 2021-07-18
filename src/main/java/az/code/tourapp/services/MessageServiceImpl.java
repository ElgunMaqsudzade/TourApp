package az.code.tourapp.services;

import az.code.tourapp.cache.interfaces.OfferCache;
import az.code.tourapp.components.ReplyProcessor;
import az.code.tourapp.components.WebhookBotComponent;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.OfferCacheDTO;
import az.code.tourapp.models.*;
import az.code.tourapp.models.enums.BasicState;
import az.code.tourapp.services.interfaces.MessageService;
import az.code.tourapp.services.interfaces.SubCacheService;
import az.code.tourapp.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

@Component
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SubCacheService cache;
    private final OfferCache oCache;
    private final WebhookBotComponent sender;
    private final AppUserDAO appUserDAO;
    private final ImageUtil imageUtil;
    private final ReplyProcessor replyProcessor;

    @Override
    public void sendNextAction(Long userId, Long chatId) {
        sender.sendAction(replyProcessor.sendNextAction(userId, chatId));
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
            cache.setState(userId, BasicState.NEXT.toString());
            sendNextAction(userId, chatId);
            cache.setState(userId, BasicState.OFFER.toString());
        }
    }
}
