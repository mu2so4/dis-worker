package ru.nsu.ccfit.muratov.distributed.crack.worker.model;

import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;

public class Cracker {
    private final ICombinatoricsVector<String> alphabet;

    private final MessageDigest digest;

    public Cracker(List<String> alphabet) {
        this.alphabet = CombinatoricsFactory.createVector(alphabet);
        try {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> crack(String hash, int maxLength) {
        hash = hash.toLowerCase();
        List<String> result = new ArrayList<>();

        for(int length = 1; length <= maxLength; length++) {
            Generator<String> generator = createPermutationWithRepetitionGenerator(alphabet, length);
            for (ICombinatoricsVector<String> permutation: generator) {
                String probe = String.join("", permutation.getVector());
                byte[] probeHashBytes = digest.digest(probe.getBytes(StandardCharsets.UTF_8));
                String probeHash = bytesToHex(probeHashBytes);
                if(probeHash.equals(hash)) {
                    result.add(probe);
                }
            }
        }

        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for(char symbol = 'a'; symbol <= 'z'; symbol++) {
            list.add(String.valueOf(symbol));
        }
        for(char symbol = '0'; symbol <= '9'; symbol++) {
            list.add(String.valueOf(symbol));
        }

        Cracker cracker = new Cracker(list);
        Scanner scanner = new Scanner(System.in);
        String hash = scanner.nextLine();
        int maxLength = scanner.nextInt();
        List<String> result = cracker.crack(hash, maxLength);
        for(String sub: result) {
            System.out.println(sub);
        }
    }
}
