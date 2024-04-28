package ru.nsu.ccfit.muratov.distributed.crack.worker.service;

import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.distributed.crack.worker.model.Cracker;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrackServiceImpl implements CrackService {
    private final Cracker cracker;

    public CrackServiceImpl() throws NoSuchAlgorithmException {
        List<String> list = new ArrayList<>();
        for(char symbol = 'a'; symbol <= 'z'; symbol++) {
            list.add(String.valueOf(symbol));
        }
        for(char symbol = '0'; symbol <= '9'; symbol++) {
            list.add(String.valueOf(symbol));
        }

        cracker = new Cracker("MD5", list);
    }

    @Override
    public List<String> crack(String hash, int maxLength) {
        return cracker.crack(hash, maxLength);
    }
}
