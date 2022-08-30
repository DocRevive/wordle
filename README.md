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

## Solver commands
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

## Solver examples

setgreen's argument is a sequence of 5 letters with green letters in the right positions and periods (.) as placeholders for non-green letters.
```
Wordle guess: oners
Wordle result: green (o), green (n), yellow (e), gray (r), yellow (s)

Commands you should run:
setgreen on...
addgray r
addyellow e 2
addyellow s 4
choices 3

choices result: {onset=5}
```
5 is the word's "score" relative to other possibilities. Since there is only one, "onset," it is the answer.
```
Command you should run: newgame

Wordle guess: soare
Wordle result: gray (s), gray (o), green (a), gray (r), gray (e)

Commands you should run:
setgreen ..a..
addgray s,o,r,e
choices 3

choices result: {clank=450, liang=449, thali=446}
```
Here, the top two possibilities the solver has given have almost the same score. By the solver's metrics, they are nearly equally helpful, but the Wordle dictionary contains many rare words that will never be picked as actual solutions. If there is a tie or a near tie, choose the word that is more familiar.
```
Wordle guess: clank
Wordle result: gray (c), gray (l), green (a), green (n), gray (k)

Commands you should run:
setgreen ..an.
addgray c,l,k
choices 3

choices result: {thang=79, giant=78, hiant=76}
```
```
Wordle guess: thang
Wordle result: green (t), gray (h), green (a), green (n), green (g)

Commands you should run:
setgreen t.ang
addgray h
choices 3

choices result: {twang=5}
```
The solution is "twang."

## Game commands

Enter letters, 'skip' to give up, 'exit' to stop. 

## Requirements
+ Java SE 11
+ A terminal that supports ANSI escape sequences. The new Windows Terminal (Windows 10 & 11) supports this by default. Third party terminals or wrappers may enable support.
## Usage
Clone this repo
```
java -jar Wordle.jar
```
