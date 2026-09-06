package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Queue;

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
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
            System.out.println("Invalid input! set default size to 30");
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
                    // If the center pixel is white - we place true (passage), if is other color - we place false (wall)
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
        this.mazePanel.clearPath();
        this.mazeConfigManager.fetchConfigFromServer();
        this.repaint();
    }


    public List<Point> findSolutionPath() {
        boolean[][] maze = this.mazePanel.getMaze();
        int height = maze.length;
        int width = maze[0].length;
        // If the first square or if the last square is false (wall), return null - there is no solution
        if (!maze[0][0] || !maze[height - 1][width - 1]) {
            return null;
        }
        // Queue which manage the squares we will check according to order of arrival (FIFO)
        Queue<Point> queue = new LinkedList<>();
        // Double array which save the squares we already visited to avoid re-visit
        boolean[][] visited = new boolean[height][width];
        // Double array which save from which square we arrive to the current square to restore route
        Point[][] parent = new Point[height][width];
        // Add the first square to the queue and mark it as visited
        queue.add(new Point(0, 0));
        visited[0][0] = true;

        // Creating arrays of the change in the coordination change for every direction (X and Y) to check neighbors
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        while (!queue.isEmpty()) {
            Point currentSquare = queue.poll();
            // If the current square is the last one, we finished the search
            if (currentSquare.x == width - 1 && currentSquare.y == height - 1) {
                break;
            }
            // Loops which chek the neighbors of the currest square in all 4 directions (up, down, left, right)
            for (int i = 0; i <= 3; i++) {
                int neighborSquareX = currentSquare.x + dx[i];
                int neighborSquareY = currentSquare.y + dy[i];
                // Check if the neighbor is inside the maze, is it a clear passage, and have we not visited it yet
                if (neighborSquareX >= 0 && neighborSquareX < width && neighborSquareY >= 0 && neighborSquareY < height && maze[neighborSquareY][neighborSquareX] && !visited[neighborSquareY][neighborSquareX]) {
                    // We mark the neighbor as visited
                    visited[neighborSquareY][neighborSquareX] = true;
                    // We save the "parent" of the neighbor
                    parent[neighborSquareY][neighborSquareX] = currentSquare;
                    // We add the neighbor to the queue so that we will check his neighbors later
                    queue.add(new Point(neighborSquareX, neighborSquareY));
                }
            }
        }

        // If we did not reach to the last index in the parents array, there is no solution
        if (parent[height - 1][width - 1] == null) {
            return null;
        }

        ArrayList<Point> finalPath = new ArrayList<>();
        // We will start from the last square
        Point step = new Point(width - 1, height - 1);
        // Add the current step to the final path list and jump to the current step's parent
        while (step != null) {
            finalPath.add(step);
            step = parent[step.y][step.x];
        }
        // Reverse the list
        Collections.reverse(finalPath);
        return finalPath;
    }

    private void animateSolutionPath(List<Point> path) {
        // Fade the irrelevant buttons & fields during animation draw
        this.mazeWidth.setEnabled(false);
        this.mazeHeight.setEnabled(false);
        this.getMazeButton.setEnabled(false);
        this.refreshConfigButton.setEnabled(false);
        for (Point point : path) {
            this.mazePanel.addPointToPath(point);
            // Animation delay
            try {
                Thread.sleep(this.mazeConfigManager.getAnimationDelayMS());
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, "The maze was successfully solved");
        // Return the button to be clicked
        //unfaded all buttons & fields
        this.mazeWidth.setEnabled(true);
        this.mazeHeight.setEnabled(true);
        this.checkSolutionButton.setEnabled(true);
        this.getMazeButton.setEnabled(true);
        this.refreshConfigButton.setEnabled(true);
    }


    public void checkSolution() {
        // Disable the button to avoid mismatch during solution
        this.checkSolutionButton.setEnabled(false);

        if (this.mazePanel.getMaze() == null) { // Showing error pop-up when the user click this button before generate a maze
            JOptionPane.showMessageDialog(this, "Please generate the maze first");
            this.checkSolutionButton.setEnabled(true);
            return;
        }
        // Clear the old path solution from the screen if there was one
        this.mazePanel.clearPath();
        List<Point> solutionPath = findSolutionPath();
        // Check if there is solution and raise an error if isn't
        if (solutionPath == null || solutionPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No solution found");
            this.checkSolutionButton.setEnabled(true);
            return;
        }
        //If there is solution, draw the solution solutionPath
        Thread thread = new Thread(() -> {
            animateSolutionPath(solutionPath);
        });
        thread.start();
    }
}

