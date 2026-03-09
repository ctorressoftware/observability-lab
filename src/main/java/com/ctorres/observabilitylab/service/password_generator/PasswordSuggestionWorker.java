package com.ctorres.observabilitylab.service.password_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class PasswordSuggestionWorker implements Callable<String> {
    private final int MAX_PASSWORD = 16;
    private final Random random = new Random();
    private final String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String lowerChars = "abcdefghijklmnopqrstuvwxyz";
    private final String numberChars = "1234567890";
    private final String specialChars = "!@#$%^&*";
    private final List<Character> usedChars = new ArrayList<>(0);

    public PasswordSuggestionWorker() {}

    @Override
    public String call() throws Exception {

        final var password = new StringBuilder(MAX_PASSWORD);

        for (int i = 0; i < password.length(); i++) {

            if (password.isEmpty()) {
                char first = getFirstCharacter();
                password.append(first);
                usedChars.add(first);
            }

            char newCharacter = getNewRandomCharacter();
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
        if (usedChars.contains(randomChar)) getNewRandomCharacter();
        return randomChar;
    }

    private int index(int bound) {
        return random.nextInt(bound);
    }
}
