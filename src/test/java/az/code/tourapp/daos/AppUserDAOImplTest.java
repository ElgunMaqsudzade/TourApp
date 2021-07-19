package az.code.tourapp.daos;

import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.Locale;
import az.code.tourapp.repos.AppUserRepo;
import az.code.tourapp.repos.BotStateRepo;
import az.code.tourapp.repos.LocaleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AppUserDAOImplTest {
    private final Long userId = 123L;
    private final Long chatId = 123L;
    private final String uuid = UUID.randomUUID().toString();

    @Mock
    private AppUserRepo appUserRepo;
    @Mock
    private BotStateRepo botStateRepo;
    @Mock
    private LocaleRepo localeRepo;
    private AppUserDAO appUserDAO;

    @BeforeEach
    void setUp() {
        appUserDAO = new AppUserDAOImpl(botStateRepo, appUserRepo, localeRepo);
    }

    @Test
    void findById() {
        //when
        AppUser appUser = AppUser.builder().userId(userId).chatId(chatId).uuid(uuid).build();
        //then
        lenient().when(appUserRepo.findById(userId)).thenReturn(Optional.of(appUser));
    }

    @Test
    void findAll() {
        //when
        appUserDAO.findAll();
        //then
        verify(appUserRepo).findAll();
    }


    @Test
    void existsById() {
        //when
        appUserDAO.existsById(userId);
        //then
        verify(appUserRepo).existsById(userId);
    }

    @Test
    void save() {
        //given
        AppUser appUser = AppUser.builder().userId(userId).chatId(chatId).uuid(uuid).build();
        //when
        appUserDAO.save(appUser);
        //then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepo).save(appUserArgumentCaptor.capture());
        AppUser captured = appUserArgumentCaptor.getValue();
        assertThat(captured).isEqualTo(appUser);

    }

    @Test
    void getStateByCommand() {
        //given
        String command = "/start";
        //when
        appUserDAO.existsCommand(command);
        //then
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(botStateRepo).existsByCommand(valueCaptor.capture());
        String captured = valueCaptor.getValue();
        assertThat(captured).isEqualTo(command);
    }

    @Test
    void getState() {
        //given
        String state = "start";
        //when
        appUserDAO.getState(state);
        //then
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(botStateRepo).getByState(valueCaptor.capture());
        String captured = valueCaptor.getValue();
        assertThat(captured).isEqualTo(state);
    }

    @Test
    void existsCommand() {
        //given
        String state = "/start";
        //when
        appUserDAO.existsCommand(state);
        //then
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        verify(botStateRepo).existsByCommand(valueCaptor.capture());
    }

    @Test
    void findLang() {
        //when
        String lang = "ENGLISH";
        Locale locale = Locale.builder().lang(lang).build();
        //then
        lenient().when(localeRepo.findByLang(lang)).thenReturn(Optional.of(locale));
    }

    @Test
    void getAllLanguages() {
        //when
        appUserDAO.getAllLanguages();
        //then
        verify(localeRepo).findAll();
    }

    @Test
    void findByUUID() {
        //when
        AppUser appUser = AppUser.builder().userId(userId).chatId(chatId).uuid(uuid).build();
        //then
        lenient().when(appUserRepo.findByUuid(uuid)).thenReturn(Optional.of(appUser));
    }
}