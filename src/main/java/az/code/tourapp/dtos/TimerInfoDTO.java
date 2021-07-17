package az.code.tourapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TimerInfoDTO<T> {
    private int fireCount;
    private String cron;
    private boolean forever;
    private long intervalMS;
    private long offsetMS;
    private T data;
}