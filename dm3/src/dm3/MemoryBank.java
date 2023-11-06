package dm3;

import java.util.ArrayList;
import java.util.List;

// Manages a collection of equations for practicing arithmetic and tracking quiz progress.
public class MemoryBank {
    private List<String> equations;
    private int currentQuizIndex;
    private int attempted;
    private int correct;
    private boolean quizMode;

    public MemoryBank() {
        equations = new ArrayList<>();
        currentQuizIndex = 0;
        attempted = 0;
        correct = 0;
        quizMode = false;
    }

    // Adds an equation to the memory bank if there is room.
    public void addEquation(String equation) {
        if (equations.size() < 10) {
            equations.add(equation);
        } else {
            System.out.println("Memory Bank is full."); // Inform that the memory bank is full.
        }
    }

    // Retrieves the next question for the quiz, or ends the quiz if no more questions.
    public String getNextQuizQuestion() {
        if (currentQuizIndex < equations.size()) {
            return equations.get(currentQuizIndex++);
        } else {
            quizMode = false;
            return null; // Return null to signal the end of the quiz.
        }
    }

    // Starts the quiz mode if there are equations available.
    public void startQuiz() {
        quizMode = true;
        if (!equations.isEmpty()) {
            currentQuizIndex = 0;
        } else {
            System.out.println("Memory Bank is empty."); // Notify if the memory bank is empty.
            exitQuiz();
        }
    }

    // Quits the quiz mode.
    public void exitQuiz() {
        quizMode = false;
    }

    // Getters and setters for tracking quiz state.
    public boolean isInQuizMode() {
        return quizMode;
    }

    public int getEquationsCount() {
        return equations.size();
    }

    public void incrementAttempted() {
        attempted++;
    }

    public void incrementCorrect() {
        correct++;
    }

    public int getAttempted() {
        return attempted;
    }

    public int getCorrect() {
        return correct;
    }

    // Checks if all questions have been answered.
    public boolean isQuizComplete() {
        return attempted >= equations.size();
    }
}
