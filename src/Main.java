import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Provides user interface for WordleSolver
 *
 * @author Daniel Kim
 * @version 2-16-22
 */
public class Main
{
    public static void main(String[] args)
    {
        WordleSolver solver = new WordleSolver();
        Scanner input = new Scanner(System.in);
        boolean validPath = false;

        // User decides the mode
        System.out.println("Choose one:\n[1] Play Wordle\n[2] Solve Wordle");
        String choice = input.nextLine().toLowerCase();
        if (choice.equals("1") || choice.equals("play")) {
            Wordle.main(new String[0]);
            return;
        }

        if (!(choice.equals("2") || choice.equals("solve"))) {
            System.exit(0);
        }

        // Ensure a valid vocab file is loaded
        do {
            System.out.println("Path to vocab file or 'default':");
            if (solver.loadProcessStoreVocab(
                    input.nextLine().equals("default") ? "/vocabulary.txt" : input.nextLine())
            ) {
                validPath = true;
            } else {
                System.out.println("Failed to open file.");
            }
        } while (!validPath);

        System.out.println("Enter command or 'help':");

        commandLoop:
        while (true) {
            String[] command = input.nextLine().replaceAll("\\s+", " ").split(" "); // separate command & arguments
            char charArg;

            switch (command[0]) {
                case "newgame":
                    solver.setGreen(".....");
                    solver.setGray(new char[0]);
                    solver.setYellow(new HashMap<>());
                    System.out.println("Done");
                    break;
                case "exit":
                    break commandLoop;
                case "setvocabfile":
                    if (invalidArgCount(command, 1)) {
                        continue;
                    }

                    if (solver.loadProcessStoreVocab(command[1])) {
                        System.out.println("Done");
                    } else {
                        System.out.println("Failed to open file.");
                    }
                    break;
                case "vocabsize":
                    System.out.println(solver.getVocabLength());
                    break;
                case "choices":
                    if (invalidArgCount(command, 1)) {
                        continue;
                    }
                    try {
                        System.out.println(solver.findBestChoices(parseInt(command[1])));
                    } catch (NumberFormatException e) {
                        System.out.println("Argument 1 must be an integer quantity");
                    }
                    break;
                case "setgreen":
                    if (invalidArgCount(command, 1)) {
                        continue;
                    }

                    solver.setGreen(command[1]);
                    System.out.println("Done");
                    break;
                case "viewgreen":
                    System.out.println(solver.getGreen());
                    break;
                case "addyellow": {
                    if (invalidArgCount(command, 2)) {
                        continue;
                    }

                    String[] stringSep = command[2].split(",");
                    Integer[] indices = new Integer[stringSep.length];

                    // Validate letter
                    if (validateCharArg(command[1])) {
                        charArg = command[1].charAt(0);
                    } else {
                        System.out.println("Argument 1 must be a single alphabetic character.");
                        continue;
                    }

                    // Catch invalid nonnumeric indices for argument 2
                    try {
                        for (int i = 0; i < stringSep.length; i++) {
                            indices[i] = parseInt(stringSep[i]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Argument 2 must be a comma-separated sequence of 0-based integer indices.");
                    }

                    solver.addYellow(Character.toLowerCase(charArg), new ArrayList<>(Arrays.asList(indices)));
                    System.out.println("Added");
                    break;
                }
                case "removeyellow": {
                    if (invalidArgCount(command, 1)) {
                        continue;
                    }

                    String[] stringSep = command[1].split(",");
                    StringBuilder notFound = new StringBuilder();
                    HashMap<Character, ArrayList<Integer>> yellow = solver.getYellow();

                    for (String s : stringSep) {
                        // Validate letter
                        if (validateCharArg(s)) {
                            charArg = s.charAt(0);
                        } else {
                            System.out.println("Argument 1 must be a comma-separated sequence of alphabetic characters.");
                            continue commandLoop;
                        }

                        if (yellow.containsKey(charArg)) {
                            yellow.remove(charArg);
                        } else {
                            notFound.append((notFound.length() == 0) ? charArg : ", " + charArg);
                        }
                    }

                    solver.setYellow(yellow);

                    if (notFound.length() > 0) {
                        System.out.println("Couldn't find " + notFound + ". Any others were removed.");
                    } else {
                        System.out.println("Removed");
                    }
                    break;
                }
                case "viewyellow":
                    System.out.println(solver.getYellow());
                    break;
                case "addgray": {
                    if (invalidArgCount(command, 1)) {
                        continue;
                    }

                    String[] stringSep = command[1].split(",");
                    String currentGray = new String(solver.getGray());
                    StringBuilder found = new StringBuilder();
                    char[] grayChars = new char[stringSep.length];

                    for (int i = 0; i < stringSep.length; i++) {
                        // Validate letter
                        if (validateCharArg(stringSep[i])) {
                            charArg = stringSep[i].charAt(0);
                        } else {
                            System.out.println("Argument 1 must be a comma-separated sequence of alphabetic characters.");
                            continue commandLoop;
                        }

                        if (currentGray.contains(String.valueOf(charArg))) {
                            found.append((found.length() == 0) ? charArg : ", " + charArg);
                        } else {
                            grayChars[i] = Character.toLowerCase(charArg);
                        }
                    }

                    solver.addGray(grayChars);

                    if (found.length() > 0) {
                        System.out.println("Already had " + found + ". Any others were added.");
                    } else {
                        System.out.println("Added");
                    }
                    break;
                }
                case "removegray": {
                    if (invalidArgCount(command, 1)) {
                        continue;
                    }

                    String removeChars = command[1].replace(",", "");
                    StringBuilder notFound = new StringBuilder();
                    char[] charGray = solver.getGray();
                    String stringGray = new String(charGray);
                    char[] newChars;
                    int newIndex = 0, notFoundCount = 0;

                    // First, validate argument and store notFound letters
                    for (int i = 0; i < removeChars.length(); i++) {
                        if (validateCharArg(String.valueOf(removeChars.charAt(i)))) {
                            charArg = removeChars.charAt(i);
                        } else {
                            System.out.println("Argument 1 must be a comma-separated sequence of alphabetic characters.");
                            continue commandLoop;
                        }

                        if (!stringGray.contains(String.valueOf(charArg))) {
                            notFound.append((notFound.length() == 0) ? charArg : ", " + charArg);
                            notFoundCount++;
                        }
                    }

                    newChars = new char[charGray.length - removeChars.length() + notFoundCount];

                    // Find and store non-removed letters
                    for (char c : charGray) {
                        if (!removeChars.contains(String.valueOf(c))) {
                            newChars[newIndex] = c;
                            newIndex++;
                        }
                    }

                    solver.setGray(newChars);

                    if (notFound.length() > 0) {
                        System.out.println("Couldn't find " + notFound + ". Any others were removed.");
                    } else {
                        System.out.println("Removed");
                    }
                    break;
                }
                case "viewgray":
                    char[] gray = solver.getGray();
                    System.out.println(gray.length > 0 ? (String.join(", ", (new String(gray)).split(""))) : "{}");
                    break;
                case "help":
                    System.out.println("Commands:\n\n" +
                            "setvocabfile <String>      sets path to vocabulary file \n" +
                            "vocabsize                  gets number of words in vocabulary \n" +
                            "choices <int>              finds top <int> word choices\n" +
                            "setgreen <String>          sets green letter config\n" +
                            "viewgreen                  displays green letter config\n" +
                            "addyellow <char> <ints>    adds yellow letter with comma-separated incorrect 0-based indices\n" +
                            "removeyellow <chars>       remove comma-separated yellow letter(s)\n" +
                            "viewyellow                 displays entire yellow information\n" +
                            "addgray <chars>            adds comma-separated gray letter(s)\n" +
                            "removegray <chars>         removes comma-separated gray letter(s)\n" +
                            "viewgray                   displays all gray characters\n" +
                            "newgame                    resets solver information\n" +
                            "exit                       terminate program"
                    );
                    break;
                default:
                    System.out.println("Command '" + command[0] + "' doesn't exist. 'help' to see command list");
                    break;
            }
        }
    }

    /**
     * Determines whether a character argument is a character
     *
     * @param  c  character argument
     * @return    true if valid, false if invalid
     */
    private static boolean validateCharArg(String c)
    {
        if (c.isEmpty()) {
            return false;
        } else {
            return Character.isLetter(c.charAt(0)) && c.length() == 1;
        }
    }

    /**
     * Checks number of command args and warns if it is wrong
     *
     * @param  command           name of command to reference
     * @param  requiredArgCount  necessary number of args
     * @return                   true if invalid, false if valid
     */
    private static boolean invalidArgCount(String[] command, int requiredArgCount)
    {
        if (command.length - 1 != requiredArgCount) {
            System.out.println("'" + command[0] + "' requires " + requiredArgCount +
                    " argument" + (requiredArgCount > 1 ? "s" : "") + "; 'help' to view");
            return true;
        } else {
            return false;
        }
    }
}
