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

        // If the image is not Null - draw every cell of the maze according to the two-dim maze array values, and draw a grid if needed.
        if (this.maze != null) {
            int cellSize = 20;
            int height = this.maze.length;
            int width = this.maze[0].length;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (!this.maze[i][j]) {
                        graphics.setColor(mazeConfigManager.getWallCellColorAsColor());
                    } else {
                        graphics.setColor(Color.WHITE);
                    }
                    graphics.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);

                    if (mazeConfigManager.isDrawGrid()) {
                        graphics.setColor(mazeConfigManager.getGridColorAsAColor());
                        graphics.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    }
                }
            }
        }
    }
}
