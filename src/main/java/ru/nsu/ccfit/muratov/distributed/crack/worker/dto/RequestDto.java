package ru.nsu.ccfit.muratov.distributed.crack.worker.dto;

import lombok.Data;

@Data
public class RequestDto {
    private String requestId;
    private String hash;
    private int maxLength;
}
