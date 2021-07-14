package az.code.tourapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {
    private MultipartFile file;
    private String chatId;
    private String userId;
    private String message;
}
