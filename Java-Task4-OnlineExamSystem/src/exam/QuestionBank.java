package exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Holds the exam questions. Order is shuffled for every attempt. */
public class QuestionBank {

    public static List<Question> load() {
        List<Question> q = new ArrayList<>();
        q.add(new Question("Which keyword is used to inherit a class in Java?",
                "implements", "extends", "inherits", "super", 1));
        q.add(new Question("Which of these is NOT a primitive data type in Java?",
                "int", "boolean", "String", "char", 2));
        q.add(new Question("What is the default value of an int instance variable?",
                "null", "0", "undefined", "-1", 1));
        q.add(new Question("Which collection does NOT allow duplicate elements?",
                "ArrayList", "LinkedList", "HashSet", "Vector", 2));
        q.add(new Question("Which method is the entry point of a Java application?",
                "start()", "run()", "main()", "init()", 2));
        q.add(new Question("Which package contains the Swing GUI classes?",
                "java.awt.swing", "javax.swing", "java.gui", "javax.awt", 1));
        q.add(new Question("What does JVM stand for?",
                "Java Variable Machine", "Java Verified Method", "Java Virtual Machine", "Joint Virtual Module", 2));
        q.add(new Question("Which access modifier makes a member visible only inside its own class?",
                "public", "protected", "default", "private", 3));
        q.add(new Question("Which exception is thrown when dividing an integer by zero?",
                "NullPointerException", "ArithmeticException", "IOException", "NumberFormatException", 1));
        q.add(new Question("Which interface must be implemented to create a thread by a class that already extends another class?",
                "Runnable", "Callable", "Serializable", "Cloneable", 0));
        Collections.shuffle(q);
        return q;
    }
}
