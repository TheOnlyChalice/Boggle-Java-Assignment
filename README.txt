# Boggle Player – Assignment Project

This project implements and evaluates a **Boggle player** that finds valid words on a 4×4 board using a dictionary. The program combines efficient data structures, depth-first search, and evaluation metrics to measure solver performance.

## 📂 Project Files
- `BogglePlayer.java` – core solver, builds a dictionary tree and searches for words with DFS
- `EvalBogglePlayer.java` – driver program to generate boards and score results
- `Word.java` – represents words and their board paths
- `Location.java` – stores row/column positions on the board
- `words.txt` – dictionary of valid words

## 🎮 Features
- Preprocess dictionary into a tree for fast lookups
- Handles special rules like `Q → QU`
- Finds unique words of length ≥3
- Evaluates based on **points, speed, and memory**
- Scoring formula:  
  \[(points²) / √(time × memory)\]

## 🚀 Run Instructions
Compile and run the driver:

```bash
javac *.java
java EvalBogglePlayer
