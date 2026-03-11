package com.ctorres.observabilitylab.service.password_generator;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.Callable;

public class PasswordSuggestionWorker implements Callable<String> {
    private final int MAX_PASSWORD = 32;
    private final Random random = new Random();
    private final String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String lowerChars = "abcdefghijklmnopqrstuvwxyz";
    private final String numberChars = "1234567890";
    private final String specialChars = "!@#$%^&*";
    private final Set<Character> usedChars = new HashSet<>(0);

    public PasswordSuggestionWorker() {}

    @Override
    public String call() {

        final var password = new StringBuilder(MAX_PASSWORD);

        for (int i = 0; i < MAX_PASSWORD; i++) {
            char newCharacter = password.isEmpty() ?  getFirstCharacter() : getNewRandomCharacter();
            password.append(newCharacter);
            usedChars.add(newCharacter);
        }

        return password.toString();
    }

    private char getFirstCharacter() {
        var alphabeticChars = upperChars + lowerChars;
        return alphabeticChars.charAt(index(alphabeticChars.length()));
    }

    private char getNewRandomCharacter() {
        var allSupportedCharacters = upperChars + lowerChars + numberChars + specialChars;
        char randomChar = allSupportedCharacters.charAt(index(allSupportedCharacters.length()));
        if (usedChars.contains(randomChar)) return getNewRandomCharacter();
        return randomChar;
    }

    private int index(int bound) {
        return random.nextInt(bound);
    }
}
