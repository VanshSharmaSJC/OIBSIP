@echo off
REM Compile and run. Optional arg: exam length in minutes (default 30), e.g. run.bat 1
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\exam\*.java && java -cp out exam.Main %*
