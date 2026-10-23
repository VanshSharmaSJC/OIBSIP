package exam;

import java.util.List;

/** Outcome of one exam attempt. answers[i] is the chosen option index, or -1 if unanswered. */
public class ExamResult {
    public final List<Question> questions;
    public final int[] answers;
    public final int secondsTaken;
    public final boolean autoSubmitted;

    public ExamResult(List<Question> questions, int[] answers, int secondsTaken, boolean autoSubmitted) {
        this.questions = questions;
        this.answers = answers;
        this.secondsTaken = secondsTaken;
        this.autoSubmitted = autoSubmitted;
    }

    public int total()      { return questions.size(); }
    public int correct()    { int c = 0; for (int i = 0; i < total(); i++) if (answers[i] == questions.get(i).correctIndex) c++; return c; }
    public int unanswered() { int c = 0; for (int a : answers) if (a < 0) c++; return c; }
    public int incorrect()  { return total() - correct() - unanswered(); }
}
