#!/bin/bash
# Compile and run. Optional arg: exam length in minutes (default 30), e.g. ./run.sh 1
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/exam/*.java && java -cp out exam.Main "$@"
