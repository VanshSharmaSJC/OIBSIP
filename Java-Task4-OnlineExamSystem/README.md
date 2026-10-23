# Online Examination System (Java Swing)

## Requirements
JDK 8 or newer (`javac -version` to check). No external libraries.

## Run
- Linux/macOS: `./run.sh`
- Windows: `run.bat`
- Short exam for testing the auto-submit timer: `./run.sh 1` (1 minute)

Manual: `mkdir out && javac -d out src/exam/*.java && java -cp out exam.Main`

## Demo accounts
| Username | Password |
|----------|----------|
| student1 | pass123  |
| student2 | pass456  |

`users.txt` is created on first run (passwords stored as SHA-256 hashes); profile changes are saved there. Delete it to reset.

## Features
- Login -> optional profile update (display name / password) -> exam -> result -> logout
- One MCQ at a time, 4 radio buttons (ButtonGroup), Next / Previous, clickable question navigator
- javax.swing.Timer countdown, always visible, turns red in the last minute, auto-submits at 00:00
- Manual submit with confirmation (warns about unanswered questions)
- Result: score X/Y, time taken, correct/incorrect/unanswered counts, per-question breakdown table
- Closing the window mid-exam asks "Are you sure you want to quit?"
- CardLayout switches between screens

## Structure
```
src/exam/Main.java          frame, CardLayout, session/close handling
src/exam/LoginPanel.java    login screen
src/exam/ProfilePanel.java  profile update screen
src/exam/ExamPanel.java     exam UI + timer
src/exam/ResultPanel.java   result screen
src/exam/UserStore.java     file-backed users
src/exam/QuestionBank.java  questions (edit here to add your own)
src/exam/Question.java, User.java, ExamResult.java
```
