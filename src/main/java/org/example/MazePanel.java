package org.example;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private MazeConfigManager mazeConfigManager;
    private boolean[][] maze;


    public MazePanel(MazeConfigManager mazeConfigManager) {
        this.mazeConfigManager = mazeConfigManager;
    }

    /**
     * Updates a new maze structure.
     * Stores the 2D array representation of the maze and triggers a screen repaint
     * so the new maze is drawn immediately.
     */
    public void setMaze(boolean[][] newMaze) {
        this.maze = newMaze;
        this.repaint();// Screen refreshing, this automatically triggers paintComponent()
        this.repaint();
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics); // Clears the screen before drawing the new frame.

        if (this.maze != null) {

        }
    }
}
