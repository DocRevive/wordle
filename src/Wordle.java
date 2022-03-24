import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Implementation of the 'Wordle' word game in Java
 *
 * @author Daniel Kim
 * @version 3-23-22
 */
public class Wordle {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_GRAY_BACKGROUND = "\u001B[100m";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] guesses = new String[6];
        List<String> vocabulary;
        char[] answer;

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/vocabulary.txt"));
            vocabulary = br.lines().collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't load the vocabulary: " + e.getMessage());
        }

        // Set blank guesses
        for (int i = 0; i < 6; i++) {
            guesses[i] = "-----";
        }

        while (true) {
            boolean correct = true;
            int guessIndex = 0;
            char current;
            ArrayList<Character> green = new ArrayList<>();
            ArrayList<Character> gray = new ArrayList<>();
            ArrayList<Character> yellow = new ArrayList<>();


            // The first 2309 elements are pre-decided, rational words
            answer = vocabulary.get((int) (Math.random() * 2308)).toCharArray();
            System.out.println("Guess!");

            while (guessIndex < 6) {
                StringBuilder alphabetInfo = new StringBuilder();
                String guess = input.nextLine().toLowerCase();
                String[] guessColors = new String[5];
                /*
                 * As green/yellow/gray status of each letter is calculated,
                 * keep track of which hints have been given out as to keep
                 * them accurate (e.g. prevent two yellows for one letter)
                 */
                char[] answerInfo = answer.clone();

                correct = true;

                // Exit the program
                if (guess.equals("stop") || guess.equals("exit")) {
                    System.exit(0);
                }

                // Give up
                if (guess.equals("next") || guess.equals("new") || guess.equals("skip")) {
                    correct = false;
                    break;
                }

                if (guess.length() != 5) {
                    System.out.println("Must be 5 letters!");
                    continue;
                }

                if (!vocabulary.contains(guess)) {
                    System.out.println("Not in the vocabulary!");
                    continue;
                }

                /*
                 * First, look for correct positions. Then, consider yellow/gray.
                 * Order matters for double-letter situations
                 */
                for (int i = 0; i < 5; i++) {
                    current = guess.charAt(i);

                    if (current == answerInfo[i]) {
                        guessColors[i] = ANSI_GREEN_BACKGROUND + current + ANSI_RESET;
                        answerInfo[new String(answerInfo).indexOf(current)] = '0';
                        if (!green.contains(current)) green.add(current);
                    }
                }

                for (int i = 0; i < 5; i++) {
                    current = guess.charAt(i);

                    if (guessColors[i] == null) {
                        correct = false;

                        if (new String(answerInfo).contains(String.valueOf(current))) {
                            guessColors[i] = ANSI_YELLOW_BACKGROUND + current + ANSI_RESET;
                            answerInfo[new String(answerInfo).indexOf(current)] = '0';
                            if (!yellow.contains(current)) yellow.add(current);
                        } else {
                            guessColors[i] = String.valueOf(current);
                            if (!gray.contains(current)) gray.add(current);
                        }
                    }
                }

                for (int i = 0; i < 26; i++) {
                    if (green.contains(ALPHABET.charAt(i))) {
                        alphabetInfo.append(ANSI_GREEN_BACKGROUND);
                    } else if (yellow.contains(ALPHABET.charAt(i))) {
                        alphabetInfo.append(ANSI_YELLOW_BACKGROUND);
                    } else if (gray.contains(ALPHABET.charAt(i))) {
                        alphabetInfo.append(ANSI_GRAY_BACKGROUND);
                    } else {
                        alphabetInfo.append(ALPHABET.charAt(i));
                        continue;
                    }
                    alphabetInfo.append(ALPHABET.charAt(i)).append(ANSI_RESET);
                }

                guesses[guessIndex] = String.join("", guessColors);
                System.out.println(String.join("\n", guesses));
                System.out.println(alphabetInfo);

                guessIndex++;

                if (correct) break;
            }

            if (correct) {
                System.out.println("Nice! That took " + guessIndex + " tries!");
            } else {
                System.out.println("The answer was: " + new String(answer));
            }

            // Reset guesses
            for (int i = 0; i < guessIndex; i++) {
                guesses[i] = "-----";
            }
        }
    }
}
