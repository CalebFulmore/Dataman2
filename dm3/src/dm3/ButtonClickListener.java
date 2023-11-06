package dm3;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class ButtonClickListener implements ActionListener {
    private JTextField mainDisplay;
    private AnswerChecker answerChecker;
    private boolean secondAttemptAllowed;
    private String lastOperation;
    private int lastDividend;
    private int lastDivisor;
    private JTextField correctDisplay;
    private JTextField attemptDisplay;
    private AtomicInteger correctCount;
    private AtomicInteger attemptCount;
    private GuiView guiView;
    private MemoryBank memoryBank;
    private boolean isNewProblem;


    public ButtonClickListener(JTextField mainDisplay, JTextField correctDisplay,
                               JTextField attemptDisplay, AtomicInteger correctCount,
                               AtomicInteger attemptCount, GuiView guiView, MemoryBank memoryBank) {
        this.mainDisplay = mainDisplay;
        this.correctDisplay = correctDisplay;
        this.attemptDisplay = attemptDisplay;
        this.correctCount = correctCount;
        this.attemptCount = attemptCount;
        this.guiView = guiView;
        this.answerChecker = new AnswerChecker();
        this.secondAttemptAllowed = true;
        this.memoryBank = memoryBank;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "On":
            case "New":
                prepareNewProblem();
                break;
            case "Enter":
                evaluateAnswer();
                break;
            case "Off":
                resetCountsAndDisplays();
                break;
            case "MemBank":
                memoryBank.addEquation(mainDisplay.getText()); // Save the current equation
                // Provide feedback to the user by updating one of the small displays
                mainDisplay.setText("Saved!");
                break;
            case "Go":
                memoryBank.startQuiz(); 
                displayNextQuizQuestion();
                correctCount.set(0);
                attemptCount.set(0);
                correctDisplay.setText("0");
                attemptDisplay.setText("0");
                break;
            default:
                appendToDisplay(command);
                break;
        }
    }
    
    private void displayNextQuizQuestion() {
    	isNewProblem = true;
        String question = memoryBank.getNextQuizQuestion();
        if (question != null) {
            mainDisplay.setText(question + " =");
            attemptDisplay.setText(String.valueOf(memoryBank.getAttempted()));
            correctDisplay.setText(String.valueOf(memoryBank.getCorrect()));
        } else {
            mainDisplay.setText("No more questions left"); // Method to call to update GUI for end of quiz
        }
    }


    private void prepareNewProblem() {
        mainDisplay.setText("=");
        secondAttemptAllowed = true;
        lastOperation = null;
        answerChecker.resetDivisionAttempts(); // Call the reset method here
        isNewProblem = true; // Indicate that this is a new problem
    }


    private void resetCountsAndDisplays() {
        correctCount.set(0);
        attemptCount.set(0);
        correctDisplay.setText("0");
        attemptDisplay.setText("0");
        mainDisplay.setText("");
    }

    private void appendToDisplay(String command) {
        if ("=".equals(mainDisplay.getText())) {
            mainDisplay.setText("");
        }
        mainDisplay.setText(mainDisplay.getText() + command);
    }
    
    private String getCorrectAnswer(String operation, int dividend, int divisor) {
        switch (operation) {
            case "÷":
                int correctQuotient = dividend / divisor;
                int remainder = dividend % divisor;
                return correctQuotient + (remainder > 0 ? " R " + remainder : "");
            case "+":
                return String.valueOf(dividend + divisor);
            case "-":
                return String.valueOf(dividend - divisor);
            case "×":
                return String.valueOf(dividend * divisor);
            default:
                return "";
        }
    }

 // Evaluates the user's answer for correctness.
    private void evaluateAnswer() {
        String displayText = mainDisplay.getText();
        // Check if the displayText contains an equation to evaluate.
        if (displayText.contains("=")) {
            // Split the equation into its components.
            String[] parts = displayText.split("[÷×\\-+]|=");
            // Ensure the equation has two operands and an answer.
            if (parts.length == 3) {
                try {
                    int firstNumber = Integer.parseInt(parts[0].trim());
                    int secondNumber = Integer.parseInt(parts[1].trim());
                    int userAnswer = Integer.parseInt(parts[2].trim());
                    boolean isCorrect = false;

                    // Capture the operation symbol (+, -, ×, ÷)
                    String operationSymbol = displayText.replaceAll("[^÷×\\-+]", "");

                    // Determine the operation and set the last operation details
                    if (displayText.contains("÷")) {
                        lastOperation = "÷";
                        lastDividend = firstNumber;
                        lastDivisor = secondNumber;
                    } else if (displayText.contains("+")) {
                        lastOperation = "+";
                    } else if (displayText.contains("-")) {
                        lastOperation = "-";
                    } else if (displayText.contains("×")) {
                        lastOperation = "×";
                    }
                    
                 // Use AnswerChecker to determine if the answer is correct
                    String result;
                    switch (lastOperation) {
                        case "÷":
                            result = answerChecker.checkDivisionAnswer(firstNumber, secondNumber, userAnswer);
                            break;
                        case "+":
                            result = answerChecker.checkAdditionAnswer(firstNumber, secondNumber, userAnswer);
                            break;
                        case "-":
                            result = answerChecker.checkSubtractionAnswer(firstNumber, secondNumber, userAnswer);
                            break;
                        case "×":
                            result = answerChecker.checkMultiplicationAnswer(firstNumber, secondNumber, userAnswer);
                            break;
                        default:
                            result = "Error: Invalid Operation";
                            break;
                    }

                    isCorrect = result.startsWith("Correct");
                    
                    if (isNewProblem) {
                        isNewProblem = false; // No longer a new problem after the first attempt
                    }

                    // Increment attempt count for every new attempt made
                    if (!memoryBank.isInQuizMode() || !secondAttemptAllowed) {
                        attemptCount.incrementAndGet();
                    }
                    
                    if (isNewProblem || (memoryBank.isInQuizMode() && secondAttemptAllowed)) {
                        attemptCount.incrementAndGet();
                    }

                    if (isCorrect) {
                        correctCount.incrementAndGet(); // Increment correct count only if the answer is correct
                        guiView.flashLight(Color.GREEN, 500, 2000); // Flash green light only if correct
                        
                        // In non-quiz mode, check for multi-color flash conditions after incrementing the correct count
                        if (!memoryBank.isInQuizMode() && correctCount.get() % 10 == 0) {
                            guiView.flashMultiColor(5000);
                        }
                        
                        if (memoryBank.isInQuizMode()) {
                            // If the answer is correct in quiz mode, move to the next question
                            displayNextQuizQuestion();
                            secondAttemptAllowed = true; // Reset for the next question
                        } else {
                            // If the answer is correct in regular mode, just reset for the next problem
                            prepareNewProblem();
                        }
                    } else {
                        if (memoryBank.isInQuizMode()) {
                            if (secondAttemptAllowed) {
                                // User gets one more chance
                                secondAttemptAllowed = false; // User is now on their second attempt
                                mainDisplay.setText(parts[0] + " " + operationSymbol + " " + parts[1] + " = ");
                            } else {
                                // Move to the next question after the second failed attempt in quiz mode
                                displayNextQuizQuestion();
                                secondAttemptAllowed = true; // Reset for the next question
                            }
                        } else {
                            if (secondAttemptAllowed) {
                                // Give a second chance in regular mode
                                secondAttemptAllowed = false;
                                mainDisplay.setText(parts[0] + " " + operationSymbol + " " + parts[1] + " = ");
                            } else {
                                // Second attempt also wrong in regular mode, show the correct answer
                                String correctAnswer = getCorrectAnswer(lastOperation, firstNumber, secondNumber);
                                mainDisplay.setText(parts[0] + " " + operationSymbol + " " + parts[1] + "=" + correctAnswer);
                                secondAttemptAllowed = true; // Reset for the next problem
                            }
                        }
                    }


                    // Update the displays regardless of the quiz mode or the correctness of the answer
                    correctDisplay.setText(String.valueOf(correctCount.get()));
                    attemptDisplay.setText(String.valueOf(attemptCount.get()));

                    // Flash multi-color lights if the quiz is complete in quiz mode
                    if (memoryBank.isInQuizMode() && memoryBank.isQuizComplete()) {
                        guiView.flashMultiColor(5000);
                    }

                } catch (NumberFormatException ex) {
                    mainDisplay.setText("Error: Invalid Input");
                }
            } else {
                mainDisplay.setText("Error: Incomplete Equation");
            }
        }
    }


}
