package az.code.tourapp.utils;

import az.code.tourapp.models.BotState;
import az.code.tourapp.models.enums.BasicState;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class EnumUtilTest {

    @Test
    void valueOf() {
        //given
        String value = "start";
        String secondValue = "sdada";
        Class aClass = BasicState.class;
        //when
        Optional<BasicState> valueOf = EnumUtil.valueOf(value, aClass);
        Optional<BasicState> secondValueOf = EnumUtil.valueOf(secondValue, aClass);
        //expected
        assertThat(BasicState.START).isEqualTo(valueOf.get());
        assertThat(secondValueOf).isEmpty();
    }

    @Test
    void commandToEnum() {
        //given
        String value = "/start";
        String secondValue = "/sdada";
        Class<BasicState> aClass = BasicState.class;
        //when
        Optional<BasicState> valueOf = EnumUtil.commandToEnum(value, aClass);
        Optional<BasicState> secondValueOf = EnumUtil.commandToEnum(secondValue, aClass);
        //expected
        assertThat(BasicState.START).isEqualTo(valueOf.get());
        assertThat(secondValueOf).isEmpty();
    }
}