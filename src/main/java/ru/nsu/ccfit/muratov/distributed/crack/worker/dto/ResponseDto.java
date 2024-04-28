package ru.nsu.ccfit.muratov.distributed.crack.worker.dto;

import lombok.Data;

@Data
public class ResponseDto {
    String requestId;
    String[] words;
}
