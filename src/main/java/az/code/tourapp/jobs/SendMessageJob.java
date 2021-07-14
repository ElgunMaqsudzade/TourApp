package az.code.tourapp.jobs;

import az.code.tourapp.components.MessageSender;
import az.code.tourapp.dtos.OfferDTO;
import az.code.tourapp.dtos.TimerInfoDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.BufferedInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SendMessageJob implements Job {
    MessageSender bot;

    public SendMessageJob(MessageSender sendMessage) {
        this.bot = sendMessage;
    }


    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<OfferDTO> infoDTO = (TimerInfoDTO<OfferDTO>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        OfferDTO message = infoDTO.getCallbackData();
        String chatId = message.getChatId();
        String text = message.getMessage();
        MultipartFile file = message.getFile();

        bot.sendPhoto(SendPhoto
                .builder()
                .chatId(chatId)
                .caption(text)
                .parseMode(ParseMode.HTML)
                .photo(getPhoto(file))
                .build());
    }


    @SneakyThrows
    private InputFile getPhoto(MultipartFile f) {
        System.out.println(f.getInputStream());
        return new InputFile(f.getInputStream(), f.getOriginalFilename());
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
