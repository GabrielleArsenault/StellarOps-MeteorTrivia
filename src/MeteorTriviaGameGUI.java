import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeteorTriviaGameGUI {
    private static JFrame frame;
    private static JPanel panel;
    private static JLabel questionLabel;
    private static JTextField answerField;
    private static JButton submitButton;
    private static JLabel scoreLabel;

    private static String[] questions = {
        "Does water regulate the Earth’s temperature",
        "You can live without water longer than live without food.",
        "Only 3.5% of the water on Earth is drinkable.",
        "You can drink salt water from seas and oceans.",
        "Water in solid form (frozen) is lighter than water in liquid form.",
        "Human brains are 75% water.",
        "A small drip from a faucet can waste as much as 75 litres of of water a day.",
        "The standards for safe and usable water is the same around the world.",
        "Extreme heat can cause cramps, swelling and fainting.",
        "Rural areas experience higher temperatures during the summer.",
        "Heatwaves can cause blackouts and power outages.",
        "Heatwaves can can cause damage to the environment.",
        "Heatwaves do not affect the air quality.",
        "One inch of flood water can destroy a home or business.",
        "The ocean absorbs most of the heat we produce.",
        "Urbanization can lead to greater poverty.",
        "Urbanization can lead to increased risk of flash flooding.",
        "Coastal regions are less urbanized then other regions.",
        "Trees can help fight droughts.",
        "Droughts caused the collapse of ancient civilization."
    };

    private static String[] answers = {
        "True",
        "False",
        "True",
        "False",
        "True",
        "True",
        "True",
        "False",
        "True",
        "False",
        "True",
        "True",
        "False",
        "True",
        "True",
        "True",
        "True",
        "False",
        "True",
        "True"
    };

    private static int score = 0;
    private static int questionIndex = 0;

    private static Random random = new Random();

    // Explosion panel
    private static ExplosionPanel explosionPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MeteorTriviaGameGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Set up the frame
        frame = new JFrame("Meteor Trivia Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Center the window

        // Create the main panel
        panel = new JPanel();   
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Question label (initialize with the first question)
        questionLabel = new JLabel(questions[questionIndex], JLabel.CENTER);
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(questionLabel);

        // Answer text field
        answerField = new JTextField(20);
        panel.add(answerField);

        // Submit button
        submitButton = new JButton("Submit Answer");
        submitButton.addActionListener(new AnswerButtonListener());
        panel.add(submitButton);

        // Score label
        scoreLabel = new JLabel("Score: " + score, JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(scoreLabel);

        // Create explosion panel
        explosionPanel = new ExplosionPanel();
        panel.add(explosionPanel);

        // Add the panel to the frame and make it visible
        frame.add(panel);
        frame.setVisible(true);
    }

    private static class AnswerButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userAnswer = answerField.getText().trim();

            if (userAnswer.equalsIgnoreCase(answers[questionIndex])) {
                score++;
                scoreLabel.setText("Score: " + score);
                showExplosion();
            } else {
                questionLabel.setText("Incorrect! Try again.");
            }

            // Move to next question
            questionIndex++;
            if (questionIndex < questions.length) {
                questionLabel.setText(questions[questionIndex]);
            } else {
                questionLabel.setText("Game Over! Final Score: " + score);
                answerField.setEnabled(false);
                submitButton.setEnabled(false);
            }

            // Clear answer field after submission
            answerField.setText("");
        }
    }

    private static void showExplosion() {
        explosionPanel.startExplosion();
    }

    // Custom JPanel to handle explosion drawing
    static class ExplosionPanel extends JPanel {
        private static final int EXPLOSION_DURATION = 2000; // Duration of the explosion in milliseconds
        private List<Particle> particles;
        private Timer explosionTimer;

        public ExplosionPanel() {
            setPreferredSize(new Dimension(500, 200));
            setBackground(Color.BLACK);
            particles = new ArrayList<>();
        }

        // Starts the explosion animation
        public void startExplosion() {
            // Clear old particles and start new explosion
            particles.clear();

            // Create new particles for explosion
            for (int i = 0; i < 50; i++) {
                particles.add(new Particle());
            }

            // Start the animation with a timer
            explosionTimer = new Timer(30, new ActionListener() {
                private long startTime = System.currentTimeMillis();

                @Override
                public void actionPerformed(ActionEvent e) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = (float) elapsed / EXPLOSION_DURATION;

                    // Update particles
                    for (Particle p : particles) {
                        p.update(progress);
                    }

                    if (progress >= 1.0f) {
                        explosionTimer.stop();
                    }

                    // Repaint the panel to show the explosion
                    repaint();
                }
            });

            explosionTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Set graphics for the explosion
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw all particles
            for (Particle p : particles) {
                p.draw(g2d);
            }
        }

        // Particle class to simulate explosion
        class Particle {
            private float x, y;
            private float speedX, speedY;
            private float size;
            private float alpha;
            private Color color;

            public Particle() {
                // Random starting position near the center of the panel
                x = getWidth() / 2;
                y = getHeight() / 2;

                // Random direction and speed
                double angle = random.nextDouble() * 2 * Math.PI;
                float speed = 1 + random.nextFloat() * 3; // Random speed between 1 and 4
                speedX = (float) (speed * Math.cos(angle));
                speedY = (float) (speed * Math.sin(angle));

                // Random size
                size = random.nextFloat() * 10 + 5;

                // Random color for fiery effect
                color = new Color(random.nextInt(256), random.nextInt(100) + 155, random.nextInt(100) + 155);

                // Initial alpha (opacity)
                alpha = 1.0f;
            }

            // Update particle's position, size, and alpha based on explosion progress
            public void update(float progress) {
                x += speedX;
                y += speedY;

                // Fade out and shrink the particle
                alpha = Math.max(1.0f - progress, 0f);
                size = Math.max(size - progress * 0.5f, 1f);
            }

            // Draw the particle
            public void draw(Graphics2D g2d) {
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
                g2d.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
            }
        }
    }
}
