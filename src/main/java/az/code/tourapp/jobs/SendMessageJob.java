package az.code.tourapp.jobs;

import az.code.tourapp.components.MessageSender;
import az.code.tourapp.dtos.MessageDTO;
import az.code.tourapp.dtos.TimerInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Set;

@Component
@Slf4j
public class SendMessageJob implements Job {
    MessageSender bot;

    public SendMessageJob(MessageSender sendMessage) {
        this.bot = sendMessage;
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        TimerInfoDTO<MessageDTO> infoDTO = (TimerInfoDTO<MessageDTO>) ctx.getJobDetail().getJobDataMap().get(this.getClass().getSimpleName());
        MessageDTO message = infoDTO.getCallbackData();
        String chatId = message.getChatId();
        String text = message.getMessage();

        Set<MultipartFile> files = message.getFiles();
        files
                .stream()
                .parallel()
                .forEach(i -> bot.sendPhoto(SendPhoto.builder().photo(new InputFile()).photo(convertFile(i)).chatId(chatId).build()));

        bot.sendMessage(SendMessage.builder().chatId(chatId).text(text).build());
    }

    @SneakyThrows
    public InputFile convertFile(MultipartFile f) {

        return new InputFile(f.getOriginalFilename(),
                f.getName(),
                f.getResource().getFile(),
                f.getInputStream(),
                true);
    }
}
