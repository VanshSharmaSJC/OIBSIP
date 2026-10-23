@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\guess\*.java && java -cp out guess.ConsoleGame
