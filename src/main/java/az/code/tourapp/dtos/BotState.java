package az.code.tourapp.dtos;

import lombok.Getter;

@Getter
public enum BotState {
    START,
    SET_LANGUAGE,
    SET_TRAVELLING_TYPE,
    SET_TRAVELLING_TO,
    SET_TRAVELLING_FROM,
    SET_TRAVELLING_DATE,
    SET_TRAVELLING_QTY,
    SET_TRAVELLING_BUDGET,
    PENDING,
    GET_OFFERS
}
