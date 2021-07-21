package az.code.tourapp.utils;

import az.code.tourapp.configs.BotConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class ImageUtil {
    String root;
    String extension;

    public ImageUtil(BotConfig config) {
        this.root = config.getOffer().getImage().getRoot();
        this.extension = config.getOffer().getImage().getExtension();
    }

    @SneakyThrows
    public InputFile getInputFile(byte[] array) {
        ByteArrayResource res = new ByteArrayResource(array);
        return new InputFile(res.getInputStream(), UUID.randomUUID().toString());
    }

    @SneakyThrows
    public String saveImage(byte[] bytes) {
        File file = new File(root);
        if (!file.exists()) {
            file.mkdirs();
        }
        Path fullPath = Paths.get(root + "/" + UUID.randomUUID() + extension);
        Files.write(fullPath, bytes);
        return fullPath.toString();
    }

    @SneakyThrows
    public void deleteImage(String path) {
        Files.deleteIfExists(Paths.get(path));
    }
}