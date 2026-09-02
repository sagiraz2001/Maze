package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MazeWindow extends JFrame {
    private MazeConfigManager mazeConfigManager;
    private JTextField mazeHeight;
    private JTextField mazeWidth;
    private JButton getMazeButton;
    private JButton refreshConfigButton;
    private JButton checkSolutionButton;
    private MazePanel mazePanel;

    public MazeWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1500, 1500);
        this.setResizable(false);
        this.setTitle("Maze");
        this.setLocationRelativeTo(null);

        // Initialize the logic manager and UI components (buttons, text fields)
        this.mazeConfigManager = new MazeConfigManager();
        this.mazeHeight = new JTextField("30", 5);
        this.mazeWidth = new JTextField("30", 5);
        this.getMazeButton = new JButton("GET MAZE");
        this.refreshConfigButton = new JButton("Refresh config");
        this.checkSolutionButton = new JButton("Check solution");

        // Create a top panel for the controls and add it to the window
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Height"));
        controlPanel.add(this.mazeHeight);
        controlPanel.add(new JLabel("Width: "));
        controlPanel.add(this.mazeWidth);
        controlPanel.add(this.getMazeButton);
        controlPanel.add(this.refreshConfigButton);
        controlPanel.add(this.checkSolutionButton);
        this.add(controlPanel, BorderLayout.NORTH);

        // Create the drawing panel and put it in the center of the screen
        this.mazePanel = new MazePanel(this.mazeConfigManager);
        this.mazeConfigManager.fetchConfigFromServer(); // adding a colors for start
        this.add(mazePanel, BorderLayout.CENTER);

        this.getMazeButton.addActionListener(event -> {
            generateMaze();
        });

        this.refreshConfigButton.addActionListener(event -> {
            refreshConfig();

        });

        this.checkSolutionButton.addActionListener(event -> {
            checkSolution();
        });

        this.setVisible(true);
    }

    // A function that checks for valid values for maze Width and Height and return default if not.
    private int getValidMazeSize(String text) {
        try {
            int textToInt = Integer.parseInt(text);
            if (textToInt >= 5 && textToInt <= 100) {
                return textToInt;
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid input! Falling back to default size 30");
        }
        return 30;
    }

    /**
     * Fetches the maze image from the API based on user input,
     * decodes its pixels into a boolean matrix (white=passage, other=wall),
     * and passes the data to the MazePanel for rendering.
     */
    public void generateMaze() {
        // Checking the user input for the width & height
        int width = getValidMazeSize(this.mazeWidth.getText());
        int height = getValidMazeSize(this.mazeHeight.getText());
        BufferedImage mazeImage = null;
        try {// Trying to load maze image and throw exception if encounter with an error
            String urlString = "https://shaitest-production-3066.up.railway.app/fm1/get-maze-image?width=" + width + "&height=" + height;
            URL url = new URL(urlString);
            mazeImage = ImageIO.read(url);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid input! Please insert numbers only");
            exception.printStackTrace();
        } catch (MalformedURLException exception) {
            System.out.println("Error: Invalid URL format constructed.");
            exception.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Error downloading the image.");
            exception.printStackTrace();
        }

        boolean[][] rawMap = new boolean[height][width];
        if (mazeImage != null) {
            // Step calculation - calculating how many pixels each cell in the source maze contains
            int stepX = mazeImage.getWidth() / width;
            int stepY = mazeImage.getHeight() / height;
            for (int i = 0; i < height; i++) {// Check all image's pixel and determine if it's passage or wall, then, generate the maze
                for (int j = 0; j < width; j++) {
                    // Saving the color of the center pixel in each cell in the source maze to check whether it is white or another color
                    int pixelX = (j * stepX) + (stepX / 2);
                    int pixelY = (i * stepY) + (stepY / 2);
                    int pixelColor = mazeImage.getRGB(pixelX, pixelY);
                    Color color = new Color(pixelColor);
                    // If the center pixel is white - we place true, if is other color - we place false
                    if (color.getRed() + color.getGreen() + color.getBlue() < 255 * 3) {
                        rawMap[i][j] = false;
                    } else {
                        rawMap[i][j] = true;
                    }
                }
            }
        } else { // If image was not loaded successfully, return from this function
            return;
        }
        this.mazePanel.setMaze(rawMap);
    }

    // Refreshing the maze configuration
    public void refreshConfig() {
        this.mazeConfigManager.fetchConfigFromServer();
        this.repaint();
    }


    public List<Point> findSolutionPath() {
        return null;////Need to continue this function
    }

    public void checkSolution() {
        // Disable the button to avoid mismatch during solution
        this.checkSolutionButton.setEnabled(false);

        if (this.mazePanel.getMaze() == null) { // Showing error pop-up when the user click this button before generate a maze
            JOptionPane.showMessageDialog(this, "Please generate the maze first");
            this.checkSolutionButton.setEnabled(true);
            return;
        }
        ////need to continue with BFS ALG later

        Thread thread = new Thread(() -> {
            ////need to continue with Animation  later
        });
        thread.start();
    }


}
