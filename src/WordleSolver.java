import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Solver for the game "Wordle"
 *
 * @author Daniel Kim
 * @version 2-15-22
 */
public class WordleSolver
{
    private HashMap<Character, Integer> letterFrequencies = new HashMap<>();
    private HashMap<Character, ArrayList<Integer>> yellow = new HashMap<>();
    private String[] vocabulary;
    private String green = ".....";
    private char[] gray = new char[0];

    /**
     * Empty constructor for objects of class Wordler
     */
    public WordleSolver()
    {}

    /**
     * Overloaded constructor for a new game of Wordle
     *
     * @param  vocabFile  path to file with space-separated 5-letter words; wordle vocabulary
     */
    public WordleSolver(String vocabFile)
    {
        loadProcessStoreVocab(vocabFile);
    }

    /**
     * Overloaded constructor for an ongoing game of Wordle
     *
     * @param  vocabFile  path to list of space-separated 5-letter words; wordle vocabulary
     * @param  green      correct letters in correct positions (green letters in wordle)
     * @param  gray       incorrect letters (gray letters in wordle)
     * @param  yellow     correct letters (letter in incorrect positions (yellow letters in wordle)
     */
    public WordleSolver(String vocabFile, String green, char[] gray, HashMap<Character, ArrayList<Integer>> yellow)
    {
        this.green = green.toLowerCase();
        this.gray = gray;
        this.yellow = yellow;
        loadProcessStoreVocab(vocabFile);
    }

    /*
     * Accessors
     */
    /**
     * Finds best word choices mid-game, using all hints
     * in object's current green, yellow, and gray settings
     *
     * @param  quantity  number of choices to return
     * @return           ordered HashMap of words mapped to rankings
     */
    public LinkedHashMap<String, Integer> findBestChoices(int quantity)
    {
        ArrayList<String> validVocab = new ArrayList<>();

        // Find all words in vocabulary that work with the information
        words:
        for (String s : vocabulary) {
            String currentWord = s;

            /*
             * Check if word satisfies all known greens & their positions
             */
            for (int y = 0; y < green.length(); y++) {
                if (Character.isLetter(green.charAt(y))) {
                    // If word has this green letter at the right position
                    if (green.charAt(y) == s.charAt(y)) {
                        /*
                         * Replace letter with space (preserve indices). If the letter
                         * occurs again in greens or in yellows, make sure this word
                         * has multiple instances of the same letter. Keeping the letter
                         * means a future iteration checking for the same letter could
                         * see the one instance and validate it (which is wrong).
                         */
                        currentWord = currentWord.substring(0, y) + ' ' + currentWord.substring(y + 1);
                    } else {
                        /*
                         * If word doesn't have at least one green at a certain position,
                         * it's wrong, so move on to the next word. This will be referred
                         * to as skipping the current word.
                         */
                        continue words;
                    }
                }
            }

            /*
             * Check if word satisfies all known yellow information
             */
            for (Map.Entry<Character, ArrayList<Integer>> entry : yellow.entrySet()) {
                // If the word has the current yellow character
                if (s.contains(String.valueOf(entry.getKey()))) {
                    // Loop through letters, counting index
                    for (int y = 0; y < currentWord.length(); y++) {
                        // If the character at the index is the yellow character
                        if (currentWord.charAt(y) == entry.getKey()) {
                            // If the index is one of the yellow character's known incorrect indices
                            if (entry.getValue().contains(y)) {
                                // Wrong word, skip it
                                continue words;
                            } else {
                                /*
                                 * If it is not an incorrect index, it could be the correct location
                                 * of the yellow character. Let the word continue to the next yellow
                                 * or onto grays, stripping the possible character for the same
                                 * reasoning above.
                                 */
                                currentWord = currentWord.substring(0, y) + ' ' + currentWord.substring(y + 1);
                            }
                        }
                    }
                } else {
                    // If the word lacks any one yellow character, skip this word
                    continue words;
                }
            }

            for (char c : gray) {
                // Skip if it has a gray
                if (currentWord.contains(String.valueOf(c))) {
                    continue words;
                }
            }

            // The word fits the known information, add to a list of candidates
            validVocab.add(s);
        }

        // Turn dynamically-sized ArrayList to fixed-size array
        String[] arrayVocab = new String[validVocab.size()];
        arrayVocab = validVocab.toArray(arrayVocab);

        // Find best words within this set
        return findBestWords(arrayVocab, generateLetterFreq(arrayVocab), quantity);
    }

    /**
     * Finds the best openers for Wordle. Same as 'findBestWords'
     * but with the object's entire vocabulary and frequencies.
     *
     * @param  quantity  number of the best openers to return
     * @return           ordered HashMap of 5-letter words mapped to rankings
     */
    public LinkedHashMap<String, Integer> findBestOpeners(int quantity)
    {
        return findBestWords(vocabulary, letterFrequencies, quantity);
    }

    /**
     * Gets known correct letters in certain correct positions
     * (green letters in wordle)
     *
     * @return 5-character String with '.' placeholder for unknowns
     */
    public String getGreen()
    {
        return green;
    }

    /**
     * Gets known correct letters in certain incorrect positions
     * (yellow letters in wordle)
     *
     * @return letters map to ArrayLists of indices of known wrong positions
     */
    public HashMap<Character, ArrayList<Integer>> getYellow()
    {
        return yellow;
    }

    /**
     * Gets known incorrect, excluded letters (gray letters in wordle)
     *
     * @return char array of letters to exclude
     */
    public char[] getGray()
    {
        return gray;
    }

    /**
     * Returns the length of the vocabulary array
     *
     * @return length of vocab
     */
    public int getVocabLength()
    {
        return vocabulary.length;
    }

    /*
     * Mutators
     */
    /**
     * Sets known correct letters in certain correct positions
     * (green letters in wordle)
     *
     * @param  green  5-character String with '.' placeholder for unknowns
     */
    public void setGreen(String green)
    {
        this.green = green.toLowerCase();
    }

    /**
     * Sets known correct letters in certain incorrect positions
     * (yellow letters in wordle)
     *
     * @param  yellow  letters map to ArrayLists of indices of known wrong positions
     */
    public void setYellow(HashMap<Character, ArrayList<Integer>> yellow)
    {
        this.yellow = yellow;
    }

    /**
     * Sets known incorrect, excluded letters (gray letters in wordle)
     *
     * @param  gray  char array of letters to exclude
     */
    public void setGray(char[] gray)
    {
        this.gray = gray;
    }

    /**
     * Adds known correct letters in certain incorrect positions
     * (yellow letters in wordle)
     *
     * @param  yellow  letters map to ArrayLists of indices of known wrong positions
     */
    public void addYellow(HashMap<Character, ArrayList<Integer>> yellow)
    {
        for (Map.Entry<Character, ArrayList<Integer>> entry : yellow.entrySet()) {
            addYellow(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds known correct letter in a certain incorrect position
     * (yellow letters in wordle)
     *
     * @param  letter    specific included letter
     * @param  wrongPos  Integer ArrayList of known incorrect positions
     */
    public void addYellow(char letter, ArrayList<Integer> wrongPos)
    {
        if (this.yellow.containsKey(letter)) {
            this.yellow.get(letter).addAll(wrongPos);
        } else {
            this.yellow.put(letter, wrongPos);
        }
    }

    /**
     * Adds known incorrect, excluded letters (gray letters in wordle)
     *
     * @param  gray  char array of letters to exclude
     */
    public void addGray(char[] gray)
    {
        int oldLen = this.gray.length;
        int addLen = gray.length;
        char[] combined = new char[oldLen + addLen];

        System.arraycopy(this.gray, 0, combined, 0, oldLen);
        System.arraycopy(gray, 0, combined, oldLen, addLen);

        this.gray = combined;
    }

    /**
     * Loads, processes, and stores vocab file and calls letter
     * frequency generation
     *
     * @param  vocabFile  path to file with space-separated 5-letter words
     * @return            success
     */
    public boolean loadProcessStoreVocab(String vocabFile)
    {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(
                            Class.forName("Wordle").getResourceAsStream(vocabFile)
                    )
            ));
            vocabulary = br.lines().collect(Collectors.joining("\n")).split("\n");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load the vocabulary: " + e.getMessage());
        }

        letterFrequencies = generateLetterFreq(vocabulary);
        return true;
    }

    /*
     * Static Methods
     */
    /**
     * Given any vocabulary and letter frequencies, go through each word
     * and pick ones that will reveal the most information
     *
     * @param  vocabulary         String array of words to calculate & compare
     * @param  letterFrequencies  HashMap of letters mapped to counted occurrences
     * @param  quantity           number of best words to return
     * @return                    ordered HashMap of words mapped to rankings
     */
    public static LinkedHashMap<String, Integer> findBestWords(String[] vocabulary, HashMap<Character, Integer> letterFrequencies, int quantity)
    {
        ArrayList<String> bestWords = new ArrayList<>(Collections.nCopies(quantity, ""));
        ArrayList<Integer> bestSums = new ArrayList<>(Collections.nCopies(quantity, 0));
        HashMap<String, Integer> bestFinal = new HashMap<>();

        /*
         * Criteria for "best" words:
         *
         * - All letters are unique, and variety of letters cover more possibilities.
         *
         * - Word has high-frequency component letters; measured by calculating
         *   and comparing sums of frequencies. They are more likely to reveal greens
         *   or yellows, which are the best hints.
         */

        for (String s : vocabulary) {
            int sum = 0;

            // Iterate through letters
            for (int y = 0; y < s.length(); y++) {
                char currentChar = s.charAt(y);
                // Does not include repeated letters in sum
                if (s.indexOf(currentChar, y + 1) == -1) {
                    sum += letterFrequencies.get(currentChar);
                }
            }

            /*
             * Loop through current top choices to see if this word qualifies
             */
            for (int y = 0; y < quantity; y++) {
                // If this word's sum is higher than one of the current top words
                if (sum > bestSums.get(y)) {
                    // Get the index of the lowest sum (last place)
                    int minIndex = bestSums.indexOf(Collections.min(bestSums));
                    // Replace last place with this word
                    bestSums.set(minIndex, sum);
                    bestWords.set(minIndex, s);
                    break;
                }
            }
        }

        // Combine discrete lists (bestWords, bestSums as keys, values, respectively) into HashMap
        for (int i = 0; i < quantity; i++) {
            String word = bestWords.get(i);
            if (word != null && !word.isEmpty()) {
                bestFinal.put(word, bestSums.get(i));
            }
        }

        // Sort HashMap and store in order-preserving LinkedHashMap
        LinkedHashMap<String, Integer> sorted = new LinkedHashMap<>();
        bestFinal.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(entry -> sorted.put(entry.getKey(), entry.getValue()));

        return sorted;
    }

    /**
     * Generates letter frequencies of provided vocabulary
     *
     * @param  vocabulary  String array of words to get frequencies from
     * @return             HashMap of letters mapped to counted occurrences
     */
    public static HashMap<Character, Integer> generateLetterFreq(String[] vocabulary)
    {
        HashMap<Character, Integer> letterFrequencies = new HashMap<>();

        for (String s : vocabulary) {
            for (int y = 0; y < s.length(); y++) {
                char letter = s.charAt(y);
                letterFrequencies.put(letter, letterFrequencies.getOrDefault(letter, 0) + 1);
            }
        }

        return letterFrequencies;
    }
}
