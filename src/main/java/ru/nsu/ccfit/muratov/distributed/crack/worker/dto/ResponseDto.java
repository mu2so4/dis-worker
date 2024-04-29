package ru.nsu.ccfit.muratov.distributed.crack.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
    private String requestId;
    private String[] data;
}
