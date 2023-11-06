document.addEventListener('DOMContentLoaded', function () {
    let userEquation = '';
    let firstAttempt = true;
    let memoryBank = [];
    const memoryBankLimit = 10; // Maximum number of saved questions

    const screenMain = document.querySelector('.screen-main');
    const correctDisplay = document.getElementById('correct');
    const attemptedDisplay = document.getElementById('attempted');

    let correctCount = 0;
    let attemptedCount = 0;

    // Update the main screen
    function updateScreen(content) {
        screenMain.textContent = content;
    }
    

    // Show the home screen
    function showHomeScreen() {
        updateScreen('=');
    }

    document.getElementById('onButton').addEventListener('click', function() {
        showHomeScreen(); // Call the function that resets the display to "="
        // Other logic that needs to run when ON button is pressed, if any
    });

    document.getElementById('newButton').addEventListener('click', function() {
        showHomeScreen(); // Call the function that resets the display to "="
        // Other logic that needs to run when ON button is pressed, if any
    });

    // Make sure this line is inside the 'DOMContentLoaded' callback
    document.getElementById('memoryBankButton').addEventListener('click', function() {
    addToMemoryBank(userEquation);
});


    // Calculate the result of the equation with integer division
function calculateResult(firstNum, operator, secondNum) {
    switch (operator) {
        case '×':
            return firstNum * secondNum;
        case '÷':
            return {
                quotient: Math.floor(firstNum / secondNum), // Integer division
                remainder: firstNum % secondNum // Remainder
            };
        case '+':
            return firstNum + secondNum;
        case '−':
            return firstNum - secondNum;
        default:
            return null;
    }
}
function flashLights(duration, colors = ['#f00', 'transparent'], callback) {
    let lightElements = document.querySelectorAll('.light');
    let flashInterval = 250; // Time in milliseconds to switch colors, reduced for quicker flashing
    let currentColorIndex = 0;

    // Function to update light colors
    function updateLights() {
        lightElements.forEach(light => {
            light.style.backgroundColor = colors[currentColorIndex];
        });
        currentColorIndex = (currentColorIndex + 1) % colors.length;
    }

    // Start the flashing effect
    let flashEffect = setInterval(updateLights, flashInterval);

    // Stop the flashing effect after the specified duration and call the callback
    setTimeout(() => {
        clearInterval(flashEffect);
        // Reset lights to default color (assuming default is red)
        lightElements.forEach(light => {
            light.style.backgroundColor = '#f00'; // Reset to red
        });
        if (typeof callback === 'function') {
            callback(); // Call the callback function after flashing is done
        }
    }, duration);
}

// Evaluate the user's equation with integer division
function evaluateEquation(equation) {
    const match = equation.match(/^(-?\d+\.?\d*)([×÷+\-−])(-?\d+\.?\d*)=((-?\d+\.?\d*))$/);

    if (!match) return { isCorrect: false }; // Not a valid equation

    let firstNum = parseFloat(match[1]);
    const operator = match[2];
    let secondNum = parseFloat(match[3]);
    let userAnswer = parseFloat(match[4]);

    if (isNaN(firstNum) || isNaN(secondNum) || isNaN(userAnswer)) return { isCorrect: false };

    const result = calculateResult(firstNum, operator, secondNum);

    if (operator === '÷') {
        // For division, check if user's answer is the integer quotient
        const isCorrect = userAnswer === result.quotient;
        // Prepare the answer string to display correct quotient and remainder if the first attempt is wrong
        const answerString = result.remainder === 0 ? `${result.quotient}` : `${result.quotient}R${result.remainder}`;
        return {
            isCorrect: isCorrect,
            answerString, // Correct answer to display if needed
            firstNum,
            secondNum,
            operator
        };
    } else {
        // For other operations, proceed as normal
        return {
            isCorrect: userAnswer === result,
            firstNum,
            secondNum,
            operator
        };
    }
}

    // Add a flag to determine if we are typing a new answer on the second attempt
let typingNewAnswer = false;

document.querySelectorAll('.btn').forEach(function (button) {
    button.addEventListener('click', function () {
        const buttonValue = this.textContent;

        if (buttonValue === 'Enter') {
            if (userEquation.includes('=')) {
                const evaluation = evaluateEquation(userEquation);
        
                if (evaluation.isCorrect) {
                    correctCount++;
                    updateScreen(userEquation);
                    if (correctCount >= 10) {
                        // If there are 10 correct answers, flash lights for 10 seconds with different colors
                        flashLights(5000, ['#f00', '#0f0', '#00f', '#ff0', '#0ff', '#f0f'], function() {
                            correctCount = 0; // Reset the count after the flashing lights
                            attemptedCount = 0;
                            correctDisplay.textContent = correctCount; // Update the display after reset
                            attemptedDisplay.textContent = attemptedCount;
                        });
                    } else {
                        // Otherwise, flash lights for 3 seconds
                        flashLights(3000);
                    }
                    userEquation = ''; // Reset the equation after a correct answer
                } else {
                    if (firstAttempt) {
                        updateScreen(userEquation.split('=')[0] + '=');
                        // Set flag to true, indicating we are ready to type a new answer
                        typingNewAnswer = true;
                    } else {
                        let answer;
                        if (evaluation.operator === '÷') {
                            // Display only the quotient for user's answer, but show the complete answer with remainder if wrong twice
                            answer = firstAttempt ? evaluation.quotient : evaluation.answerString;
                        } else {
                            // For other operations, calculate the correct result
                            answer = calculateResult(evaluation.firstNum, evaluation.operator, evaluation.secondNum);
                        }
                        // Update the screen with the correct answer including the remainder if it's a division
                        updateScreen(`${evaluation.firstNum} ${evaluation.operator} ${evaluation.secondNum}=${answer}`);
                        userEquation = ''; // Reset the equation after showing the correct answer
                    }
                    firstAttempt = false;
                }
                 
                attemptedCount++;
                correctDisplay.textContent = correctCount;
                attemptedDisplay.textContent = attemptedCount;
                if (evaluation.isCorrect) firstAttempt = true; // Reset for the next new equation
            }
            const evaluation = evaluateEquation(userEquation);
        } else {
            if (!firstAttempt && userEquation.includes('=')) {
                let parts = userEquation.split('=');
                let currentAnswerPart = parts[1];
    
                // If we are typing a new answer, clear the previous answer
                if (typingNewAnswer) {
                    currentAnswerPart = '';
                    typingNewAnswer = false; // Reset the flag as we are now starting to type a new answer
                }

                // Append or overwrite the current answer
                if (buttonValue.match(/[0-9.]/)) {
                    userEquation = parts[0] + '=' + currentAnswerPart + buttonValue;
                } else if (buttonValue.match(/[\-+×÷]/)) {
                    // If it's an operator, start a new equation
                    userEquation = buttonValue;
                    firstAttempt = true;
                }
            } else {
                // For the first attempt or when no "=" is present, append the buttonValue
                userEquation += buttonValue;
            }
            updateScreen(userEquation);
        }
    });
});

// Add a function to save to the memory bank
// Add a function to save to the memory bank
function addToMemoryBank(equation) {
    if (memoryBank.length < memoryBankLimit) {
        memoryBank.push(equation);
        updateScreen('Saved to Memory'); // Feedback to the user
        setTimeout(showHomeScreen, 1000); // Show home screen after 2 seconds
        userEquation = ''; // Clear the userEquation after saving to memory
    } else {
        updateScreen('Memory Full');
    }
}

// Add a function to handle quizzing from the memory bank
function quizFromMemoryBank() {
    if (memoryBank.length > 0) {
        currentQuizIndex = 0;
        correctCount = 0;
        attemptedCount = 0;
        correctDisplay.textContent = correctCount;
        attemptedDisplay.textContent = attemptedCount;

// Function to display the next quiz question
function nextQuestion() {
    if (currentQuizIndex < memoryBank.length) {
        userEquation = ''; // Ensure no previous input affects the display.
        let questionToDisplay = memoryBank[currentQuizIndex] + '=';
        updateScreen(questionToDisplay);
        currentQuizIndex++;
    } else {
        // All questions answered, trigger light show
        flashLights(5000, ['#f00', '#0f0', '#00f', '#ff0', '#0ff', '#f0f']);
        memoryBank = []; // Reset memory bank
        attemptedCount = 0; // Reset attempted count
        correctCount = 0; // You might also want to reset correct count here
        correctDisplay.textContent = correctCount; // Update the correct count display
        attemptedDisplay.textContent = attemptedCount; // Update the attempted count display
        updateScreen('No more questions');
    }
}




        nextQuestion();  // Start the quiz by showing the first question

        // Override the 'Enter' button event handler within the scope of this quiz function
        document.getElementById('enterButton').onclick = function() {
            let userAnswer = screenMain.textContent.split('=')[1];
            let isCorrect = evaluateEquation(memoryBank[currentQuizIndex-1] + '=' + userAnswer).isCorrect;
            
            if (isCorrect) {
                correctCount++;
                correctDisplay.textContent = correctCount;
            }
            attemptedCount++;
            attemptedDisplay.textContent = attemptedCount;
            
            nextQuestion(); // Move to the next question regardless of whether the answer was correct
        };
    } else {
        updateScreen('No Questions');
    }
}


document.getElementById('goButton').addEventListener('click', function(event) {
    event.preventDefault(); // Prevent any default action that might occur
    userEquation = ''; 
    quizFromMemoryBank();
});

document.getElementById('offButton').addEventListener('click', function() {
    // Clear the display
    updateScreen('');

    // Reset calculator state
    userEquation = '';
    firstAttempt = true;
    correctCount = 0;
    attemptedCount = 0;
    memoryBank = [];
    typingNewAnswer = false;
    currentQuizIndex = 0; // Assuming you have this variable declared in a wider scope for quiz functionality

    // Update the counters on the screen
    correctDisplay.textContent = correctCount;
    attemptedDisplay.textContent = attemptedCount;

    // Disable any quiz or game mode that might be active
    document.getElementById('enterButton').onclick = null;
});
     

document.getElementById('clearButton').addEventListener('click', function() {
    // Clear the display
    updateScreen('=');
  
    // Reset the current equation and related flags
    userEquation = '';
    firstAttempt = true;
    typingNewAnswer = false;

});

});
