package ru.nsu.ccfit.muratov.distributed.crack.worker.service;

import java.util.List;

public interface CrackService {
    List<String> crack(String hash, int maxLength);
}
