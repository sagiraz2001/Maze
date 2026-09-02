package org.example;

import javax.swing.*;
import java.awt.*;

public class MazePanel extends JPanel {
    private MazeConfigManager mazeConfigManager;
    private boolean[][] maze;


    public MazePanel(MazeConfigManager mazeConfigManager) {
        this.mazeConfigManager = mazeConfigManager;
    }

    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics); // Clears the screen before drawing the new frame.

        // If the image is not Null - draw every cell of the maze according to the two-dim maze array values.
        if (this.maze != null) {
            // Calculates the available height and width for a single cell in the panel, chooses the minimum of them, and sets it as cellSize
            int cellHeight = this.getHeight() / this.maze.length;
            int cellWidth = this.getWidth() / this.maze[0].length;
            int cellSize = Math.min(cellWidth, cellHeight);

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
                    // Draw a grid if needed.
                    if (mazeConfigManager.isDrawGrid()) {
                        graphics.setColor(mazeConfigManager.getGridColorAsAColor());
                        graphics.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                    }
                }
            }
        }
    }

    public boolean[][] getMaze() {
        return maze;
    }

    /**
     * Updates a new maze structure.
     * Stores the 2D array representation of the maze and triggers a screen repaint
     * so the new maze is drawn immediately.
     */
    public void setMaze(boolean[][] newMaze) {
        this.maze = newMaze;
        this.repaint();// Screen refreshing, this automatically triggers paintComponent()
    }
}
