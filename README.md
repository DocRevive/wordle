# Wordle
Play the hit NYT word game, Wordle, in your terminal. Open two terminals and you can cheat.

This CLI program lets you play Wordle and helps you solve Wordle. Solving is done by taking in all known information: 
- Green letters: correct letters in the correct spots
- Yellow letters: correct letters in the wrong spots
- Gray letters: incorrect letters (aren't in the word)

and finding all the words in the official vocabulary list that fit the criteria. Next, each potential word is ordered from "best" to "worst" to guess based on a score based on these metrics:
- Whether all the letters are unique: a variety of letters cover more possibilities and can reveal more information.
- How frequent each of its letters are in the entire Wordle vocabulary: if a letter is present in more words, it's more likely to be in the word, and more likely to reveal greens or yellows.

If you pass in no information, the best starting words are found (based on that scoring system). Currently, three words are tied for best: "soare", "aeros", and "arose". They are all permutations of each other, since the scoring doesn't factor in the position of each letter. Determining how frequent each letter is in each of the five positions could prove to be useful.

If one of those starting words completely fails (all gray), the next best word, according to the solver, is "unity". 

# Solver commands:
```
setvocabfile <String>      sets path to vocabulary file
vocabsize                  gets number of words in vocabulary
choices <int>              finds top <int> word choices
setgreen <String>          sets green letter config
viewgreen                  displays green letter config
addyellow <char> <ints>    adds yellow letter with comma-separated incorrect 0-based indices
removeyellow <chars>       remove comma-separated yellow letter(s)
viewyellow                 displays entire yellow information
addgray <chars>            adds comma-separated gray letter(s)
removegray <chars>         removes comma-separated gray letter(s)
viewgray                   displays all gray characters
newgame                    resets solver information
exit                       terminate program
```

## Requirements
+ Java SE 11
+ A terminal that supports ANSI escape sequences. The new Windows Terminal (Windows 10 & 11) supports this by default. Third party terminals or wrappers may enable support.
## Usage
Clone this repo
```
java -jar Wordle.jar
```
