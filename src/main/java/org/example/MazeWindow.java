package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

public class MazeWindow extends JFrame {
    private MazeConfigManager mazeConfigManager;
    private JTextField mazeHeight;
    private JTextField mazeWidth;
    private JButton getMazeButton;
    private JButton refreshConfigButton;
    private MazePanel mazePanel;

    public MazeWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setResizable(false);
        this.setTitle("Maze");
        this.setLocationRelativeTo(null);

        // Initialize the logic manager and UI components (buttons, text fields)
        this.mazeConfigManager = new MazeConfigManager();
        this.mazeHeight = new JTextField("30", 5);
        this.mazeWidth = new JTextField("30", 5);
        this.getMazeButton = new JButton("GET MAZE");
        this.refreshConfigButton = new JButton("Refresh config");

        // Create a top panel for the controls and add it to the window
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Height"));
        controlPanel.add(this.mazeHeight);
        controlPanel.add(new JLabel("Width: "));
        controlPanel.add(this.mazeWidth);
        controlPanel.add(this.getMazeButton);
        controlPanel.add(this.refreshConfigButton);
        this.add(controlPanel, BorderLayout.NORTH);

        // Create the drawing panel and put it in the center of the screen
        this.mazePanel = new MazePanel(this.mazeConfigManager);
        this.add(mazePanel, BorderLayout.CENTER);

        this.getMazeButton.addActionListener(event -> {
            generateMaze();
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
            String urlString = "https://backend-qcf9.onrender.com/fm1/get-maze-image?width=" + width + "&height=" + height;
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
            for (int i = 0; i < height; i++) {// Check all image's pixel and determine if it's passage or wall, then, generate the maze
                for (int j = 0; j < width; j++) {
                    int pixelColor = mazeImage.getRGB(j, i);
                    Color color = new Color(pixelColor);
                    if (color.getRed() + color.getGreen() + color.getBlue() < 255 * 3) {
                        rawMap[i][j] = false;
                    } else {
                        rawMap[i][j] = true;
                    }
                }
            }
        }
        this.mazePanel.setMaze(rawMap);
    }


}
