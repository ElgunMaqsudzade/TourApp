package az.code.tourapp.services.interfaces;

import az.code.tourapp.models.Offer;


public interface MessageService {

    void sendNextButton(Long userId, Long chatId);

    void sendOffer(Offer offer);
}
