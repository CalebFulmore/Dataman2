package dm3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiView extends JFrame {
    private JTextField mainDisplay;
    private JTextField smallDisplay1;
    private JTextField smallDisplay2;
    private JTextField smallDisplay3;
    private JButton light;
 // Use AtomicIntegers to handle the counts safely in case of multi-threading
    private AtomicInteger correctCount = new AtomicInteger(0);
    private AtomicInteger attemptCount = new AtomicInteger(0);
    private MemoryBank memoryBank;


    public GuiView() { // Instantiate the MemoryBank
    	memoryBank = new MemoryBank();
    	
        createGUI();
    }
    
    

    private void createGUI() {
        setTitle("Dataman");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel displayPanel = new JPanel(new GridLayout(5, 1));
        mainDisplay = new JTextField();
        mainDisplay.setEditable(false);
        mainDisplay.setHorizontalAlignment(JTextField.RIGHT);
        mainDisplay.setFont(new Font("Arial", Font.BOLD, 24)); // Set a larger font for the main display
        displayPanel.add(mainDisplay);

        // Small displays and labels
        smallDisplay1 = new JTextField(10);
        smallDisplay1.setEditable(false);
        smallDisplay2 = new JTextField(10);
        smallDisplay2.setEditable(false);
        smallDisplay3 = new JTextField(10);
        smallDisplay3.setEditable(false);

        JPanel smallDisplays = new JPanel(new GridLayout(2, 3)); // Use 2 rows to include labels
        smallDisplays.add(smallDisplay1);
        smallDisplays.add(smallDisplay2);
        smallDisplays.add(smallDisplay3);

        // Labels for the small displays
        JLabel labelCorrect = new JLabel("Correct");
        labelCorrect.setHorizontalAlignment(JLabel.CENTER);
        JLabel labelAttempted = new JLabel("Attempted");
        labelAttempted.setHorizontalAlignment(JLabel.CENTER);
        JLabel labelTime = new JLabel("Time");
        labelTime.setHorizontalAlignment(JLabel.CENTER);

        smallDisplays.add(labelCorrect);
        smallDisplays.add(labelAttempted);
        smallDisplays.add(labelTime);

        displayPanel.add(smallDisplays);

     // Buttons arranged in a grid to resemble a standard calculator.
        JPanel buttonPanel = new JPanel(new GridLayout(6, 4, 5, 5)); // Increase grid rows for better layout

        String[] buttons = {
            // Function buttons at the top
            "On", "Off", "New", "MemBank",
            // Number pad and basic operations
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", "C", "=", "+",
            // Additional function buttons at the bottom
            "Enter", "Clear", "Number Guesser", "Go"
        };

    
        for (String b : buttons) {
            JButton button = new JButton(b);
            button.addActionListener(new ButtonClickListener(mainDisplay, smallDisplay1,
                    smallDisplay2, correctCount, attemptCount, this, memoryBank)); // Pass the memoryBank
            buttonPanel.add(button);
        }

        // Light
        light = new JButton();
        light.setBackground(Color.RED);
        light.setEnabled(false);

        // Layout setup
        add(displayPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(light, BorderLayout.SOUTH);

        setSize(400, 600);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }
    
    private Timer flashTimer;

    public void flashLight(Color color, int delay, int duration) {
        ActionListener flashAction = new ActionListener() {
            private int count = 0;
            private boolean on = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count * delay >= duration) {
                    ((Timer) e.getSource()).stop();
                    light.setBackground(Color.RED); // Reset to default color after flashing
                    light.setOpaque(true);
                } else {
                    on = !on; // Toggle the light
                    light.setBackground(on ? color : Color.RED);
                    light.setOpaque(on);
                }
                count++;
            }
        };
        if (flashTimer != null && flashTimer.isRunning()) {
            flashTimer.stop();
        }
        flashTimer = new Timer(delay, flashAction);
        flashTimer.start();
    }

    public void flashMultiColor(int duration) {
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE}; // Colors to cycle through
        AtomicInteger colorIndex = new AtomicInteger(0);
        Timer multiColorTimer = new Timer(500, e -> {
            if (colorIndex.get() * 500 >= duration) {
                ((Timer) e.getSource()).stop();
                light.setBackground(Color.RED); // Reset to default color after flashing
                light.setOpaque(true);
            } else {
                light.setBackground(colors[colorIndex.getAndIncrement() % colors.length]);
                light.setOpaque(true);
            }
        });
        multiColorTimer.start();
    }


    public static void main(String[] args) {
        // Run the GUI construction in the Event-Dispatching thread for thread-safety.
        SwingUtilities.invokeLater(new GuiView()::createGUI);
    }


}
