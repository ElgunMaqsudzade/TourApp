package az.code.tourapp.jobs;

import az.code.tourapp.components.MessageSender;
import az.code.tourapp.daos.interfaces.AppUserDAO;
import az.code.tourapp.dtos.TimerInfoDTO;
import az.code.tourapp.models.AppUser;
import az.code.tourapp.models.Offer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SendOfferJob implements Job {
    MessageSender bot;
    AppUserDAO appUserDAO;

    public SendOfferJob(MessageSender bot, AppUserDAO appUserDAO) {
        this.bot = bot;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<Offer> infoDTO = (TimerInfoDTO<Offer>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        Offer offer = infoDTO.getData();
        AppUser user = appUserDAO.findByUUID(offer.getUUID());
        Long chatId = user.getChatId();
        String text = offer.getMessage();

        ByteArrayResource resource = new ByteArrayResource(offer.getFileAsBytes());
        bot.sendPhoto(SendPhoto
                .builder()
                .chatId(String.valueOf(chatId))
                .caption(text)
                .parseMode(ParseMode.HTML)
                .photo(getPhoto(resource))
                .build());
    }


    @SneakyThrows
    private InputFile getPhoto(ByteArrayResource r) {
        return new InputFile(r.getInputStream(), UUID.randomUUID().toString());
    }

    private MessageEntity getCaption(String text) {
        return MessageEntity.builder().text(text).type("bold").length(4).offset(21).build();
    }

    @SneakyThrows
    private Collection<? extends InputMedia> getMediaList(Set<MultipartFile> files, String text) {

        List<InputMedia> photos = files
                .stream()
                .map(f -> getMedia(f, text))
                .collect(Collectors.toList());

        return photos;
    }

    @SneakyThrows
    private InputMedia getMedia(MultipartFile f, String text) {
        return InputMediaPhoto.builder()
                .media(f.getContentType())
                .mediaName(f.getOriginalFilename())
                .caption(text)
                .newMediaStream(f.getInputStream())
                .isNewMedia(true)
                .build();
    }
}
