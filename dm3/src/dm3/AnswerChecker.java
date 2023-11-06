package dm3;

// Provides methods to check arithmetic answers and manage attempt tracking.
public class AnswerChecker {
    private int divisionAttempts;

    public AnswerChecker() {
        divisionAttempts = 0;
    }

    // Checks if the division answer is correct and manages division attempt count.
    public String checkDivisionAnswer(int dividend, int divisor, int userQuotient) {
        int correctQuotient = dividend / divisor;
        int remainder = dividend % divisor;
        int userRemainder = (userQuotient * divisor) % dividend;

        if (userQuotient == correctQuotient || (userQuotient == correctQuotient + 1 && userRemainder >= divisor / 2)) {
            divisionAttempts = 0; // Reset attempts on correct answer.
            return "Correct";
        } else {
            divisionAttempts++;
            if (divisionAttempts >= 2) {
                divisionAttempts = 0; // Reset attempts after two tries.
                return correctQuotient + " R " + remainder; // Provide the correct answer.
            } else {
                return "Incorrect, try again!"; // Prompt another attempt.
            }
        }
    }

    // Checks if the addition answer is correct.
    public String checkAdditionAnswer(int addend1, int addend2, int userSum) {
        return userSum == addend1 + addend2 ? "Correct" : "Incorrect, try again!";
    }

    // Checks if the subtraction answer is correct.
    public String checkSubtractionAnswer(int minuend, int subtrahend, int userDifference) {
        return userDifference == minuend - subtrahend ? "Correct" : "Incorrect, try again!";
    }

    // Checks if the multiplication answer is correct.
    public String checkMultiplicationAnswer(int multiplicand, int multiplier, int userProduct) {
        return userProduct == multiplicand * multiplier ? "Correct" : "Incorrect, try again!";
    }

    // Resets the division attempts counter.
    public void resetDivisionAttempts() {
        divisionAttempts = 0;
    }
}